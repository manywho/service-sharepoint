package com.manywho.services.sharepoint.groups;

import org.apache.olingo.client.api.domain.ClientEntity;

public class GroupMapper {

    static public Group buildManyWhoGroupObject(ClientEntity groupEntity) {
        Group group = new Group();

        group.setId( "groups/" + groupEntity.getProperty("id").getValue().toString());
        group.setDescription(groupEntity.getProperty("description").getValue().toString());
        group.setDisplayName(groupEntity.getProperty("displayName").getValue().toString());

        return group;
    }
}
