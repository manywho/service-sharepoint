package com.manywho.services.sharepoint.drives;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.files.upload.facade.DriveFacadeOdata;

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
    public Drive find(ServiceConfiguration configuration, String driveId) {
        tokenCompatibility.addinTokenNotSupported(configuration, "search drive");
        Drive drive = driveFacade.fetchDrive(tokenCompatibility.getToken(configuration), "me/drive");

        if (drive == null) {
            throw new RuntimeException(String.format("Drive with id: \"%s\" not foud", driveId));
        }

        return drive;
    }

    @Override
    public List<Drive> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        tokenCompatibility.addinTokenNotSupported(configuration, "search drive");

        return driveFacade.fetchDrives(configuration, tokenCompatibility.getToken(configuration), "me/drives");
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