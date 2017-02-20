package com.manywho.services.sharepoint.oauth;

public class AuthResponse {
    private String id_token;
    private String token_type;
    private String scope;
    private String expires_in;
    private String ext_expires_in;
    private String expires_on;
    private String not_before;
    private String resource;
    private String access_token;
    private String refresh_token;

    public String getId_token() {
        return id_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getScope() {
        return scope;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getExt_expires_in() {
        return ext_expires_in;
    }

    public String getExpires_on() {
        return expires_on;
    }

    public String getNot_before() {
        return not_before;
    }

    public String getResource() {
        return resource;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
