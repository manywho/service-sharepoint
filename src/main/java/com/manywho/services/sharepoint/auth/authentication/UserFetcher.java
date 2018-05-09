package com.manywho.services.sharepoint.auth.authentication;

import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.client.entities.AuthResponse;
import com.manywho.services.sharepoint.client.entities.UserResponse;
import com.manywho.services.sharepoint.configuration.ApiConstants;
import com.manywho.services.sharepoint.users.GraphRestCompatibilityUtility;
import com.manywho.services.sharepoint.users.UserServiceClient;

import javax.inject.Inject;

import static com.manywho.services.sharepoint.configuration.ApiConstants.AUTHENTICATION_TYPE_ADD_IN;
import static com.manywho.services.sharepoint.configuration.ApiConstants.RESOURCE_GRAPH;

public class UserFetcher {

    private OauthAuthenticationClient oauthAuthenticationClient;
    private ContextTokenManager contextTokenManager;
    private ConfigurationParser configurationParser;

    @Inject
    public UserFetcher(ContextTokenManager contextTokenManager, OauthAuthenticationClient oauthAuthenticationClient,
                       ConfigurationParser configurationParser) {
        this.oauthAuthenticationClient = oauthAuthenticationClient;
        this.contextTokenManager = contextTokenManager;
        this.configurationParser = configurationParser;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByAuthCode(AuthenticationCredentials credentials) {
        AuthResponse authResponse = oauthAuthenticationClient.getAccessTokenByAuthCode(
                credentials.getCode(),
                RESOURCE_GRAPH);

        UserResponse userResponse = oauthAuthenticationClient.getCurrentUser(authResponse.getAccessToken());
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId("SharePoint");
        authenticatedWhoResult.setDirectoryName("SharePoint");
        authenticatedWhoResult.setEmail(userResponse.getMail());
        authenticatedWhoResult.setFirstName(userResponse.getGivenName());
        authenticatedWhoResult.setIdentityProvider(ApiConstants.AUTHENTICATION_TYPE_AZURE_AD);
        authenticatedWhoResult.setLastName(userResponse.getDisplayName());
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(userResponse.getMail());
        authenticatedWhoResult.setToken(authResponse.getAccessToken());
        authenticatedWhoResult.setUserId(userResponse.getUserPrincipalName());
        authenticatedWhoResult.setUsername(userResponse.getDisplayName());

        return authenticatedWhoResult;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByContextToken(AuthenticationCredentials credentials) throws Exception {
            AuthResponse response = contextTokenManager.getAuthentication(credentials);
            String restUserLogin = UserServiceClient.getUserLogin(configurationParser.from(credentials), response.getAccessToken());
            String userPrincipalName = GraphRestCompatibilityUtility.getUserPrincipalName(restUserLogin);

            AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
            authenticatedWhoResult.setDirectoryId( "SharePoint Add-In" );
            authenticatedWhoResult.setDirectoryName( "SharePoint Add-In" );
            authenticatedWhoResult.setEmail("no-email");
            authenticatedWhoResult.setFirstName("username");
            authenticatedWhoResult.setIdentityProvider(AUTHENTICATION_TYPE_ADD_IN);
            authenticatedWhoResult.setLastName("User Last Name");
            authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
            authenticatedWhoResult.setTenantName("SharePoint Add-In");
            authenticatedWhoResult.setToken(response.getAccessToken());
            authenticatedWhoResult.setUserId(userPrincipalName);
            authenticatedWhoResult.setUsername("username");

            return authenticatedWhoResult;
    }
}
