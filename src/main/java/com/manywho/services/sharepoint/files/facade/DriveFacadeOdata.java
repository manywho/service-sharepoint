package com.manywho.services.sharepoint.files.facade;

import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.types.Drive;
import com.manywho.services.sharepoint.types.DriveItem;
import com.manywho.services.sharepoint.mapper.ObjectMapperService;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.core.ODataClientFactory;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_BETA;

public class DriveFacadeOdata {

    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;

    @Inject
    public DriveFacadeOdata(ObjectMapperService objectMapperService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getClient();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
    }

    public List<Drive> fetchDrives(ServiceConfiguration configuration, String token) {
        String urlEntity = String.format("/me/drives");
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_BETA).appendEntitySetSegment(urlEntity).build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();
        List<ClientEntity> sites = entitySetResponse.getBody().getEntities();
        List<Drive> listDrives = new ArrayList<>();

        for (ClientEntity siteEntity : sites) {
            listDrives.add(this.objectMapperService.buildManyWhoDriveObject(siteEntity));
        }

        return listDrives;
    }

    public List<DriveItem> fetchDriveItemsRoot(ServiceConfiguration configuration, String token, String driveId) {
        String path = String.format("/drives/%s/root/children", driveId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, "root");
    }

    public List<DriveItem> fetchDriveItems(ServiceConfiguration configuration, String token, String driveId, String parentDriveItemId) {
        String path = String.format("/drives/%s/items/%s/children", driveId, parentDriveItemId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, parentDriveItemId);
    }

    private List<DriveItem> fetchDriveItemsInternal(ServiceConfiguration configuration, String token, String path,
                                                    String driveId, String parentItemId) {

        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_BETA).appendEntitySetSegment(path).build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();
        List<ClientEntity> sites = entitySetResponse.getBody().getEntities();
        List<DriveItem> listDrivesItems = new ArrayList<>();

        for (ClientEntity siteEntity : sites) {
            listDrivesItems.add(this.objectMapperService.buildManyWhoDriveItemObject(siteEntity, driveId, parentItemId));
        }

        return listDrivesItems;
    }

}
