package com.manywho.services.sharepoint.managers;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointFacade;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.services.graph.Item;
import com.microsoft.services.graph.User;
import com.microsoft.services.graph.fetchers.*;
import com.microsoft.services.orc.core.OrcCollectionFetcher;
import com.microsoft.services.orc.resolvers.JavaDependencyResolver;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import java.util.UUID;

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
//
    public ObjectDataResponse uploadFile(String token, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileService.getFilePart(formDataMultiPart);
        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);

        if (bodyPart != null) {
            return sharePointFacade.uploadFileToSharePoint(token, fileDataRequest.getResourcePath(), bodyPart);
        }

        throw new Exception("A file was not provided to upload to SharePoint");
    }

//    public static void retrieveGraphServicesFile(AuthenticatedWho user, FileDataRequest fileDataRequest, FormDataMultiPart file) throws Exception {
//        JavaDependencyResolver resolver = new JavaDependencyResolver(user.getToken());
//        resolver.getLogger().setEnabled(true);
//
//        //String GRAPH_ENDPOINT = "https://graph.microsoft.com/beta/myOrganization";
//        String GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0";
//        GraphServiceClient client = new GraphServiceClient(GRAPH_ENDPOINT, resolver);
//        String filename = UUID.randomUUID().toString() + ".txt";
//
//        Item newFile = new Item();
//        newFile.setType("File");
//        newFile.setName(filename);
//
//        String ID = user.getUserId();
//        String payload = "My Content";
//
//        OrcCollectionFetcher<User, UserFetcher, UserCollectionOperations> users = client.getUsers();
//        UserFetcher user1 = users.getById(ID);
//        OrcCollectionFetcher<Item, ItemFetcher, ItemCollectionOperations> getFiles = user1.getFiles();
//        ListenableFuture<Item> newFile1 = getFiles.add(newFile);
//        Item addedFile = newFile1.get();
//        client.getUsers().getById(ID).getFiles().getById(addedFile.getId())
//                .asFile().getOperations().uploadContent(payload.getBytes()).get();
//
//        byte[] content = client.getUsers().getById(ID).getFiles().getById(addedFile.getId()).asFile().getOperations().content().get();
//        String retrieved = new String(content, "UTF-8");
//    }


//    public ObjectDataResponse loadFiles(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest) throws Exception {
//        Configuration configuration = propertyParser.parse(fileDataRequest.getConfigurationValues(), Configuration.class);
//        SharePointList<File> filesSharepoint = fileSharePointService.fetchFiles(authenticatedWho.getToken(), configuration, fileDataRequest);
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
