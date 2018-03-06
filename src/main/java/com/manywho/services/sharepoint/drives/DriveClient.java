package com.manywho.services.sharepoint.drives;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.sharepoint.client.OdataPaginator;
import com.manywho.services.sharepoint.configuration.ApiConstants;
import com.manywho.services.sharepoint.drives.items.DriveItem;
import com.manywho.services.sharepoint.drives.items.DriveItemMapper;
import com.manywho.services.sharepoint.client.GraphClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.uri.URIBuilder;

import javax.inject.Inject;
import javax.swing.text.html.parser.Entity;
import javax.ws.rs.client.Client;
import java.net.URI;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class DriveClient {

    private final ODataClient client;
    private GraphClient graphClient;
    private DriveMapper driveMapper;
    private OdataPaginator odataPaginator;

    @Inject
    public DriveClient(ODataClient client, DriveMapper driveMapper, OdataPaginator odataPaginator) {
        this.client = client;
        this.graphClient = new GraphClient(client);
        this.driveMapper = driveMapper;
        this.odataPaginator = odataPaginator;
    }

    public Drive fetchDrive(String token, String path) {
        URI entityUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(path).build();

        return driveMapper.getObject(graphClient.query(token, entityUri));
    }

    public List<Drive> fetchDrives(String token, String path, ListFilter listFilter) {
        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path);
        List<ClientEntity> drives = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return driveMapper.getObjects(drives);
    }

    public List<DriveItem> fetchDriveItemsRoot(String token, String driveId, ListFilter listFilter) {
        String path = String.format("drives/%s/root/children", driveId);
        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path);
        DriveItemMapper objectDriveItemMapper = new DriveItemMapper(driveId, "root");
        List<ClientEntity> driveItems = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return objectDriveItemMapper.getObjects(driveItems);
    }

    public List<DriveItem> fetchDriveItems(String token, String driveId, String parentDriveItemId, ListFilter listFilter) {
        String path = String.format("drives/%s/items/%s/children", driveId, parentDriveItemId);
        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(path);
        DriveItemMapper objectDriveItemMapper = new DriveItemMapper(driveId, parentDriveItemId);
        List<ClientEntity> driveItems = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return objectDriveItemMapper.getObjects(driveItems);
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
