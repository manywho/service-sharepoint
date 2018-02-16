package com.manywho.services.sharepoint.drives;

import com.manywho.services.sharepoint.graph.ObjectMapperBase;
import org.apache.olingo.client.api.domain.ClientEntity;
public class MapperDrive extends ObjectMapperBase<Drive> {

    public Drive getObject(ClientEntity itemEntity) {

        Drive drive = new Drive();

        drive.setId("drives/" + itemEntity.getProperty("id").getValue().toString());
        drive.setDriveType(itemEntity.getProperty("driveType").getValue().toString());
        drive.setName(itemEntity.getProperty("name").getValue().toString());

        return drive;
    }
}
