package com.manywho.services.sharepoint.configuration;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {

    @Configuration.Setting(name="Username",  contentType = ContentType.String)
    private String username;

    @Configuration.Setting(name="Password", contentType = ContentType.String)
    private String password;

    @Configuration.Setting(name="Host",  contentType = ContentType.String)
    private String host;

    @Configuration.Setting(name="Include Default Lists?",  contentType = ContentType.Boolean)
    private Boolean includeDefaultLists;

    @Configuration.Setting(name="Only For Groups",  contentType = ContentType.String)
    private String onlyGroups;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public Boolean getIncludeDefaultLists() {
        return includeDefaultLists;
    }

    public String getOnlyGroups() {
        return onlyGroups;
    }
}
