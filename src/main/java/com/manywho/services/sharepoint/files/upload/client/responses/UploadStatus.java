package com.manywho.services.sharepoint.files.upload.client.responses;

public class UploadStatus {
    private String id;
    private String name;
    private String size;
    private String[] nextExpectedRanges;
    private String expirationDateTime;

    public UploadStatus() {
        id = null;
        name = null;
        size = null;
        nextExpectedRanges = null;
        expirationDateTime = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String[] getNextExpectedRanges() {
        return nextExpectedRanges;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public boolean isUploadFinished() {
        return id != null;
    }
}
