package com.manywho.services.sharepoint.managers;

import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.ObjectMapperService;

import javax.inject.Inject;

public class FileManager {
    private FileService fileService;
    private ObjectMapperService objectMapperService;
    private SharePointOdataFacade sharePointFacade;

    @Inject
    public FileManager(FileService fileService, ObjectMapperService objectMapperService,
                       SharePointOdataFacade sharePointFacade) {

        this.fileService = fileService;
        this.objectMapperService = objectMapperService;
        this.sharePointFacade = sharePointFacade;

    }

//    public ObjectDataResponse uploadFile(String token, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
//        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);
//        ServiceConfigurationImpl configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), ServiceConfigurationImpl.class);
//
//        if (bodyPart != null) {
//            return sharePointFacade.uploadFileToSharePoint(token, fileDataRequest.getResourcePath(), bodyPart);
//        }
//
//        throw new Exception("A file was not provided to upload to SharePoint");
//    }
}
