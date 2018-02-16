package com.manywho.services.sharepoint.groups;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Group", summary = "Details about a Group")
public class Group implements Type{
    public final static String NAME = "Group";

    @Identifier
    @Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Property(name = "Description", contentType = ContentType.String)
    private String description;

    @Property(name = "Display Name", contentType = ContentType.String)
    private String displayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
