package com.manywho.services.sharepoint.drives.items;

import com.manywho.services.sharepoint.graph.ObjectMapperBase;
import org.apache.olingo.client.api.domain.ClientEntity;

public class MapperDriveItem extends ObjectMapperBase<DriveItem> {

    private String driveId;
    private String parentDriveItemId;

    public MapperDriveItem(String driveId, String parentDriveItemId) {
        this.driveId = driveId;
        this.parentDriveItemId = parentDriveItemId;
    }

    public DriveItem getObject(ClientEntity driveItemEntity) {

            DriveItem item = new DriveItem();
            item.setId(String.format("drives/%s/items/%s", driveId, driveItemEntity.getProperty("id").getValue()));

            item.setDriveId(driveId);
            if (driveItemEntity.getProperty("folder") != null) {
                item.setType("folder");
            } else if (driveItemEntity.getProperty("file") != null) {
                item.setType("file");
            } else if (driveItemEntity.getProperty("image") != null) {
                item.setType("image");
            } else if (driveItemEntity.getProperty("photo") != null) {
                item.setType("photo");
            } else {
                item.setType("unknown");
            }

            item.setDriveItemParent(parentDriveItemId);

            item.setName(driveItemEntity.getProperty("name").getValue().toString());

            return item;
    }
}
