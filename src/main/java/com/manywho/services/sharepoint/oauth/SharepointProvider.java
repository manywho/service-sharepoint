package com.manywho.services.sharepoint.oauth;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import org.scribe.model.OAuthConfig;

import javax.inject.Inject;

public class SharepointProvider extends AbstractOauth2Provider {
    private final ApplicationConfiguration configuration;
    public static final String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";
    public static final String AUTHORITY_URI = "https://login.microsoftonline.com/common";

    @Inject
    public SharepointProvider(ApplicationConfiguration configuration) {
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
        return REDIRECT_URI;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {

        return String.format("%s/oauth2/authorize?client_id=%s&scope=%s&response_type=%s",
                AUTHORITY_URI, config.getApiKey(), "User.Read" , "code");
    }
}
