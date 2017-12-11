package com.manywho.services.sharepoint.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;
import com.manywho.services.sharepoint.oauth.AuthResponse;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import org.json.JSONObject;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AuthenticationService {
    public final static String RESOURCE_ID = "00000003-0000-0000-c000-000000000000";
    private ServiceConfigurationImpl securityConfiguration;
    private AzureHttpClient azureHttpClient;

    @Inject
    public AuthenticationService(ServiceConfigurationImpl securityConfiguration, AzureHttpClient azureHttpClient) {
        this.securityConfiguration = securityConfiguration;
        this.azureHttpClient = azureHttpClient;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByAuthCode(AuthenticationCredentials credentials) throws Exception {
        AuthResponse authResponse = azureHttpClient.getAccessTokenByAuthCode(
                credentials.getCode(),
                ServiceConfigurationImpl.REDIRECT_URI,
                securityConfiguration.getOauth2ClientId(),
                securityConfiguration.getOauth2ClientSecret(),
                RESOURCE_ID);

        JWT jwt = JWT.decode(authResponse.getAccess_token());
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId("SharePoint");
        authenticatedWhoResult.setDirectoryName("SharePoint");
        authenticatedWhoResult.setEmail(jwt.getClaim("unique_name").asString());
        authenticatedWhoResult.setFirstName(jwt.getClaim("given_name").asString());
        authenticatedWhoResult.setIdentityProvider(securityConfiguration.getName());
        authenticatedWhoResult.setLastName(jwt.getClaim("family_name").asString());
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(securityConfiguration.getOauth2ClientId());
        authenticatedWhoResult.setToken( jwt.getToken());
        //tod use here userid from jwt
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


    public AuthenticatedWhoResult getAuthenticatedWhoResultByContextToken(AuthenticationCredentials credentials) throws Exception {
        //ApplicationConfiguration configuration = propertyParser.from(credentials.getConfigurationValues());
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


        String domain = getHostFromCredentials(credentials)
                .replace("https://", "");

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
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName("SharePoint Add-In");
        authenticatedWhoResult.setToken( response.getAccess_token());
        // todo get userId by token
        authenticatedWhoResult.setUserId(UUID.randomUUID().toString());
        authenticatedWhoResult.setUsername("username");

        return authenticatedWhoResult;
    }

    private String getHostFromCredentials(AuthenticationCredentials credentials) {
        Optional<EngineValue> host = credentials.getConfigurationValues().stream()
                .filter(p -> Objects.equals(p.getDeveloperName(), "Host")).findFirst();

        return host.get().getContentValue();
    }
}
