package com.manywho.services.sharepoint.graph;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;

import java.net.URI;
import java.util.List;

public class GraphClient {
    private ODataClient client;

    public GraphClient(ODataClient oDataClient) {
        this.client = oDataClient;
    }

    public ClientEntity query(String token, URI path) {
        ODataEntityRequest<ClientEntity> entitySetRequest = client.getRetrieveRequestFactory().getEntityRequest(path);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntity> clientEntity = entitySetRequest.execute();

        return clientEntity.getBody();
    }

    public List<ClientEntity> queryList(String token, URI path) {

        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = client.getRetrieveRequestFactory().getEntitySetRequest(path);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();

        return entitySetResponse.getBody().getEntities();
    }

//    public List<ClientEntity> queryListWithPagination(String token, URIBuilder uriBuilder, ListFilter listFilter) {
//        OdataPaginator paginator = new OdataPaginator();
//
//        return paginator.getEntities(token, uriBuilder, listFilter, client.getRetrieveRequestFactory());
//    }

    public void executeDelete(String token, URI path) {
        ODataDeleteRequest deleteRequest = client.getCUDRequestFactory().getDeleteRequest(path);
        deleteRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        deleteRequest.execute();
    }
}
