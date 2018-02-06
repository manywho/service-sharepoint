package com.manywho.services.sharepoint.files.client.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class FileMetadata {
    private String id;

    private String name;

    @JsonIgnore
    private String mimeType;

    @JsonProperty("@microsoft.graph.downloadUrl")
    private String downloadUri;

    @JsonProperty("createdDateTime")
    private String dateCreated;

    @JsonProperty("lastModifiedDateTime")
    private String dateModified;

    public FileMetadata() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    @JsonProperty("file")
    public void setMimeType(Map<String, Object> file) {
        if (file!= null && file.get("mimeType") != null) {
            mimeType = (String) file.get("mimeType");
        }
    }
}
