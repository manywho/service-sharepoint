package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "SharePoint List", summary = "Details about a SharePoint List")
public class SharePointList implements Type {
    public final static String NAME = "SharePoint List";

    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;

    @Type.Property(name = "Created Date Time", contentType = ContentType.String)
    private String createdDateTime;

    @Type.Property(name = "Last Modified Date Time", contentType = ContentType.String)
    private String modifiedDateTime;

    @Type.Property(name = "Description", contentType = ContentType.String)
    private String description;

    @Type.Property(name = "Web URL", contentType = ContentType.String)
    private String webUrl;

    @Type.Property(name = "Site ID", contentType = ContentType.String)
    private String siteId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(String modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
