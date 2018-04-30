package com.manywho.services.sharepoint.groups;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.sharepoint.configuration.ApiConstants;
import com.manywho.services.sharepoint.client.GraphClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupClient {
    private final ODataClient client;
    private GraphClient graphClient;

    @Inject
    public GroupClient(ODataClient client) {
        this.client = client;
        this.graphClient = new GraphClient(client);
    }

    public List<Group> fetchGroups(String token, ListFilter listFilter) {

        URI uri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment("groups?$filter=groupTypes/any(c:c+eq+'Unified')")
                .build();

        List<Group> groups = new ArrayList<>();

        for (ClientEntity groupEntity : graphClient.queryList(token, uri)) {
            groups.add(GroupMapper.buildManyWhoGroupObject(groupEntity));
        }

        return groups;
    }

    public List<Group> fetchUserGroups(String token, String userPrincipalName) {

        URI uri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment("users")
                .appendEntitySetSegment(userPrincipalName)
                .appendActionCallSegment("memberOf")
                .build();

        return graphClient.queryList(token, uri).stream()
                .map(GroupMapper::buildManyWhoGroupObject)
                .collect(Collectors.toList());
    }
}
