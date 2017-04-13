package com.manywho.services.sharepoint.services;

import com.auth0.jwt.JWT;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.oauth.AuthResponse;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.oauth.SharepointProvider;

import javax.inject.Inject;

public class AuthenticationService {
    public final static String RESOURCE_ID = "00000003-0000-0000-c000-000000000000";
    private SecurityConfiguration securityConfiguration;
    private AzureHttpClient azureHttpClient;

    @Inject
    public AuthenticationService( SecurityConfiguration securityConfiguration, AzureHttpClient azureHttpClient) {
        this.securityConfiguration = securityConfiguration;
        this.azureHttpClient = azureHttpClient;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResult(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {
        AuthResponse authResponse = azureHttpClient.getAccessTokenByAuthCode(
                credentials.getCode(),
                SharepointProvider.REDIRECT_URI,
                securityConfiguration.getOauth2ClientId(),
                securityConfiguration.getOauth2ClientSecret(),
                RESOURCE_ID);

        JWT jwt = JWT.decode(authResponse.getAccess_token());
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId( provider.getName());
        authenticatedWhoResult.setDirectoryName( provider.getName());
        authenticatedWhoResult.setEmail(jwt.getClaim("unique_name").asString());
        authenticatedWhoResult.setFirstName(jwt.getClaim("given_name").asString());
        authenticatedWhoResult.setIdentityProvider(provider.getName());
        authenticatedWhoResult.setLastName(jwt.getClaim("family_name").asString());
        authenticatedWhoResult.setStatus(AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(provider.getClientId());
        authenticatedWhoResult.setToken( jwt.getToken());
        authenticatedWhoResult.setUserId( "9c102602-1474-11e7-93ae-92361f002671");
        authenticatedWhoResult.setUsername(jwt.getClaim("unique_name").asString());

        return authenticatedWhoResult;
    }
}
