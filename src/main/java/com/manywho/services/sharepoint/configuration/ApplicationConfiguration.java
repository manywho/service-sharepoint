package com.manywho.services.sharepoint.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationDefault;
import com.manywho.sdk.services.config.ServiceConfigurationEnvironmentVariables;
import com.manywho.sdk.services.config.ServiceConfigurationProperties;

import javax.inject.Inject;

public class ApplicationConfiguration extends ServiceConfigurationDefault {
    @Inject
    public ApplicationConfiguration(ServiceConfigurationEnvironmentVariables environment, ServiceConfigurationProperties properties) {
        super(environment, properties);
    }

    public String getOauth2ClientId() {
        return this.get("oauth2.clientId");
    }

    public String getOauth2ClientSecret() {
        return this.get("oauth2.clientSecret");
    }

    public String getAppSecret() {
        return this.get("app.clientSecret");
    }

    public String getAppId() {
        return this.get("app.clientId");
    }
}
