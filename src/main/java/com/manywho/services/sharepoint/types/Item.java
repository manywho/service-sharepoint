package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Item", summary = "Details about a Item")
public class Item implements Type{
    public final static String NAME = "Item";

    @Type.Identifier
    private String id;

    @Type.Property(name = "Item ID", contentType = ContentType.String)
    private String itemId;

    @Type.Property(name = "Created Date Time", contentType = ContentType.String)
    private String createdDateTime;

    @Type.Property(name = "Last Modified Date Time", contentType = ContentType.String)
    private String modifiedDateTime;

    @Type.Property(name = "e Tag", contentType = ContentType.String)
    private String eTag;

    @Type.Property(name = "Web URL", contentType = ContentType.String)
    private String webUrl;

    @Type.Property(name = "List Item ID", contentType = ContentType.String)
    private String listItemId;

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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getListItemId() {
        return listItemId;
    }

    public void setListItemId(String listItemId) {
        this.listItemId = listItemId;
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
