package com.manywho.services.sharepoint.services;

import com.auth0.jwt.JWT;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.facades.SharePointFacade;

import javax.inject.Inject;

public class AuthenticationService {
    private final static String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";
    private final static String AUTHORITY_URL = "https://login.microsoftonline.com/common"; //
    private final static String RESOURCE_ID = "00000003-0000-0000-c000-000000000000";
    private final static String FILES_ENDPOINT = "https://manywho.sharepoint.com/_api/v1.0/me";
    private SecurityConfiguration securityConfiguration;
    private SharePointFacade sharePointFacade;

    @Inject
    public AuthenticationService( SecurityConfiguration securityConfiguration, SharePointFacade sharePointFacade) {
        this.securityConfiguration = securityConfiguration;
        this.sharePointFacade = sharePointFacade;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResult(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {
        String accessToken = sharePointFacade.getAccessToken(
                credentials.getCode(),
                AUTHORITY_URL,
                RESOURCE_ID,
                securityConfiguration.getOauth2ClientId(),
                securityConfiguration.getOauth2ClientSecret(),
                REDIRECT_URI);

//        String accessToken = sharePointFacade.getAccessTokenFromUserCredentials(
//                FILES_ENDPOINT,
//                securityConfiguration.getOauth2ClientId(),
//                AUTHORITY_URL);

        JWT jwt = JWT.decode(accessToken);
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
        authenticatedWhoResult.setUserId( jwt.getClaim("oid").asString());
        authenticatedWhoResult.setUsername(jwt.getClaim("unique_name").asString());

        return authenticatedWhoResult;
    }
}
