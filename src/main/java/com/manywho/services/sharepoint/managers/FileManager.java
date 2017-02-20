package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.FileSharePointService;
import com.manywho.services.sharepoint.services.FolderSharePointService;
import com.manywho.services.sharepoint.services.ObjectMapperService;

public class FileManager {
    private FileService fileService;
    private FileSharePointService fileSharePointService;
    private FolderSharePointService folderSharepointService;
    private ObjectMapperService objectMapperService;
    private PropertyCollectionParser propertyParser;

//    @Inject
//    public FileManager(FileService fileService, FileSharePointService fileSharePointService, PropertyCollectionParser propertyParser,
//                       FolderSharePointService folderSharePointService, ObjectMapperService objectMapperService) {
//        this.fileService = fileService;
//        this.fileSharePointService = fileSharePointService;
//        this.folderSharepointService = folderSharePointService;
//        this.propertyParser = propertyParser;
//        this.objectMapperService = objectMapperService;
//    }
//
//    public ObjectDataResponse uploadFile(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
//        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);
//        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);
//
//        if (bodyPart != null) {
//            Item itemFile = fileSharePointService.uploadFileToSharepoint(authenticatedWho, configuration, fileDataRequest, bodyPart);
//            if (itemFile != null) {
//                return new ObjectDataResponse(objectMapperService.buildManyWhoFileSystemObject(itemFile));
//            }
//        }
//
//        throw new Exception("A file was not provided to upload to Box");
//    }

//    public ObjectDataResponse loadFiles(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest) throws Exception {
//        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);
//        List<File> filesSharepoint = fileSharePointService.fetchFiles(authenticatedWho.getToken(), configuration, fileDataRequest);
//        ObjectCollection files = new ObjectCollection();
//
//        for (File file : filesSharepoint) {
//                files.add(objectMapperService.buildManyWhoFileSystemObject(file));
//        }
//
//        return new ObjectDataResponse(files);
//    }
//
//    public ObjectDataResponse loadFile(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
//        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);
//        String fileId = objectDataRequest.getListFilter().getId();
//
//        File filesSharepoint = fileSharePointService.fetchFile(authenticatedWho.getToken(), configuration, fileId);
//        ObjectCollection files = new ObjectCollection();
//
//        if(filesSharepoint != null) {
//            files.add(objectMapperService.buildManyWhoObjectFile(filesSharepoint, null));
//        }
//
//        return new ObjectDataResponse(files);
//    }
//
//    public ServiceResponse copyFile(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
//        FileCopy fileCopy = propertyParser.parse(serviceRequest.getInputs(), FileCopy.class);
//        Configuration configuration = propertyParser.parse(serviceRequest.getConfigurationValues(), Configuration.class);
//        if (fileCopy == null) {
//            throw new Exception("Unable to parse the incoming FileCopy request");
//        }
//
//        String newPath = fileCopy.getFolder().getId() + fileCopy.getName();
//        fileSharePointService.copyFile(user.getToken(), configuration, fileCopy.getFile().getId(), newPath);
//
//        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
//    }
//
//    public ObjectDataResponse loadFolder(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
//        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);
//        String folderPath = objectDataRequest.getListFilter().getId();
//
//        Folder folderSharePoint = folderSharepointService.fetchFolder(authenticatedWho.getToken(), configuration, folderPath);
//        ObjectCollection files = new ObjectCollection();
//
//        if(folderSharePoint != null) {
//            files.add(objectMapperService.buildManyWhoObjectFolder(folderSharePoint));
//        }
//
//        return new ObjectDataResponse(files);
//    }
}
