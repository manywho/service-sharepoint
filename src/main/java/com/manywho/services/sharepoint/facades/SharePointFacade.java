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

        return responseSites(sitesEntitySetResponse.getBody());
    }

    private void authenticate(String token, ODataRequest sitesEntitySetRequest) {
        sitesEntitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
    }

    private ObjectDataResponse responseSites(ODataEntity site) {
        List<ODataEntity> sites = new ArrayList<>();
        sites.add(0, site);

        //todo get id of parent of this site
        return responseSites(sites, "");
    }

    private ObjectDataResponse responseSites(List<ODataEntity> sites, String parentId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoSiteObject(siteEntity, parentId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseLists(List<ODataEntity> lists, String siteId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ODataEntity listEntity : lists) {
            objectCollection.add(this.objectMapperService.buildManyWhoListObject(listEntity, siteId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseLists(ODataEntity list) {
        List<ODataEntity> lists = new ArrayList<>();
        lists.add(0, list);

        //todo get id of parent of this site
        return responseLists(lists, "");
    }


    public ObjectDataResponse fetchLists(String token, String idSite) {
        URI sitesEntitySetURI = client.newURIBuilder(GRAPH_ENDPOINT)
                .appendEntitySetSegment(String.format("sharepoint/sites/%s/lists", idSite)).build();

        ODataEntitySetRequest<ODataEntitySet> listEntitySetRequest = retrieveRequestFactory.getEntitySetRequest(sitesEntitySetURI);
        authenticate(token, listEntitySetRequest);
        ODataRetrieveResponse<ODataEntitySet> listEntitySetResponse = listEntitySetRequest.execute();

        return responseLists(listEntitySetResponse.getBody().getEntities(), idSite);
    }

    public ObjectDataResponse fetchList(String token, String idSite, String idList) {
        URI listEntityURI = client.newURIBuilder(GRAPH_ENDPOINT)
                .appendEntitySetSegment(String.format("sharepoint/sites/%s/lists/%s", idSite, idList)).build();

        ODataEntityRequest<ODataEntity> listsEntitySetRequest = retrieveRequestFactory.getEntityRequest(listEntityURI);
        authenticate(token, listsEntitySetRequest);
        ODataRetrieveResponse<ODataEntity> listsEntitySetResponse = listsEntitySetRequest.execute();

        return responseLists(listsEntitySetResponse.getBody());
    }

    public ObjectDataResponse fetchListsRoot(String token) {
        URI sitesEntitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment("sharepoint/site/lists").build();

        ODataEntitySetRequest<ODataEntitySet> listEntitySetRequest = retrieveRequestFactory.getEntitySetRequest(sitesEntitySetURI);
        authenticate(token, listEntitySetRequest);
        ODataRetrieveResponse<ODataEntitySet> listEntitySetResponse = listEntitySetRequest.execute();

        return responseLists(listEntitySetResponse.getBody().getEntities(), "");
    }
}


