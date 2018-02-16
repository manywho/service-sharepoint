package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.files.upload.facade.DriveFacadeOdata;
import com.manywho.services.sharepoint.files.upload.utilities.FileIdExtractor;
import com.manywho.services.sharepoint.types.DriveItem;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DriveItemDatabase implements Database<ServiceConfiguration, DriveItem> {

    private DriveFacadeOdata driveFacade;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public DriveItemDatabase(DriveFacadeOdata driveFacade, TokenCompatibility tokenCompatibility) {
        this.driveFacade = driveFacade;
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public DriveItem find(ServiceConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<DriveItem> findAll(ServiceConfiguration configuration, ListFilter listFilter) {

        tokenCompatibility.addinTokenNotSupported(configuration, "search drive Item");

        if (listFilter != null && listFilter.getWhere() != null) {
            Optional<ListFilterWhere> drive  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Drive ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            Optional<ListFilterWhere> driveItemId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (!drive.isPresent()) {
                throw new RuntimeException("Drive ID is mandatory");
            }

            String token = tokenCompatibility.getToken(configuration);
            String driveId = drive.get().getContentValue().replace("drives/", "");

            if (!driveItemId.isPresent()) {

                return driveFacade.fetchDriveItemsRoot(configuration, token, driveId);
            } else {
                String parentItemId = FileIdExtractor.extractDriveItemIdFromUniqueId(driveItemId.get().getContentValue());

                return driveFacade.fetchDriveItems(configuration, token, driveId, parentItemId);
            }
        }

        throw new RuntimeException("Filter not found");
    }

    @Override
    public DriveItem create(ServiceConfiguration configuration, DriveItem drive) {
        return null;
    }

    @Override
    public List<DriveItem> create(ServiceConfiguration configuration, List<DriveItem> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, DriveItem drive) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<DriveItem> list) {

    }

    @Override
    public DriveItem update(ServiceConfiguration configuration, DriveItem drive) {
        return null;
    }

    @Override
    public List<DriveItem> update(ServiceConfiguration configuration, List<DriveItem> list) {
        return null;
    }

}
