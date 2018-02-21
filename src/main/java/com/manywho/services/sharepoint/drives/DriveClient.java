package com.manywho.services.sharepoint.drives;

import com.manywho.services.sharepoint.configuration.ApiConstants;
import com.manywho.services.sharepoint.drives.items.DriveItem;
import com.manywho.services.sharepoint.drives.items.DriveItemMapper;
import com.manywho.services.sharepoint.client.GraphClient;
import org.apache.olingo.client.api.ODataClient;
import javax.inject.Inject;
import java.net.URI;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class DriveClient {

    private final ODataClient client;
    private GraphClient graphClient;
    private DriveMapper driveMapper;

    @Inject
    public DriveClient(ODataClient client, DriveMapper driveMapper) {
        this.client = client;
        this.graphClient = new GraphClient(client);
        this.driveMapper = driveMapper;
    }

    public Drive fetchDrive(String token, String path) {
        URI entityUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();

        return driveMapper.getObject(graphClient.query(token, entityUri));
    }

    public List<Drive> fetchDrives(String token, String path) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();

        return driveMapper.getObjects(graphClient.queryList(token, uri));
    }

    public List<DriveItem> fetchDriveItemsRoot(String token, String driveId) {
        String path = String.format("drives/%s/root/children", driveId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        DriveItemMapper objectDriveItemMapper = new DriveItemMapper(driveId, "root");

        return objectDriveItemMapper.getObjects(graphClient.queryList(token, uri));
    }

    public List<DriveItem> fetchDriveItems(String token, String driveId, String parentDriveItemId) {
        String path = String.format("drives/%s/items/%s/children", driveId, parentDriveItemId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();
        DriveItemMapper objectDriveItemMapper = new DriveItemMapper(driveId, parentDriveItemId);

        return objectDriveItemMapper.getObjects(graphClient.queryList(token, uri));
    }

    public DriveItem fetchDriveItem (String token, String driveId, String itemId) {
        String urlEntity = String.format("drives/%s/items/%s", driveId, itemId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();
        DriveItemMapper objectDriveItemMapper = new DriveItemMapper(driveId, "");

        return objectDriveItemMapper.getObject(graphClient.query(token, uri));
    }

    // todo create action delete file
    public void deleteFile (String token, String driveId, String fileId) {
        String urlEntity = String.format("drives/%s/items/%s", driveId, fileId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();

        graphClient.executeDelete(token, uri);
    }

}
