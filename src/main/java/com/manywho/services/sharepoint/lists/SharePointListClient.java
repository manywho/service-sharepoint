package com.manywho.services.sharepoint.lists;

import com.google.inject.Inject;
import com.manywho.services.sharepoint.client.GraphClient;
import com.manywho.services.sharepoint.lists.items.SharePointListItemMapper;
import com.manywho.services.sharepoint.lists.items.SharePointListItem;
import org.apache.olingo.client.api.ODataClient;

import java.net.URI;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class SharePointListClient {
    private final ODataClient client;
    private GraphClient graphClient;

    @Inject
    public SharePointListClient(ODataClient client) {
        this.client = client;
        this.graphClient = new GraphClient(client);
    }

    public SharePointList fetchList(String token, String idSite, String idList) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists/%s", idSite, idList)).build();

        SharePointListMapper sharePointListMapper = new SharePointListMapper(idSite);

        return sharePointListMapper.getObject(graphClient.query(token, uri));
    }

    public List<SharePointList> fetchListsRoot(String token) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment("sites/root/lists").build();

        SharePointListMapper sharePointListMapper = new SharePointListMapper(null);

        return sharePointListMapper.getObjects(graphClient.queryList(token, uri));
    }

    public List<SharePointList> fetchLists(String token, String idSite, boolean fullType) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists", idSite))
                .build();

        SharePointListMapper sharePointListMapper = new SharePointListMapper(idSite);

        return sharePointListMapper.getObjects(graphClient.queryList(token, uri));
    }


    public SharePointListItem fetchItem(String token, String siteId, String listId, String itemId) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId))
                .build();

        SharePointListItemMapper mapperSharePointListItem = new SharePointListItemMapper(siteId, listId);

        return mapperSharePointListItem.getObject(graphClient.query(token, uri));
    }

    public List<SharePointListItem> fetchItems(String token, String listIdUnique) {
        String siteId = IdExtractorForLists.extractSiteId(listIdUnique);
        String listId = IdExtractorForLists.extractListId(listIdUnique);
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);

        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(urlEntity)
                .build();

        SharePointListItemMapper mapperSharePointList = new SharePointListItemMapper(siteId, listId);

        return mapperSharePointList.getObjects(graphClient.queryList(token, uri));
    }

    public void deleteTypeList(String token, String siteId, String listId, String itemId) {
        String urlEntity = String.format("sites/%s/lists/%s/%s", siteId, listId, itemId);
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();

        graphClient.executeDelete(token, uri);
    }
}
