package com.manywho.services.sharepoint.groups;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;

import java.util.List;

public class GroupDatabase implements Database<ServiceConfiguration, Group> {
    private TokenManager tokenManager;
    private GroupClient groupClient;

    @Inject
    public GroupDatabase(TokenManager tokenManager, GroupClient groupClient) {
        this.tokenManager = tokenManager;
        this.groupClient = groupClient;
    }

    @Override
    public Group find(ServiceConfiguration configuration, String s) {
        throw new RuntimeException("Find a Group is not currently supported");
    }

    @Override
    public List<Group> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return groupClient
                .fetchGroups(tokenManager.getToken(configuration), listFilter);
    }

    @Override
    public Group create(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Create Group is not currently supported");
    }

    @Override
    public List<Group> create(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Create a list of Groups us not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Delete a Group is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Delete a list of Groups is not currently supported");
    }

    @Override
    public Group update(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Update a Group is not currently supported");
    }

    @Override
    public List<Group> update(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Update a list of Groups is not currently supported");
    }
}
