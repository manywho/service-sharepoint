package com.manywho.services.sharepoint.drive;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.drive.facade.DriveFacade;
import com.manywho.services.sharepoint.drive.types.Drive;

import java.util.List;

public class DriveDatabase implements Database<ApplicationConfiguration, Drive> {

    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private DriveFacade driveFacade;

    @Inject
    public DriveDatabase(AuthenticatedWhoProvider authenticatedWhoProvider, DriveFacade driveFacade) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.driveFacade = driveFacade;
    }

    @Override
    public Drive find(ApplicationConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Drive> findAll(ApplicationConfiguration configuration, ListFilter listFilter) {
        return driveFacade.fetchDrives(configuration, authenticatedWhoProvider.get().getToken());
    }

    @Override
    public Drive create(ApplicationConfiguration configuration, Drive drive) {
        return null;
    }

    @Override
    public List<Drive> create(ApplicationConfiguration configuration, List<Drive> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, Drive drive) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<Drive> list) {

    }

    @Override
    public Drive update(ApplicationConfiguration configuration, Drive drive) {
        return null;
    }

    @Override
    public List<Drive> update(ApplicationConfiguration configuration, List<Drive> list) {
        return null;
    }
}
