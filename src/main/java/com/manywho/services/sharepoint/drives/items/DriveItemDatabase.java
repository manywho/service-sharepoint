package com.manywho.services.sharepoint.drives.items;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.drives.DriveFacade;
import com.manywho.services.sharepoint.files.FileIdExtractor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DriveItemDatabase implements Database<ServiceConfiguration, DriveItem> {

    private DriveFacade driveFacade;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public DriveItemDatabase(DriveFacade driveFacade, TokenCompatibility tokenCompatibility) {
        this.driveFacade = driveFacade;
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public DriveItem find(ServiceConfiguration configuration, String driveItemId)
    {
        String token = tokenCompatibility.getToken(configuration);

        return driveFacade.fetchDriveItem(token, IdExtractorForDriveItems.extractDriveId(driveItemId),
                IdExtractorForDriveItems.extractDriveItemId(driveItemId));
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
            String driveId = IdExtractorForDriveItems.extractDriveId(drive.get().getContentValue());

            if (!driveItemId.isPresent()) {

                return driveFacade.fetchDriveItemsRoot(token, driveId);
            } else {
                String parentItemId = FileIdExtractor.extractDriveItemIdFromUniqueId(driveItemId.get().getContentValue());

                return driveFacade.fetchDriveItems(token, driveId, parentItemId);
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
