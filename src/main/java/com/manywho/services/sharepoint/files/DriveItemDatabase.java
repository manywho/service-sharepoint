package com.manywho.services.sharepoint.files;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.files.facade.DriveFacade;
import com.manywho.services.sharepoint.files.types.DriveItem;
import com.manywho.services.sharepoint.files.utilities.FileIdExtractor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DriveItemDatabase implements Database<ApplicationConfiguration, DriveItem> {

    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private DriveFacade driveFacade;

    @Inject
    public DriveItemDatabase(AuthenticatedWhoProvider authenticatedWhoProvider, DriveFacade driveFacade) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.driveFacade = driveFacade;
    }

    @Override
    public DriveItem find(ApplicationConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<DriveItem> findAll(ApplicationConfiguration configuration, ListFilter listFilter) {

        if (listFilter != null && listFilter.getWhere() != null) {
            Optional<ListFilterWhere> driveId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Drive ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            Optional<ListFilterWhere> driveItemId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Drive Item Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (!driveId.isPresent()) {
                throw new RuntimeException("Drive ID is mandatory");
            }

            if (!driveItemId.isPresent()) {
                return driveFacade.fetchDriveItemsRoot(configuration, authenticatedWhoProvider.get().getToken(),
                        driveId.get().getContentValue());
            } else {
                String parentItemId = FileIdExtractor.extractDriveItemId(driveItemId.get().getContentValue());

                return driveFacade.fetchDriveItems(configuration, authenticatedWhoProvider.get().getToken(),
                        driveId.get().getContentValue(), parentItemId);
            }
        }

        throw new RuntimeException("Filter not found");
    }

    @Override
    public DriveItem create(ApplicationConfiguration configuration, DriveItem drive) {
        return null;
    }

    @Override
    public List<DriveItem> create(ApplicationConfiguration configuration, List<DriveItem> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, DriveItem drive) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<DriveItem> list) {

    }

    @Override
    public DriveItem update(ApplicationConfiguration configuration, DriveItem drive) {
        return null;
    }

    @Override
    public List<DriveItem> update(ApplicationConfiguration configuration, List<DriveItem> list) {
        return null;
    }
}
