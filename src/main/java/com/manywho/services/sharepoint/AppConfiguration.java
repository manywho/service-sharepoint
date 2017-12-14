package com.manywho.services.sharepoint;


import com.manywho.services.sharepoint.configuration.reader.ServiceConfigurationDefault;

import javax.inject.Inject;

public class AppConfiguration {
    private ServiceConfigurationDefault serviceConfigurationDefault;

    public static final String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";
    public static final String AUTHORITY_URI = "https://login.microsoftonline.com/common";
    public static final String AUTH_TYPE = "SharePoint Service";

    @Inject
    public AppConfiguration(ServiceConfigurationDefault serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    public String getName() {
        return AUTH_TYPE;
    }

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

}
