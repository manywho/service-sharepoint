package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Drive", summary = "Details about a Drive")
public class Drive implements Type {

    public final static String NAME = "Drive";

    @Type.Identifier
    @Type.Property(name = "ID", contentType = ContentType.String)
    private String id;

    @Type.Property(name = "Type", contentType = ContentType.String)
    private String driveType;

    @Type.Property(name = "Name", contentType = ContentType.String)
    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
