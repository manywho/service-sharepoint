package com.manywho.services.sharepoint.auth.authorization;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.sdk.services.types.system.AuthorizationAttribute;
import com.manywho.sdk.services.types.system.AuthorizationGroup;
import com.manywho.sdk.services.types.system.AuthorizationUser;
import com.manywho.sdk.services.utils.Streams;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.auth.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;
import com.manywho.services.sharepoint.types.Group;
import com.microsoft.aad.adal4j.AuthenticationResult;
import java.util.List;
import java.util.stream.Collectors;
import static com.manywho.services.sharepoint.AppConfiguration.ODATA_TYPE;

public class AuthorizationManager {
    private final ConfigurationParser configurationParser;
    private final AppConfiguration configuration;
    private final TypeBuilder typeBuilder;
    private SharepointFacadeFactory sharepointFacadeFactory;
    private AzureHttpClient azureHttpClient;

    @Inject
    public AuthorizationManager(ConfigurationParser configurationParser, TypeBuilder typeBuilder,
                                AppConfiguration configuration, SharepointFacadeFactory sharepointFacadeFactory,
                                AzureHttpClient azureHttpClient) {

        this.configuration = configuration;
        this.configurationParser = configurationParser;
        this.typeBuilder = typeBuilder;
        this.sharepointFacadeFactory = sharepointFacadeFactory;
        this.azureHttpClient = azureHttpClient;

    }

    public ObjectDataResponse authorization(AuthenticatedWho authenticatedWho, ObjectDataRequest request) {
        ServiceConfiguration serviceConfiguration = configurationParser.from(request);


        String status;
        $User user = new $User();
        user.setUserId("PUBLIC_USER");
        user.setDirectoryId("Sharepoint");
        user.setDirectoryName("Sharepoint");
        user.setAuthenticationType(AuthorizationType.Oauth2);
        user.setLoginUrl(configuration.getAuthorizationUrl());


        switch (request.getAuthorization().getGlobalAuthenticationType()) {
            case AllUsers:
                // If it's a public user (i.e. not logged in) then return a 401
                if (authenticatedWho.getUserId().equals("PUBLIC_USER")) {
                    user.setUserId(authenticatedWho.getUserId());
                    status = "401";
                } else {
                    user.setUserId(authenticatedWho.getUserId());
                    status = "200";
                }

                break;
            case Public:
                status = "200";
                break;
            case Specified:

                String userId = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider())
                        .getUserId(serviceConfiguration, authenticatedWho.getToken());

                if (authenticatedWho.getUserId().equals("PUBLIC_USER")) {
                    status = "401";
                    break;
                }

                if (Strings.isNullOrEmpty(userId)) {
                    status = "401";
                    break;
                }

                // We need to check if the authenticated user is one of the authorized users by ID
                if (request.getAuthorization().hasUsers()) {
                    boolean isAuthorized = request.getAuthorization().getUsers().stream()
                            .anyMatch(u -> u.getAuthenticationId().equals(userId));

                    if (isAuthorized) {
                        status = "200";
                    } else {
                        status = "401";
                    }

                    break;
                }

//              // We need to check if the authenticated user is a member of one of the given groups, by group ID
              if (request.getAuthorization().hasGroups()) {
                  List<Group> groups = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider())
                          .fetchGroups(serviceConfiguration, authenticatedWho.getToken(), null);

                    // If the user is a member of no groups, then they're automatically not authorized
                    if (groups == null) {
                        status = "401";
                        break;
                    }

                    List<com.manywho.sdk.api.run.elements.config.Group> authorizedGroups = request.getAuthorization().getGroups();

                    boolean isAuthorized = Streams.asStream(groups)
                            .anyMatch(group -> authorizedGroups.stream().anyMatch(g -> g.getAuthenticationId().equals(group.getId())));

                    if (isAuthorized) {
                        status = "200";
                    } else {
                        status = "401";
                    }

                    break;
                }
            default:
                status = "401";
                break;
        }

        user.setStatus(status);

        return new ObjectDataResponse(typeBuilder.from(user));
    }

    public ObjectDataResponse groupAttributes() {
        return new ObjectDataResponse(
                typeBuilder.from(new AuthorizationAttribute("member", "Member"))
        );
    }

    public ObjectDataResponse groups(ObjectDataRequest request) {



        ServiceConfiguration configuration = configurationParser.from(request);

        AuthenticationResult authenticationResult = null;
        try {
            authenticationResult = azureHttpClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Not possible to get credentials");
        }

        SharePointFacadeInterface sharePointFacadeInterface = sharepointFacadeFactory.get(ODATA_TYPE);


        // Build the required AuthorizationGroup objects out of the groups that Okta tells us about
        List<AuthorizationGroup> groups = Streams.asStream(sharePointFacadeInterface.fetchGroups(configuration, authenticationResult.getAccessToken(), null).iterator())
                .map(group -> new AuthorizationGroup(group.getId(), group.getDisplayName(), group.getDescription()))
                .collect(Collectors.toList());

        return new ObjectDataResponse(
                typeBuilder.from(groups)
        );
    }

    public ObjectDataResponse userAttributes() {

        return new ObjectDataResponse(
                typeBuilder.from(new AuthorizationAttribute("user", "User"))
        );
    }

    public ObjectDataResponse users(ObjectDataRequest request) {
        ServiceConfiguration configuration = configurationParser.from(request);
        SharePointFacadeInterface sharePointFacadeInterface = sharepointFacadeFactory.get(ODATA_TYPE);

        AuthenticationResult authenticationResult = null;
        try {
            authenticationResult = azureHttpClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Not possible to get credentials");
        }

        // Build the required AuthorizationUser objects out of the users that Okta tells us about
        List<AuthorizationUser> users = Streams.asStream(sharePointFacadeInterface.fetchUsers(configuration, authenticationResult.getAccessToken(), null).iterator())
                .map(user -> new AuthorizationUser(
                        user.getId(),
                        user.getDisplayName(),
                        user.getJobTitle()
                ))
                .collect(Collectors.toList());

        return new ObjectDataResponse(
                typeBuilder.from(users)
        );
    }
}
