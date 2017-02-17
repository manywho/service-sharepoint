package com.manywho.services.sharepoint.oauth;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import org.scribe.model.OAuthConfig;
import javax.inject.Inject;
import java.util.Random;

public class SharepointProvider extends AbstractOauth2Provider {
    private final SecurityConfiguration configuration;

    @Inject
    public SharepointProvider(SecurityConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return "sharepoint-manywho";
    }

    @Override
    public String getClientId() {
        return configuration.getOauth2ClientId();
    }

    @Override
    public String getClientSecret() {
        return configuration.getOauth2ClientSecret();
    }

    @Override
    public String getRedirectUri() {
        return "https://flow.manywho.com/api/run/1/oauth2";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {

        return String.format("https://login.microsoftonline.com/common/oauth2/authorize?client_id=%s&scope=%s&response_type=%s",
                config.getApiKey(), "User.Read" , "code");
    }
}
