package com.manywho.services.sharepoint;


import com.manywho.services.sharepoint.configuration.reader.ServiceConfigurationDefault;
import com.manywho.services.sharepoint.configuration.ApiConstants;

import javax.inject.Inject;

import static com.manywho.services.sharepoint.configuration.ApiConstants.AUTHORITY_URI;

public class AppConfiguration {
    private ServiceConfigurationDefault serviceConfigurationDefault;

    @Inject
    public AppConfiguration(ServiceConfigurationDefault serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    public String getName() { return ApiConstants.AUTHENTICATION_TYPE_AZURE_AD; }

    public String getOauth2ClientId() {
        return this.serviceConfigurationDefault.get("oauth2.clientId");
    }

    public String getOauth2ClientSecret()
    {
        return this.serviceConfigurationDefault.get("oauth2.clientSecret");
    }

    public String getAppSecret() {
        return this.serviceConfigurationDefault.get("app.clientSecret");
    }

    public String getAppId() {
        return this.serviceConfigurationDefault.get("app.clientId");
    }

    public String getAuthorizationUrl() {
        return String.format("%s/oauth2/authorize?client_id=%s&scope=%s&response_type=%s",
                AUTHORITY_URI, this.getOauth2ClientId(), "User.Read" , "code");
    }

    public String getRedisUrl() {
        return this.serviceConfigurationDefault.get("redis.url");
    }
}
