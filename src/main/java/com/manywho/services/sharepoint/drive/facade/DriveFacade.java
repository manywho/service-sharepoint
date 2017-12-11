package com.manywho.services.sharepoint.drive.facade;

import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.drive.types.Drive;
import com.manywho.services.sharepoint.drive.types.DriveItem;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
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

public class DriveFacade {

    private final static String GRAPH_ENDPOINT = "https://graph.microsoft.com/beta";
    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;
    private FileSharePointService fileSharePointService;

    @Inject
    public DriveFacade(ObjectMapperService objectMapperService, FileSharePointService fileSharePointService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getClient();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
        this.fileSharePointService = fileSharePointService;
    }

    public List<Drive> fetchDrives(ApplicationConfiguration configuration, String token) {
        String urlEntity = String.format("/me/drives");
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(urlEntity).build();
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

    public List<DriveItem> fetchDriveItemsRoot(ApplicationConfiguration configuration, String token, String driveId) {
        String path = String.format("/drives/%s/root/children", driveId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, "root");
    }

    public List<DriveItem> fetchDriveItems(ApplicationConfiguration configuration, String token, String driveId, String parentDriveItemId) {
        String path = String.format("/drives/%s/items/%s/children", driveId, parentDriveItemId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, parentDriveItemId);
    }

    public List<DriveItem> fetchDriveItemsInternal(ApplicationConfiguration configuration, String token, String path,
                                                   String driveId, String parentItemId) {

        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(path).build();
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
