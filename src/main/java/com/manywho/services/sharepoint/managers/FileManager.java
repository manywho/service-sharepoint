package com.manywho.services.sharepoint.managers;

import com.independentsoft.share.File;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.FileSharePointService;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import javax.inject.Inject;
import java.util.List;

public class FileManager {
    @Inject
    private FileService fileService;

    @Inject
    private FileSharePointService fileSharePointService;

    @Inject
    private PropertyCollectionParser propertyParser;

    public ObjectDataResponse uploadFile(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);
        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);

        if (bodyPart != null) {
            File file = fileSharePointService.uploadFileToSharepoint(authenticatedWho.getToken(), configuration, fileDataRequest, bodyPart);
            if (file != null) {
                return new ObjectDataResponse(fileService.buildManyWhoFileObject(file));
            }
        }

        throw new Exception("A file was not provided to upload to Box");
    }

    public ObjectDataResponse loadFiles(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);
        List<File> filesSharepoint = fileSharePointService.fetchFiles(authenticatedWho.getToken(), configuration, fileDataRequest);
        ObjectCollection files = new ObjectCollection();

        for (File file : filesSharepoint) {
                files.add(fileService.buildManyWhoFileObject(file));
        }

        return new ObjectDataResponse(files);
    }
}
