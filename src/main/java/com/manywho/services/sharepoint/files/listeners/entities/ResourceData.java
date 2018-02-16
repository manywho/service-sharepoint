package com.manywho.services.sharepoint.files.listeners.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceData {

    @JsonProperty("@odata.type")
    private String type;

    @JsonProperty("@odata.id")
    private String odataId;

    @JsonProperty("@odata.etag")
    private String etag;

    @JsonProperty("Id")
    private String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOdataId() {
        return odataId;
    }

    public void setOdataId(String odataId) {
        this.odataId = odataId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
