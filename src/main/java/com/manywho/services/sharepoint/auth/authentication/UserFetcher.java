package com.manywho.services.sharepoint.auth.authentication;

import com.auth0.jwt.JWT;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.client.entities.AuthResponse;
import com.manywho.services.sharepoint.client.entities.UserResponse;
import com.manywho.services.sharepoint.configuration.ApiConstants;

import javax.inject.Inject;

import static com.manywho.services.sharepoint.configuration.ApiConstants.AUTHENTICATION_TYPE_ADD_IN;
import static com.manywho.services.sharepoint.configuration.ApiConstants.RESOURCE_GRAPH;

public class UserFetcher {

    private OauthAuthenticationClient oauthAuthenticationClient;
    private ContextTokenManager contextTokenManager;

    @Inject
    public UserFetcher(ContextTokenManager contextTokenManager, OauthAuthenticationClient oauthAuthenticationClient) {
        this.oauthAuthenticationClient = oauthAuthenticationClient;
        this.contextTokenManager = contextTokenManager;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByAuthCode(AuthenticationCredentials credentials) {
        AuthResponse authResponse = oauthAuthenticationClient.getAccessTokenByAuthCode(
                credentials.getCode(),
                RESOURCE_GRAPH);

        JWT jwt = JWT.decode(authResponse.getAccessToken());

        UserResponse userResponse = oauthAuthenticationClient.getCurrentUser(jwt.getToken());
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId("SharePoint");
        authenticatedWhoResult.setDirectoryName("SharePoint");
        authenticatedWhoResult.setEmail(userResponse.getMail());
        authenticatedWhoResult.setFirstName(userResponse.getGivenName());
        authenticatedWhoResult.setIdentityProvider(ApiConstants.AUTHENTICATION_TYPE_AZURE_AD);
        authenticatedWhoResult.setLastName(userResponse.getDisplayName());
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(userResponse.getMail());
        authenticatedWhoResult.setToken(jwt.getToken());
        authenticatedWhoResult.setUserId(jwt.getClaim("puid").asString());
        authenticatedWhoResult.setUsername(userResponse.getDisplayName());

        return authenticatedWhoResult;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByContextToken(AuthenticationCredentials credentials) throws Exception {
            AuthResponse response = contextTokenManager.getAuthentication(credentials);
            JWT jwt = JWT.decode(response.getAccessToken());

            String userId = jwt.getClaim("nameid").asString();


            AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
            authenticatedWhoResult.setDirectoryId( "SharePoint Add-In" );
            authenticatedWhoResult.setDirectoryName( "SharePoint Add-In" );
            authenticatedWhoResult.setEmail("no-email");
            authenticatedWhoResult.setFirstName("username");
            authenticatedWhoResult.setIdentityProvider(AUTHENTICATION_TYPE_ADD_IN);
            authenticatedWhoResult.setLastName("User Last Name");
            authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
            authenticatedWhoResult.setTenantName("SharePoint Add-In");
            authenticatedWhoResult.setToken( response.getAccessToken());
            authenticatedWhoResult.setUserId(userId);
            authenticatedWhoResult.setUsername("username");

            return authenticatedWhoResult;
    }
}

