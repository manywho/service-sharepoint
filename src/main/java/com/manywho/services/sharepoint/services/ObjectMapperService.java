package com.manywho.services.sharepoint.services;

import com.independentsoft.share.File;
import com.independentsoft.share.Folder;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import org.apache.commons.io.FilenameUtils;

import java.util.List;

public class ObjectMapperService {
    public Object buildManyWhoFileSystemObject(File file) {

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Kind", FilenameUtils.getExtension(file.getName())));
        properties.add(new Property("ID", file.getServerRelativeUrl()));
        properties.add(new Property("Mime Type"));
        properties.add(new Property("Name", file.getName()));
        properties.add(new Property("Description", file.getTitle()));
        properties.add(new Property("Date Created", file.getCreatedTime()));
        properties.add(new Property("Date Modified", file.getLastModifiedTime()));
        properties.add(new Property("Download Uri", file.getLinkingUrl()));
        properties.add(new Property("Embed Uri"));
        properties.add(new Property("Icon Uri"));
        Object object = new Object();
        object.setDeveloperName("$File");
        object.setExternalId(file.getUniqueId());
        object.setProperties(properties);

        return object;
    }

    public Object buildManyWhoObjectFile(File file, String content) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", file.getServerRelativeUrl()));
        properties.add(new Property("Name", file.getName()));
        properties.add(new Property("Description", file.getTitle()));
        properties.add(new Property("Content", content));

        properties.add(new Property("Parent Folder", content));
        properties.add(new Property("Comments", content));
        //properties.add(new Property("Parent Folder", convertBoxFolder(fileInfo.getParent())));
        //properties.add(new Property("Comments", convertBoxComments(fileInfo.getResource().getComments())));

        properties.add(new Property("Created At", file.getCreatedTime()));
        properties.add(new Property("Modified At", file.getLastModifiedTime()));

        Object object = new Object();
        object.setDeveloperName(com.manywho.services.sharepoint.types.File.NAME);
        object.setExternalId(file.getServerRelativeUrl());
        object.setProperties(properties);

        return object;
    }

    public MObject buildManyWhoObjectFolder(Folder folderSharepoint) {
//        ObjectCollection files = StreamUtils.asStream(info.getResource().iterator())
//                .filter(item -> item instanceof BoxFile.Info)
//                .map(file -> convertBoxFileBasic((BoxFile.Info) file))
//                .collect(Collectors.toCollection(ObjectCollection::new));
        List<com.manywho.services.sharepoint.types.File> files = null;


        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", folderSharepoint.getServerRelativeUrl()));
        properties.add(new Property("Name", folderSharepoint.getName()));
        properties.add(new Property("Description", folderSharepoint.getName()));
        properties.add(new Property("Files", files));
        properties.add(new Property("Created At", folderSharepoint.getCreatedTime()));
        properties.add(new Property("Modified At", folderSharepoint.getLastModifiedTime()));

        Object object = new Object();
        object.setDeveloperName(com.manywho.services.sharepoint.types.Folder.NAME);
        object.setExternalId(folderSharepoint.getServerRelativeUrl());
        object.setProperties(properties);

        return object;
    }
}
