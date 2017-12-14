package com.manywho.services.sharepoint.files.client.responses;

public class SessionCreated {
    private String uploadUrl;
    private String expirationDateTime;

    public SessionCreated() {}

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
}
