package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Drive Item", summary = "Details about a Drive Item")
public class DriveItem implements Type {
    public final static String NAME = "Drive Item";

    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;

    @Type.Property(name = "Type", contentType = ContentType.String)
    private String type;

    @Type.Property(name = "Drive ID", contentType = ContentType.String)
    private String driveId;

    @Type.Property(name = "Parent ID", contentType = ContentType.String)
    private String driveItemParent;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getDriveItemParent() {
        return driveItemParent;
    }

    public void setDriveItemParent(String driveItemParent) {
        this.driveItemParent = driveItemParent;
    }
}
