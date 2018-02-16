package com.manywho.services.sharepoint.groups;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;

import java.util.List;

public class GroupDatabase implements Database<ServiceConfiguration, Group> {
    private TokenCompatibility tokenCompatibility;
    @Inject
    public GroupDatabase(TokenCompatibility tokenCompatibility) {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public Group find(ServiceConfiguration configuration, String s) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public List<Group> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchGroups(configuration, tokenCompatibility.getToken(configuration), listFilter);
    }

    @Override
    public Group create(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public List<Group> create(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public Group update(ServiceConfiguration configuration, Group group) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public List<Group> update(ServiceConfiguration configuration, List<Group> list) {
        throw new RuntimeException("Method not supported");
    }
}
