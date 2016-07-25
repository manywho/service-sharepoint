package com.manywho.services.sharepoint.services;

import com.independentsoft.share.File;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.util.Optional;

public class FileService {

    public BodyPart getFilePart(FormDataMultiPart formDataMultiPart) throws Exception {
        // If the filename is blank or doesn't exist, assume it's the FileDataRequest and skip it
        Optional<BodyPart> filePart = formDataMultiPart.getBodyParts().stream()
                .filter(bodyPart -> StringUtils.isNotEmpty(bodyPart.getContentDisposition().getFileName()))
                .findFirst();

        if (filePart.isPresent()) {
            return filePart.get();
        }

        throw new Exception("A file could not be found in the received request");
    }

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

    public Object buildManyWhoFileObject(File file, String content) {
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
}
