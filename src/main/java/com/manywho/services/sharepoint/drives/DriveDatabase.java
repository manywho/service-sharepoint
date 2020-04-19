package com.manywho.services.sharepoint.drives;

import com.google.inject.Inject;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;

import java.util.List;

public class DriveDatabase implements Database<ServiceConfiguration, Drive> {

    private DriveClient driveClient;
    private TokenManager tokenManager;

    @Inject
    public DriveDatabase(DriveClient driveClient, TokenManager tokenManager) {

        this.driveClient = driveClient;
        this.tokenManager = tokenManager;
    }

    @Override
    public Drive find(ServiceConfiguration configuration, ObjectDataType objectDataType, Command command, String driveId) {
        tokenManager.addinTokenNotSupported(configuration, "search drive");
        Drive drive = driveClient.fetchDrive(tokenManager.getToken(configuration), driveId);

        if (drive == null) {
            throw new RuntimeException(String.format("Drive with id: \"%s\" not foud", driveId));
        }

        return drive;
    }

    @Override
    public List<Drive> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter listFilter, List<MObject> objects) {
        tokenManager.addinTokenNotSupported(configuration, "search drive");

        //ToDo in future versions of engine the list filter will be provider with empty list and this check will not be needed
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        return driveClient.fetchDrives(tokenManager.getToken(configuration), "me/drives", listFilter);
    }

    @Override
    public Drive create(ServiceConfiguration configuration, ObjectDataType objectDataType, Drive drive) {
        throw new RuntimeException("Create a drive is not currently supported");
    }

    @Override
    public List<Drive> create(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Drive> list) {
        throw new RuntimeException("Create a list of drives is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, ObjectDataType objectDataType, Drive drive) {
        throw new RuntimeException("Delete a drive is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Drive> list) {
        throw new RuntimeException("Delete a list of drives is not currently supported");
    }

    @Override
    public Drive update(ServiceConfiguration configuration, ObjectDataType objectDataType, Drive drive) {
        throw new RuntimeException("Update a list of drives is not currently supported");
    }

    @Override
    public List<Drive> update(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Drive> list) {
        throw new RuntimeException("Update a drive is not currently supported");
    }
}
