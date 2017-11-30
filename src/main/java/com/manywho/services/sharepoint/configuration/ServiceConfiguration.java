package com.manywho.services.sharepoint.configuration;

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

    @Property("Only For Groups")
    private String onlyGroups;

    public ServiceConfiguration(){}

    public ServiceConfiguration(String username, String password, String host, Boolean includeDefaultLists, String onlyGroups) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.includeDefaultLists = includeDefaultLists;
        this.onlyGroups = onlyGroups;
    }

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }

    public String getHost() {return host;}

    public Boolean getIncludeDefaultLists() {
        return includeDefaultLists;
    }

    public String getOnlyGroups() {
        return onlyGroups;
    }
}
