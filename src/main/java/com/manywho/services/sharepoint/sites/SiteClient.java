package com.manywho.services.sharepoint.sites;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.manywho.services.sharepoint.client.GraphClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class SiteClient {
    private final ODataClient client;
    private GraphClient graphClient;

    @Inject
    public SiteClient(ODataClient client) {
        this.client = client;
        this.graphClient = new GraphClient(client);
    }

    public List<Site> fetchSites(String token, String groupId){
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment("groups?$filter=groupTypes/any(c:c+eq+'Unified')")
                .build();

        List<ClientEntity> groups = graphClient.queryList(token, uri);
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        // we get the sites for each group
        for (ClientEntity group : groups) {
            if (Strings.isNullOrEmpty(groupId) || groupId.equals(group.getProperty("id").getValue().toString())) {
                listSitesByGroups.add(fetchSiteByGroup(token, group.getProperty("id").getValue().toString()));
            }
        }

        SiteMapper siteMapper = new SiteMapper(null, groupId);

        return siteMapper.getObjects(listSitesByGroups);
    }

    public Site fetchSite(String token, String id) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s", id))
                .build();

        SiteMapper siteMapper = new SiteMapper(null, null);

        return siteMapper.getObject(graphClient.query(token, uri));
    }

    public List<Site> fetchSubsites(String token, String parentId) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("sites/%s/sites", parentId))
                .build();

        SiteMapper siteMapper = new SiteMapper(parentId, null);

        return siteMapper.getObjects(graphClient.queryList(token, uri));
    }

    // return site for the team group
    public ClientEntity fetchSiteByGroup(String token, String groupId) {

        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(String.format("groups/%s/sites/root", groupId))
                .build();

        return graphClient.query(token, uri);
    }
}
