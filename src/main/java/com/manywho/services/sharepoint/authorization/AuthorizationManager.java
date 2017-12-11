package com.manywho.services.sharepoint.authorization;

import com.google.inject.Inject;
import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.sdk.services.types.system.AuthorizationAttribute;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;

public class AuthorizationManager {
    private final ConfigurationParser configurationParser;
    private final ServiceConfigurationImpl configuration;
    private final TypeBuilder typeBuilder;

    @Inject
    public AuthorizationManager(ConfigurationParser configurationParser, TypeBuilder typeBuilder, ServiceConfigurationImpl configuration) {
        this.configuration = configuration;
        this.configurationParser = configurationParser;
        this.typeBuilder = typeBuilder;
    }

    public ObjectDataResponse authorization(AuthenticatedWho authenticatedWho, ObjectDataRequest request) {
        ApplicationConfiguration applicationConfiguration = configurationParser.from(request);

//        var client = Clients.builder()
//                .setClientCredentials(new TokenClientCredentials(configuration.getApiKey()))
//                .setOrgUrl("https://" + configuration.getOrganizationUrl())
//                .build();

        String status;

        $User user = new $User();
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
                throw new RuntimeException("Specified user is not implemented");
//                if (authenticatedWho.getUserId().equals("PUBLIC_USER")) {
//                    status = "401";
//                    break;
//                }
//
//                var user = client.getUser(authenticatedWho.getUserId());
//                if (user == null) {
//                    status = "401";
//                    break;
//                }
//
//                // We need to check if the authenticated user is one of the authorized users by ID
//                if (request.getAuthorization().hasUsers()) {
//                    var isAuthorized = request.getAuthorization().getUsers().stream()
//                            .anyMatch(u -> u.getAuthenticationId().equals(user.getListId()));
//
//                    if (isAuthorized) {
//                        status = "200";
//                    } else {
//                        status = "401";
//                    }
//
//                    break;
//                }
//
//                // We need to check if the authenticated user is a member of one of the given groups, by group ID
//                if (request.getAuthorization().hasGroups()) {
//                    // If the user is a member of no groups, then they're automatically not authorized
//                    if (user.listGroups() == null) {
//                        status = "401";
//                        break;
//                    }
//
//                    var authorizedGroups = request.getAuthorization().getGroups();
//
//                    var isAuthorized = Streams.asStream(user.listGroups())
//                            .anyMatch(group -> authorizedGroups.stream().anyMatch(g -> g.getAuthenticationId().equals(group.getListId())));
//
//                    if (isAuthorized) {
//                        status = "200";
//                    } else {
//                        status = "401";
//                    }
//
//                    break;
//                }
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
        return null;
//        ApplicationConfiguration configuration = configurationParser.from(request);
//
//        var client = OktaClientFactory.create(configuration);
//
//        // Build the required AuthorizationGroup objects out of the groups that Okta tells us about
//        var groups = Streams.asStream(client.listGroups().iterator())
//                .map(group -> new AuthorizationGroup(group.getListId(), group.getProfile().getName(), group.getProfile().getDescription()))
//                .collect(Collectors.toList());
//
//        return new ObjectDataResponse(
//                typeBuilder.from(groups)
//        );
    }

    public ObjectDataResponse userAttributes() {

        return new ObjectDataResponse(
                typeBuilder.from(new AuthorizationAttribute("user", "User"))
        );
    }

    public ObjectDataResponse users(ObjectDataRequest request) {
        return null;
//        ApplicationConfiguration configuration = configurationParser.from(request);
//
//        var client = OktaClientFactory.create(configuration);
//
//        // Build the required AuthorizationUser objects out of the users that Okta tells us about
//        var users = Streams.asStream(client.listUsers().iterator())
//                .map(user -> new AuthorizationUser(
//                        user.getListId(),
//                        String.format("%s %s", user.getProfile().getFirstName(), user.getProfile().getLastName()),
//                        user.getProfile().getEmail()
//                ))
//                .collect(Collectors.toList());
//
//        return new ObjectDataResponse(
//                typeBuilder.from(users)
//        );
    }
}
