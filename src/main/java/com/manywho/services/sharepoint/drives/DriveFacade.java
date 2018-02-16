package com.manywho.services.sharepoint.drives;

import com.manywho.services.sharepoint.constants.ApiConstants;
import com.manywho.services.sharepoint.drives.items.DriveItem;
import com.manywho.services.sharepoint.drives.items.MapperDriveItem;
import com.manywho.services.sharepoint.graph.GraphClient;
import org.apache.olingo.client.api.ODataClient;
import javax.inject.Inject;
import java.net.URI;
import java.util.List;

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_V1;

public class DriveFacade {

    private final ODataClient client;
    private GraphClient graphClient;
    private MapperDrive mapperDrive;

    @Inject
    public DriveFacade(ODataClient client, MapperDrive mapperDrive) {
        this.client = client;
        this.graphClient = new GraphClient(client);
        this.mapperDrive = mapperDrive;
    }

    public Drive fetchDrive(String token, String path) {
        URI entityUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();

        return mapperDrive.getObject(graphClient.query(token, entityUri));
    }

    public List<Drive> fetchDrives(String token, String path) {
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();

        return mapperDrive.getObjects(graphClient.queryList(token, entitySetURI));
    }

    public List<DriveItem> fetchDriveItemsRoot(String token, String driveId) {
        String path = String.format("drives/%s/root/children", driveId);
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        MapperDriveItem objectMapperDriveItem = new MapperDriveItem(driveId, "root");

        return objectMapperDriveItem.getObjects(graphClient.queryList(token, entitySetURI));
    }

    public List<DriveItem> fetchDriveItems(String token, String driveId, String parentDriveItemId) {
        String path = String.format("drives/%s/items/%s/children", driveId, parentDriveItemId);
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        MapperDriveItem objectMapperDriveItem = new MapperDriveItem(driveId, parentDriveItemId);

        return objectMapperDriveItem.getObjects(graphClient.queryList(token, entitySetURI));
    }

    public DriveItem fetchDriveItem (String token, String driveId, String itemId) {
        String urlEntity = String.format("drives/%s/items/%s", driveId, itemId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();
        MapperDriveItem objectMapperDriveItem = new MapperDriveItem(driveId, "");

        return objectMapperDriveItem.getObject(graphClient.query(token, uri));
    }

    // todo create action delete file
    public void deleteFile (String token, String driveId, String fileId) {
        String urlEntity = String.format("drives/%s/items/%s", driveId, fileId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();

        graphClient.executeDelete(token, uri);
    }

}
