package com.manywho.services.sharepoint.entities;

import com.manywho.sdk.services.annotations.Property;

public class Configuration {
    @Property("Username")
    private String username;

    @Property("Password")
    private String password;

    @Property("Host")
    private String host;

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }

    public String getHost() {return host;}
}
