package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.glassfish.jersey.media.multipart.BodyPart;
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
    private FileSharePointService fileSharePointService;

    @Inject
    public SharePointFacade(ObjectMapperService objectMapperService, FileSharePointService fileSharePointService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getV4();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
        this.fileSharePointService = fileSharePointService;
    }

    public ObjectDataResponse fetchSites(String token) throws ExecutionException, InterruptedException {
        ODataRetrieveResponse<ODataEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "sites/root/sites");

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), "");
    }

    public ObjectDataResponse fetchSites(String token, String parentId) {
        String url = String.format("sites/%s/sites", parentId);
        ODataRetrieveResponse<ODataEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, url);

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), parentId);
    }

    public ObjectDataResponse fetchSite(String token, String id) {
        String urlEntity = String.format("sites/%s", id);
        List<ODataEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return responseSites(sites, "");
    }

    public ObjectDataResponse fetchLists(String token, String idSite) {
        String urlEntity = String.format("sites/%s/lists", idSite);
        ODataRetrieveResponse<ODataEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseLists(entitySetResponse.getBody().getEntities(), idSite);
    }

    public ObjectDataResponse fetchList(String token, String idSite, String idList) {
        String entryPoint = String.format("sites/%s/lists/%s", idSite, idList);
        ODataRetrieveResponse<ODataEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);
        List<ODataEntity> lists = new ArrayList<>();
        lists.add(0, entitySetResponse.getBody());

        return responseLists(lists, idSite);
    }

    public ObjectDataResponse fetchListsRoot(String token) {
        return responseLists(getEntitiesSetResponse(token, "site/lists").getBody().getEntities(), "");
    }

    public ObjectDataResponse fetchItem(String token, String siteId, String listId, String itemId) {
        String entryPoint = String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId);
        ODataRetrieveResponse<ODataEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);

        List<ODataEntity> items = new ArrayList<>();
        items.add(0, entitySetResponse.getBody());

        return responseItems(items, siteId, listId);
    }

    public ObjectDataResponse fetchItems(String token, String siteId , String listId) {
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);
        ODataRetrieveResponse<ODataEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseItems(entitySetResponse.getBody().getEntities(), siteId, listId);
    }

    public ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
        //String uploadPath = fileSharePointService.getAnUploadUrl(token, bodyPart, path);
        //fileSharePointService.uploadFile(token, uploadPath, bodyPart);
        fileSharePointService.uploadSmallFile(token, path, bodyPart);
        return new ObjectDataResponse();
    }

    private ODataRetrieveResponse<ODataEntitySet> getEntitiesSetResponse(String token, String urlEntity) {
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(urlEntity).build();
        ODataEntitySetRequest<ODataEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));

        return entitySetRequest.execute();
    }

    private ODataRetrieveResponse<ODataEntity> getEntitySetResponse(String token, String entryPoint) {
        URI entityUri = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(entryPoint).build();
        ODataEntityRequest<ODataEntity> entitySetRequest = retrieveRequestFactory.getEntityRequest(entityUri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        return entitySetRequest.execute();
    }

    private ObjectDataResponse responseSites(List<ODataEntity> sites, String parentId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoSiteObject(siteEntity, parentId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseItems(List<ODataEntity> sites, String siteId, String listId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoItemObject(siteEntity, siteId, listId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseLists(List<ODataEntity> lists, String siteId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity listEntity : lists) {
            objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(listEntity, siteId));
        }

        return new ObjectDataResponse(objectCollection);
    }
}
