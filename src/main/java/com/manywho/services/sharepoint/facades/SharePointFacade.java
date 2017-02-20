package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SharePointFacade {

    private final static String GRAPH_ENDPOINT = "https://graph.microsoft.com/beta";
    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;

    @Inject
    public SharePointFacade(ObjectMapperService objectMapperService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getV4();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
    }


//    public Item createFile(String token, String userId, String path, InputStream inputStream) throws ExecutionException, InterruptedException, IOException {

//        String filename = UUID.randomUUID().toString() + ".txt";
//        JavaDependencyResolver resolver = new JavaDependencyResolver(token);
//        resolver.getLogger().setEnabled(true);
//        resolver.getLogger().setLogLevel(LogLevel.VERBOSE);
//
//        Item newFile = new Item();
//        //com.microsoft.services.files.Item newFile = new com.microsoft.services.files.Item();
//        newFile.setType("File");
//        newFile.setName(filename);
//
//        GraphServiceClient client = new GraphServiceClient(GRAPH_ENDPOINT, resolver);
//        Item addedFile = client.getUsers().getById(userId).getFiles().add(newFile).get();
//
//
//        client.getUsers().getById(userId).getFiles().getById(addedFile.getId()).asFile().getOperations()
//                .uploadContent(IOUtils.toByteArray(inputStream)).get();

        //FilesClient client = new FilesClient("https://manywho.sharepoint.com/_api/v1.0/me", resolver);
//        com.microsoft.services.files.Item addedFile = client.getFiles().add(newFile).get();
//        client.getFiles().getById(addedFile.getId()).asFile().putContent(IOUtils.toByteArray(inputStream)).get();

        //return addedFile;
//        return null;
//    }

//    public Folder createFolder(String path) {
//
////        String folderName = "newfolder"+ UUID.randomUUID().toString();
////        Item newFolder = new Item();
////        newFolder.setType("Folder");
////        newFolder.setName(folderName);
////
////        Item addedFolder = client.getUsers().getById(ID).().add(newFolder).get();
////
////        client.getUsers().getById(ID).getFiles().getById(addedFolder.getId()).asFolder();
////
////        byte[] content = client.getUsers().getById(ID).g().getById(addedFolder.getId()).asFile().getOperations().content().get();
//        return null;
//
////        try {
////            return service.createFolder(path);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public List<File> fetchFiles(String path) {
//        return null;
//
//        //try {
//        //    return service.getFiles(path);
//        //} catch (ServiceException e) {
//        //    throw new RuntimeException(e);
//        //}
//    }
//
//    public File fetchFile(String path) {
//        return null;
////        try {
////            return service.getFile(path);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public File copyFile(String accessToken, String sourcePath, String newPath)  {
//        return null;
////        try {
////            if(service.copyFile(sourcePath, newPath)){
////                return service.getFile(newPath);
////            }
////
////            throw new RuntimeException("File "+ newPath + "can not be copy");
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public Folder fetchFolder(String folderPath) {
//        return null;
////        try {
////            return service.getFolder(folderPath);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }

    public ObjectDataResponse fetchSites(String token) throws ExecutionException, InterruptedException {
        return fetchSitesInternal(token, "sharepoint/sites", "");
    }

    public ObjectDataResponse fetchSites(String token, String parentId) {
        return fetchSitesInternal(token, String.format("sharepoint/sites/%s/sites", parentId), parentId);
    }

    private ObjectDataResponse fetchSitesInternal(String token, String segment, String parentId) {
        URI sitesEntitySetURI = client.newURIBuilder(GRAPH_ENDPOINT)
                .appendEntitySetSegment(segment).build();

        ODataEntitySetRequest<ODataEntitySet> sitesEntitySetRequest = retrieveRequestFactory.getEntitySetRequest(sitesEntitySetURI);
        authenticate(token, sitesEntitySetRequest);
        ODataRetrieveResponse<ODataEntitySet> sitesEntitySetResponse = sitesEntitySetRequest.execute();

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), parentId);
    }

    public ObjectDataResponse fetchSite(String token, String id) {
        URI siteEntityURI = client.newURIBuilder(GRAPH_ENDPOINT)
                .appendEntitySetSegment(String.format("sharepoint/sites/%s", id)).build();

        ODataEntityRequest<ODataEntity> sitesEntitySetRequest = retrieveRequestFactory.getEntityRequest(siteEntityURI);
        authenticate(token, sitesEntitySetRequest);
        ODataRetrieveResponse<ODataEntity> sitesEntitySetResponse = sitesEntitySetRequest.execute();

        return responseSites(sitesEntitySetResponse.getBody(), "");
    }

    private void authenticate(String token, ODataRequest sitesEntitySetRequest) {
        sitesEntitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
    }

    private ObjectDataResponse responseSites(ODataEntity site, String parentId) {
        List<ODataEntity> sites = new ArrayList<>();
        sites.add(0, site);

        return responseSites(sites, parentId);
    }

    private ObjectDataResponse responseSites(List<ODataEntity> sites, String parentId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoSiteObject(siteEntity, parentId));
        }

        return new ObjectDataResponse(objectCollection);
    }
}


