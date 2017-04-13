package com.manywho.services.sharepoint.services.file;

public class ResponseUrlUpload {
    private String uploadUrl;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public ResponseUrlUpload(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public ResponseUrlUpload() {
    }
}
