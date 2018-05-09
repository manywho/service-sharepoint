package com.manywho.services.sharepoint.users;

import com.google.inject.Inject;
import com.manywho.services.sharepoint.client.GraphClient;
import com.manywho.services.sharepoint.configuration.ApiConstants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UserClientOdata {
    private final ODataClient client;
    private GraphClient graphClient;

    @Inject
    public UserClientOdata(ODataClient client) {
        this.client = client;
        this.graphClient = new GraphClient(client);
    }

    public List<User> fetchUsers(String token) {
        URI uri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment("users").build();
        List<User> users = new ArrayList<>();
        UserMapper userMapper = new UserMapper();

        for (ClientEntity userEntity : graphClient.queryList(token, uri)) {
            users.add(userMapper.buildManyWhoUserObject(userEntity));
        }

        return users;
    }

    public String getUserPrincipalName(String token) {
        URI uri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment("me").build();

        return graphClient.query(token, uri).getProperty("userPrincipalName").getValue().toString();
    }
}
