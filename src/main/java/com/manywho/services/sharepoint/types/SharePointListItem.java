package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

import java.time.OffsetDateTime;

@Type.Element(name = "SharePoint List Item", summary = "Details about an SharePoint List Item")
public class SharePointListItem implements Type{
    public final static String NAME = "SharePoint List Item";

    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Created At", contentType = ContentType.DateTime)
    private OffsetDateTime createdDateTime;

    @Type.Property(name = "Modified At", contentType = ContentType.DateTime)
    private OffsetDateTime modifiedDateTime;

    @Type.Property(name = "Web URL", contentType = ContentType.String)
    private String webUrl;

    @Type.Property(name = "Site ID", contentType = ContentType.String)
    private String siteId;

    @Type.Property(name = "List ID", contentType = ContentType.String)
    private String listId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(OffsetDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public OffsetDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(OffsetDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
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

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }
}
