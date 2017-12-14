package com.manywho.services.sharepoint.files;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.files.facade.DriveFacade;
import com.manywho.services.sharepoint.files.types.Drive;

import java.util.List;

public class DriveDatabase implements Database<ServiceConfiguration, Drive> {

    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private DriveFacade driveFacade;

    @Inject
    public DriveDatabase(AuthenticatedWhoProvider authenticatedWhoProvider, DriveFacade driveFacade) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.driveFacade = driveFacade;
    }

    @Override
    public Drive find(ServiceConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Drive> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return driveFacade.fetchDrives(configuration, authenticatedWhoProvider.get().getToken());
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
