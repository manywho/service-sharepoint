package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointFacade;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;

public class FileManager {
    private FileService fileService;
    private ObjectMapperService objectMapperService;
    private PropertyCollectionParser propertyParser;
    private SharePointFacade sharePointFacade;

    @Inject
    public FileManager(FileService fileService, PropertyCollectionParser propertyParser,
                       ObjectMapperService objectMapperService, SharePointFacade sharePointFacade) {
        this.fileService = fileService;
        this.propertyParser = propertyParser;
        this.objectMapperService = objectMapperService;
        this.sharePointFacade = sharePointFacade;

    }

    public ObjectDataResponse uploadFile(String token, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);
        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);

        if (bodyPart != null) {
            return sharePointFacade.uploadFileToSharePoint(token, fileDataRequest.getResourcePath(), bodyPart);
        }

        throw new Exception("A file was not provided to upload to SharePoint");
    }
}
