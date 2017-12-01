package com.manywho.services.sharepoint.configuration;


import javax.inject.Inject;

public class ServiceConfigurationImpl {
    private ServiceConfigurationDefault serviceConfigurationDefault;

    public static final String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";
    public static final String AUTHORITY_URI = "https://login.microsoftonline.com/common";

    @Inject
    public ServiceConfigurationImpl(ServiceConfigurationDefault serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    public String getName() {
        return "sharepoint-manywho";
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
                AUTHORITY_URI, this.getAppId(), "User.Read" , "code");
    }

}
