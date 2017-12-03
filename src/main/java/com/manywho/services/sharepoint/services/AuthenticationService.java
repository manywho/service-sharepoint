package com.manywho.services.sharepoint.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.oauth.AuthResponse;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.oauth.SharepointProvider;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;

public class AuthenticationService {
    public final static String RESOURCE_ID = "00000003-0000-0000-c000-000000000000";
    private ApplicationConfiguration securityConfiguration;
    private AzureHttpClient azureHttpClient;
    private PropertyCollectionParser propertyParser;

    @Inject
    public AuthenticationService(ApplicationConfiguration securityConfiguration, AzureHttpClient azureHttpClient, PropertyCollectionParser propertyParser) {
        this.securityConfiguration = securityConfiguration;
        this.azureHttpClient = azureHttpClient;
        this.propertyParser = propertyParser;
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

    private String getTargetPrincipalName(String appctxsender) {

        if (appctxsender == null) {
            return null;
        }

        return appctxsender.split("@")[0];
    }

     private String getRealm(String aud) {
        if (aud == null)
        {
            return null;
        }

        return aud.substring(aud.indexOf('@') + 1);
    }


    public AuthenticatedWhoResult getAuthenticatedWhoResult(AuthenticationCredentials credentials) throws Exception {
        ServiceConfiguration configuration = propertyParser.parse(credentials.getConfigurationValues(), ServiceConfiguration.class);
        Algorithm algorithm = Algorithm.HMAC256(securityConfiguration.getAppSecret());
//        JWTVerifier verifier = JWT.require(algorithm)
//                .withIssuer("00000001-0000-0000-c000-000000000000@f1cd6ef0-f210-4c39-8471-15f8929b25ce")
//                .build();
//
//        verifier.verify(credentials.getSessionToken());

        //DecodedJWT jwt = verifier.verify(credentials.getSessionToken());
        DecodedJWT jwt = JWT.decode(credentials.getSessionToken());
        String appctx = jwt.getClaim("appctx").asString();
        String aud = jwt.getClaim("aud").asString();
        JSONObject object = new JSONObject(appctx);
        String targetPrincipalName = getTargetPrincipalName(jwt.getClaim("appctxsender").asString());
        String realm = getRealm(jwt.getClaim("aud").asString());
        String refreshToken = jwt.getClaim("refreshtoken").asString();
        String uriAuth = object.getString("SecurityTokenServiceUri");

        String uri = "https://accounts.accesscontrol.windows.net/tokens/OAuth/2";
        String grant_type = "refresh_token";

        String domain = configuration.getHost().replace("https://", "");
        String resource = String.format("%s/%s@%s", targetPrincipalName, domain, realm);
        AuthResponse response = azureHttpClient.getAccessTokenByContextToken(uri, grant_type, aud,
                securityConfiguration.getAppSecret(), refreshToken, resource);


        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId( "SharePoint Add-In" );
        authenticatedWhoResult.setDirectoryName( "SharePoint Add-In" );
        authenticatedWhoResult.setEmail("no-email");
        authenticatedWhoResult.setFirstName("username");
        authenticatedWhoResult.setIdentityProvider("SharePoint Add-In");
        authenticatedWhoResult.setLastName("User Last Name");
        authenticatedWhoResult.setStatus(AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName("SharePoint Add-In");
        authenticatedWhoResult.setToken( response.getAccess_token());

        if (Objects.equals(configuration.getStrategy(), "SuperUser")) {
            authenticatedWhoResult.setIdentityProvider(SharepointProvider.IDENTITY_NAME);
            String token = azureHttpClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword())
                    .getAccessToken();

            authenticatedWhoResult.setToken(token);
        }

        authenticatedWhoResult.setUserId(UUID.randomUUID().toString());
        authenticatedWhoResult.setUsername("username");

        return authenticatedWhoResult;
    }
}
