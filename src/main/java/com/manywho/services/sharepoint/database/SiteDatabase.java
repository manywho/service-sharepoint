package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.database.sites.SiteManager;
import com.manywho.services.sharepoint.types.Site;

import java.util.List;

public class SiteDatabase implements Database<ServiceConfiguration, Site> {
    private SiteManager siteManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public SiteDatabase(SiteManager siteManager, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.siteManager = siteManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public Site find(ServiceConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Site> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return siteManager.loadSites(authenticatedWhoProvider.get(), configuration, listFilter);
    }

    @Override
    public Site create(ServiceConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> create(ServiceConfiguration configuration, List<Site> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, Site site) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Site> list) {

    }

    @Override
    public Site update(ServiceConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> update(ServiceConfiguration configuration, List<Site> list) {
        return null;
    }
}
