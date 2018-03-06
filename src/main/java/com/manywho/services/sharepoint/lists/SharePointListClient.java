package com.manywho.services.sharepoint.lists;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.sharepoint.client.GraphClient;
import com.manywho.services.sharepoint.client.OdataPaginator;
import com.manywho.services.sharepoint.lists.items.SharePointListItemMapper;
import com.manywho.services.sharepoint.lists.items.SharePointListItem;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.uri.URIBuilder;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class SharePointListClient {
    private final ODataClient client;
    private GraphClient graphClient;
    private OdataPaginator odataPaginator;

    @Inject
    public SharePointListClient(ODataClient client, OdataPaginator odataPaginator) {
        this.client = client;
        this.graphClient = new GraphClient(client);
        this.odataPaginator = odataPaginator;
    }

    public SharePointList fetchList(String token, String idSite, String idList) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists/%s", idSite, idList)).build();

        SharePointListMapper sharePointListMapper = new SharePointListMapper(idSite);

        return sharePointListMapper.getObject(graphClient.query(token, uri));
    }

    public List<SharePointList> fetchListsRoot(String token, ListFilter listFilter) {
        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment("sites/root/lists");

        SharePointListMapper sharePointListMapper = new SharePointListMapper(null);
        List<ClientEntity> lists = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return sharePointListMapper.getObjects(lists);
    }

    public List<SharePointList> fetchLists(String token, String idSite, boolean fullType, ListFilter listFilter) {
        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists", idSite));

        SharePointListMapper sharePointListMapper = new SharePointListMapper(idSite);
        List<ClientEntity> lists = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return sharePointListMapper.getObjects(lists);
    }


    public SharePointListItem fetchItem(String token, String siteId, String listId, String itemId) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId))
                .build();

        SharePointListItemMapper mapperSharePointListItem = new SharePointListItemMapper(siteId, listId);

        return mapperSharePointListItem.getObject(graphClient.query(token, uri));
    }

    public List<SharePointListItem> fetchItems(String token, String listIdUnique, ListFilter listFilter) {
        String siteId = IdExtractorForLists.extractSiteId(listIdUnique);
        String listId = IdExtractorForLists.extractListId(listIdUnique);
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);

        URIBuilder uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(urlEntity);

        SharePointListItemMapper mapperSharePointList = new SharePointListItemMapper(siteId, listId);
        List<ClientEntity> items = odataPaginator.getEntities(token, uri, listFilter, client.getRetrieveRequestFactory());

        return mapperSharePointList.getObjects(items);
    }

    public void deleteTypeList(String token, String siteId, String listId, String itemId) {
        String urlEntity = String.format("sites/%s/lists/%s/%s", siteId, listId, itemId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();

        graphClient.executeDelete(token, uri);
    }
}
