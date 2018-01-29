package com.manywho.services.sharepoint.auth.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.auth.oauth.AuthResponse;
import com.manywho.services.sharepoint.auth.oauth.AuthenticationClient;
import com.manywho.services.sharepoint.auth.oauth.UserResponse;
import com.manywho.services.sharepoint.constants.ApiConstants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;

import static com.manywho.services.sharepoint.constants.ApiConstants.*;

public class UserFetcher {
    private AppConfiguration serviceConfiguration;
    private AuthenticationClient azureHttpClient;

    @Inject
    public UserFetcher(AppConfiguration serviceConfiguration, AuthenticationClient azureHttpClient) {
        this.serviceConfiguration = serviceConfiguration;
        this.azureHttpClient = azureHttpClient;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByAuthCode(AuthenticationCredentials credentials) {
        AuthResponse authResponse = azureHttpClient.getAccessTokenByAuthCode(
                credentials.getCode(),
                REDIRECT_URI,
                serviceConfiguration.getOauth2ClientId(),
                serviceConfiguration.getOauth2ClientSecret(),
                RESOURCE_GRAPH);

        JWT jwt = JWT.decode(authResponse.getAccessToken());

        UserResponse userResponse = azureHttpClient.getCurrentUser(jwt.getToken());
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId("SharePoint");
        authenticatedWhoResult.setDirectoryName("SharePoint");
        authenticatedWhoResult.setEmail(userResponse.getMail());
        authenticatedWhoResult.setFirstName(userResponse.getGivenName());
        authenticatedWhoResult.setIdentityProvider(ApiConstants.AUTHENTICATION_TYPE_AZURE_AD);
        authenticatedWhoResult.setLastName(userResponse.getDisplayName());
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(serviceConfiguration.getOauth2ClientId());
        authenticatedWhoResult.setToken(jwt.getToken());
        authenticatedWhoResult.setUserId(userResponse.getId());
        authenticatedWhoResult.setUsername(userResponse.getDisplayName());

        return authenticatedWhoResult;
    }

    public AuthenticatedWhoResult getAuthenticatedWhoResultByContextToken(AuthenticationCredentials credentials) throws Exception {
        AuthResponse response = getAuthFromContextToken(credentials);

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

        // todo get userId and name by getToken
        authenticatedWhoResult.setUserId(UUID.randomUUID().toString());
        authenticatedWhoResult.setUsername("username");

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

    private AuthResponse getAuthFromContextToken(AuthenticationCredentials credentials) throws UnsupportedEncodingException, JSONException {
        Algorithm algorithm = Algorithm.HMAC256(serviceConfiguration.getAppSecret());
        DecodedJWT jwt = JWT.decode(credentials.getSessionToken());
        String appctx = jwt.getClaim("appctx").asString();
        String aud = jwt.getClaim("aud").asString();
        JSONObject object = new JSONObject(appctx);

        String targetPrincipalName = getTargetPrincipalName(jwt.getClaim("appctxsender").asString());
        String realm = getRealm(jwt.getClaim("aud").asString());
        String refreshToken = jwt.getClaim("refreshtoken").asString();

        String uri = "https://login.windows.net/common/tokens/OAuth/2";
        String grant_type = "refresh_token";

        String domain = credentials.getConfigurationValues().stream()
                .filter(p -> Objects.equals(p.getDeveloperName(), "Host")).findFirst()
                .orElseThrow(()-> new RuntimeException("Host is mandatory"))
                .getContentValue().replace("https://", "");

        String resource = String.format("%s/%s@%s", targetPrincipalName, domain, realm);

        return azureHttpClient.getAccessTokenByContextToken(uri, grant_type, aud,
                serviceConfiguration.getAppSecret(), refreshToken, resource);
    }
}

