package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.files.facade.DriveFacadeOdata;
import com.manywho.services.sharepoint.types.Drive;

import java.util.List;

public class DriveDatabase implements Database<ServiceConfiguration, Drive> {

    private DriveFacadeOdata driveFacade;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public DriveDatabase(DriveFacadeOdata driveFacade, TokenCompatibility tokenCompatibility) {

        this.driveFacade = driveFacade;
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public Drive find(ServiceConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Drive> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        tokenCompatibility.addinTokenNotSupported(configuration, "search drive");

        return driveFacade.fetchDrives(configuration, tokenCompatibility.getToken(configuration));
    }

    @Override
    public Drive create(ServiceConfiguration configuration, Drive drive) {
        return null;
    }

    @Override
    public List<Drive> create(ServiceConfiguration configuration, List<Drive> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, Drive drive) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Drive> list) {

    }

    @Override
    public Drive update(ServiceConfiguration configuration, Drive drive) {
        return null;
    }

    @Override
    public List<Drive> update(ServiceConfiguration configuration, List<Drive> list) {
        return null;
    }
}
