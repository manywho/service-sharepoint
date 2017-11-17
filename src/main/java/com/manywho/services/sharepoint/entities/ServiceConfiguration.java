package com.manywho.services.sharepoint.entities;

import com.manywho.sdk.services.annotations.Property;

public class ServiceConfiguration {
    @Property("Username")
    private String username;

    @Property("Password")
    private String password;

    @Property("Host")
    private String host;

    @Property("Include Default Lists?")
    private Boolean includeDefaultLists;

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }

    public String getHost() {return host;}

    public Boolean getIncludeDefaultLists() {
        return includeDefaultLists;
    }
}
