package com.manywho.services.sharepoint.auth.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.client.entities.AuthResponse;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class ContextTokenManager {
    private OauthAuthenticationClient oauthAuthenticationClient;
    private AppConfiguration appConfiguration;
    @Inject

    public ContextTokenManager(OauthAuthenticationClient oauthAuthenticationClient, AppConfiguration appConfiguration) {
        this.oauthAuthenticationClient = oauthAuthenticationClient;
        this.appConfiguration = appConfiguration;
    }

    public AuthResponse getAuthentication(AuthenticationCredentials credentials) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(appConfiguration.getAppSecret());
        DecodedJWT jwt = JWT.decode(credentials.getSessionToken());
        String aud = jwt.getClaim("aud").asString();

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

        return oauthAuthenticationClient.getAccessTokenByContextToken(uri, grant_type, aud,
                appConfiguration.getAppSecret(), refreshToken, resource);
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
}
