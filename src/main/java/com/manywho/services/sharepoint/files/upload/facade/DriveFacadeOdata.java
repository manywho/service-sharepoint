package com.manywho.services.sharepoint.files.upload.facade;

import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.constants.ApiConstants;
import com.manywho.services.sharepoint.mapper.ObjectMapperService;
import com.manywho.services.sharepoint.drives.Drive;
import com.manywho.services.sharepoint.drives.items.DriveItem;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
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

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_V1;

public class DriveFacadeOdata {

    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;
    private final CUDRequestFactory cudRequestFactory;

    @Inject
    public DriveFacadeOdata(ObjectMapperService objectMapperService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getClient();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
        this.cudRequestFactory = client.getCUDRequestFactory();
    }

    public Drive fetchDrive(String token, String path) {
        URI entityUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        ODataEntityRequest<ClientEntity> entitySetRequest = retrieveRequestFactory.getEntityRequest(entityUri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntity> drive = entitySetRequest.execute();

        if (drive.getBody() == null) {
            return null;
        }

        return this.objectMapperService.buildManyWhoDriveObject(drive.getBody());
    }

    public List<Drive> fetchDrives(ServiceConfiguration configuration, String token, String path) {
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
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
        String path = String.format("drives/%s/root/children", driveId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, "root");
    }

    public List<DriveItem> fetchDriveItems(ServiceConfiguration configuration, String token, String driveId, String parentDriveItemId) {
        String path = String.format("drives/%s/items/%s/children", driveId, parentDriveItemId);

        return fetchDriveItemsInternal(configuration, token, path, driveId, parentDriveItemId);
    }

    // todo create action delete file
    public void deleteFile (ServiceConfiguration configuration, String token, String driveId, String fileId) {
        String urlEntity = String.format("drives/%s/items/%s", driveId, fileId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();
        ODataDeleteRequest deleteRequest = cudRequestFactory.getDeleteRequest(uri);
        deleteRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        deleteRequest.execute();
    }

    private List<DriveItem> fetchDriveItemsInternal(ServiceConfiguration configuration, String token, String path,
                                                    String driveId, String parentItemId) {

        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();
        List<ClientEntity> driveItemsEntity = entitySetResponse.getBody().getEntities();
        List<DriveItem> listDrivesItems = new ArrayList<>();

        for (ClientEntity driveItemEntity : driveItemsEntity) {
            listDrivesItems.add(this.objectMapperService.buildManyWhoDriveItemObject(driveItemEntity, driveId, parentItemId));
        }

        return listDrivesItems;
    }

}
