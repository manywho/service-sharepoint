package com.manywho.services.sharepoint.drives;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;

import java.util.List;

public class DriveDatabase implements Database<ServiceConfiguration, Drive> {

    private DriveFacade driveFacade;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public DriveDatabase(DriveFacade driveFacade, TokenCompatibility tokenCompatibility) {

        this.driveFacade = driveFacade;
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public Drive find(ServiceConfiguration configuration, String driveId) {
        tokenCompatibility.addinTokenNotSupported(configuration, "search drive");
        Drive drive = driveFacade.fetchDrive(tokenCompatibility.getToken(configuration), driveId);

        if (drive == null) {
            throw new RuntimeException(String.format("Drive with id: \"%s\" not foud", driveId));
        }

        return drive;
    }

    @Override
    public List<Drive> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        tokenCompatibility.addinTokenNotSupported(configuration, "search drive");

        return driveFacade.fetchDrives(tokenCompatibility.getToken(configuration), "me/drives");
    }

    @Override
    public Drive create(ServiceConfiguration configuration, Drive drive) {
        throw new RuntimeException("Create a drive is not currently supported");
    }

    @Override
    public List<Drive> create(ServiceConfiguration configuration, List<Drive> list) {
        throw new RuntimeException("Create a list of drives is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, Drive drive) {
        throw new RuntimeException("Delete a drive is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Drive> list) {
        throw new RuntimeException("Delete a list of drives is not currently supported");
    }

    @Override
    public Drive update(ServiceConfiguration configuration, Drive drive) {
        throw new RuntimeException("Update a list of drives is not currently supported");
    }

    @Override
    public List<Drive> update(ServiceConfiguration configuration, List<Drive> list) {
        throw new RuntimeException("Update a drive is not currently supported");
    }
}
