package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.database.lists.SharepointListManager;
import com.manywho.services.sharepoint.types.SharePointList;

import java.util.List;

public class SharepointListDatabase implements Database<ServiceConfiguration, SharePointList> {

    private SharepointListManager listManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public SharepointListDatabase(SharepointListManager listManager, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.listManager = listManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public SharePointList find(ServiceConfiguration configuration, String id) {
        return listManager.loadList(authenticatedWhoProvider.get(), configuration, id);
    }

    @Override
    public List<SharePointList> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return listManager.loadLists(authenticatedWhoProvider.get(), configuration, listFilter);
    }

    @Override
    public SharePointList create(ServiceConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> create(ServiceConfiguration configuration, List<SharePointList> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, SharePointList sharePointList) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<SharePointList> list) {

    }

    @Override
    public SharePointList update(ServiceConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> update(ServiceConfiguration configuration, List<SharePointList> list) {
        return null;
    }
}
