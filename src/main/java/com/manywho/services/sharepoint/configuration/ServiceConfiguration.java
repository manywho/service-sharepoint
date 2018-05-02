package com.manywho.services.sharepoint.configuration;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ServiceConfiguration implements Configuration {

    @Configuration.Setting(name="Username",  contentType = ContentType.String, required = false)
    private String username;

    @Configuration.Setting(name="Password", contentType = ContentType.Password, required = false)
    private String password;

    @Configuration.Setting(name="Host",  contentType = ContentType.String, required = false)
    private String host;

    @Configuration.Setting(name="Authentication Strategy",  contentType = ContentType.String, required = false)
    private String strategy;

    @Configuration.Setting(name="Prompt the Admin Consent",  contentType = ContentType.Boolean, required = false)
    private Boolean promptAdminConsent;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    // at the moment we dont include the lists that are create by the system
    public boolean getIncludeDefaultLists() {
        return false;
    }

    public String getStrategy() {
        return strategy;
    }

    public Boolean getPromptAdminConsent() {
        return promptAdminConsent;
    }
}
