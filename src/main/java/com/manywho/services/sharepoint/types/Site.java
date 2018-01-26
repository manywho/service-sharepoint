package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Site", summary = "Details about a Site")
public class Site implements Type{
    public final static String NAME = "Site";

    @Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name="Group ID", contentType = ContentType.String)
    private String groupId;

    @Type.Property(name = "Created Date Time", contentType = ContentType.String)
    private String createdDateTime;

    @Type.Property(name = "Last Modified Date Time", contentType = ContentType.String)
    private String modifiedDateTime;

    @Type.Property(name = "Description", contentType = ContentType.String)
    private String description;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;

    @Type.Property(name = "Web URL", contentType = ContentType.String)
    private String webUrl;

    @Type.Property(name = "Parent ID", contentType = ContentType.String)
    private String parentId;

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

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getGroupId() {
        return groupId;
    }
}
