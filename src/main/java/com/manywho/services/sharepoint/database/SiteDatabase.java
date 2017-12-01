package com.manywho.services.sharepoint.database;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.types.Site;

import java.util.List;

public class SiteDatabase implements Database<ApplicationConfiguration, Site>{
    @Override
    public Site find(ApplicationConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Site> findAll(ApplicationConfiguration configuration, ListFilter listFilter) {
        return null;
    }

    @Override
    public Site create(ApplicationConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> create(ApplicationConfiguration configuration, List<Site> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, Site site) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<Site> list) {

    }

    @Override
    public Site update(ApplicationConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> update(ApplicationConfiguration configuration, List<Site> list) {
        return null;
    }
}
