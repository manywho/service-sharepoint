package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.managers.ListManager;
import com.manywho.services.sharepoint.types.SharePointList;

import java.util.List;

public class SharepointListDatabase implements Database<ApplicationConfiguration, SharePointList> {

    private ListManager listManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public SharepointListDatabase(ListManager listManager, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.listManager = listManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public SharePointList find(ApplicationConfiguration configuration, String id) {
        return listManager.loadList(authenticatedWhoProvider.get(), configuration, id);
    }

    @Override
    public List<SharePointList> findAll(ApplicationConfiguration configuration, ListFilter listFilter) {
        return listManager.loadLists(authenticatedWhoProvider.get(), configuration, listFilter);
    }

    @Override
    public SharePointList create(ApplicationConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> create(ApplicationConfiguration configuration, List<SharePointList> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, SharePointList sharePointList) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<SharePointList> list) {

    }

    @Override
    public SharePointList update(ApplicationConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> update(ApplicationConfiguration configuration, List<SharePointList> list) {
        return null;
    }
}
