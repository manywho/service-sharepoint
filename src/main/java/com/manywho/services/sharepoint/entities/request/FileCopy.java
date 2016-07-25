package com.manywho.services.sharepoint.entities.request;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.sharepoint.types.File;
import com.manywho.services.sharepoint.types.Folder;

import javax.validation.constraints.NotNull;

public class FileCopy {
    @Property(value = "Source File", isObject = true)
    @NotNull(message = "A source file is required when copying a file")
    private File file;

    @Property(value = "Destination Folder", isObject = true)
    @NotNull(message = "A destination folder is required when copying a file")
    private Folder folder;

    @Property("Name")
    private String name;

    public File getFile() {
        return file;
    }

    public Folder getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }
}
