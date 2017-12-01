package com.manywho.services.sharepoint.configuration;

import com.manywho.sdk.services.annotations.Property;

public class ServiceConfiguration {
    @Property("Username")
    private String username;

    @Property("Password")
    private String password;

    @Property("Host")
    private String host;

    public ServiceConfiguration(){}

    public ServiceConfiguration(String username, String password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }

    public String getHost() {return host;}
}
