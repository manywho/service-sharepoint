package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;
import com.manywho.services.sharepoint.types.Group;

import java.util.List;

public class GroupDatabase implements Database<ServiceConfiguration, Group> {
    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private SharePointFacadeInterface sharePointFacadeInterface;

    @Inject
    public GroupDatabase(SharepointFacadeFactory sharepointFacadeFactory, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.sharePointFacadeInterface = sharepointFacadeFactory.get(authenticatedWhoProvider.get().getIdentityProvider());
    }

    @Override
    public Group find(ServiceConfiguration configuration, String s) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public List<Group> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return sharePointFacadeInterface.fetchGroups(configuration, authenticatedWhoProvider.get().getToken(), listFilter);
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
