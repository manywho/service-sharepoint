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
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.client.entities.AuthResponse;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.groups.Group;
import com.manywho.services.sharepoint.groups.GroupClient;
import com.manywho.services.sharepoint.users.UserClientOdata;
import com.manywho.services.sharepoint.users.UserServiceClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.manywho.services.sharepoint.configuration.ApiConstants.AUTHENTICATION_TYPE_ADD_IN;

public class AuthorizationManager {
    private final ConfigurationParser configurationParser;
    private final AppConfiguration configuration;
    private final TypeBuilder typeBuilder;
    private OauthAuthenticationClient azureHttpClient;
    private GroupClient groupClient;
    private UserClientOdata userClientOdata;

    @Inject
    public AuthorizationManager(ConfigurationParser configurationParser, TypeBuilder typeBuilder, AppConfiguration configuration,
                                OauthAuthenticationClient azureHttpClient, GroupClient groupClient, UserClientOdata userClientOdata) {

        this.configuration = configuration;
        this.configurationParser = configurationParser;
        this.typeBuilder = typeBuilder;
        this.azureHttpClient = azureHttpClient;
        this.groupClient = groupClient;
        this.userClientOdata = userClientOdata;
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

                String userId;

                if (authenticatedWho.getIdentityProvider().equals(AUTHENTICATION_TYPE_ADD_IN)) {
                    userId = UserServiceClient.getUserId(serviceConfiguration, authenticatedWho.getToken());
                } else {
                    userId = userClientOdata
                            .getUserId(serviceConfiguration, authenticatedWho.getToken());
                }

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
                // we use graph for that tassk
              if (request.getAuthorization().hasGroups()) {
                  List<Group> groups = groupClient.fetchGroups(authenticatedWho.getToken(), null);

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
        AuthResponse authenticationResult = null;

        try {
            authenticationResult = azureHttpClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching a valid getToken with the username and password", e);
        }

        List<AuthorizationGroup> groups = Streams.asStream(groupClient.fetchGroups(authenticationResult.getAccessToken(), null).iterator())
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
        AuthResponse authResponse = azureHttpClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword());

        // Build the required AuthorizationUser objects out of the users that Okta tells us about
        List<AuthorizationUser> users = Streams.asStream(userClientOdata.fetchUsers(authResponse.getAccessToken()).iterator())
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
