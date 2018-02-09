package com.manywho.services.sharepoint.mapper;

import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Drive;
import com.manywho.services.sharepoint.types.DriveItem;
import com.manywho.services.sharepoint.types.*;
import com.microsoft.services.sharepoint.SPList;
import com.microsoft.services.sharepoint.SPListItem;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObjectMapperService {

    public Site buildManyWhoSiteObject(ClientEntity siteItem, String parentId) {
        Site site = new Site();

        site.setId("sites/" + siteItem.getProperty("id").getValue().toString());
        site.setCreatedDateTime(siteItem.getProperty("createdDateTime").getValue().toString());
        site.setModifiedDateTime(siteItem.getProperty("lastModifiedDateTime").getValue().toString());
        site.setDescription(siteItem.getProperty("description").getValue().toString());
        site.setName(siteItem.getProperty("name").getValue().toString());
        site.setParentId(parentId);

        if(Strings.isNullOrEmpty(parentId)) {
            site.setParentId("root");
        }

        site.setWebUrl(siteItem.getProperty("webUrl").getValue().toString());

        return site;
    }

    public SharePointList buildManyWhoSharePointListObject(ClientEntity sharepointListEntity, String siteId) {
        SharePointList sharePointList = new SharePointList();
        sharePointList.setCreatedDateTime(sharepointListEntity.getProperty("createdDateTime").getValue().toString());
        sharePointList.setModifiedDateTime(sharepointListEntity.getProperty("lastModifiedDateTime").getValue().toString());
        sharePointList.setDescription(sharepointListEntity.getProperty("description").getValue().toString());
        sharePointList.setName(sharepointListEntity.getProperty("name").getValue().toString());
        sharePointList.setWebUrl(sharepointListEntity.getProperty("webUrl").getValue().toString());

        if (Strings.isNullOrEmpty(siteId)) {
            sharePointList.setSiteId("root");
        } else {
            sharePointList.setSiteId(siteId);
        }

        sharePointList.setId(String.format("sites/%s/lists/%s", sharePointList.getSiteId(), sharepointListEntity.getProperty("id").getValue().toString()));

        return sharePointList;
    }

    public SharePointList buildManyWhoSharePointListObject(SPList listEntity, String siteId) {
        SharePointList list = new SharePointList();
        list.setCreatedDateTime(listEntity.getData("Created").toString());
        list.setModifiedDateTime(listEntity.getData("LastItemUserModifiedDate").toString());
        list.setDescription(listEntity.getData("Description").toString());
        list.setName(listEntity.getData("Title").toString());

        java.lang.Object metadata = listEntity.getData("__metadata");

        list.setSiteId(siteId);
        try {
            list.setWebUrl(((JSONObject) metadata).get("id").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list.setId(String.format("sites/%s/lists/%s", listEntity.getId(), siteId));

        return list;
    }

    public SharePointListItem buildManyWhoItemObject(ClientEntity itemEntity, String siteId, String listId) {

        SharePointListItem sharePointListItem = new SharePointListItem();

        sharePointListItem.setCreatedDateTime(itemEntity.getProperty("createdDateTime").getValue().toString());
        sharePointListItem.setModifiedDateTime(itemEntity.getProperty("lastModifiedDateTime").getValue().toString());
        sharePointListItem.setWebUrl(itemEntity.getProperty("webUrl").getValue().toString());
        sharePointListItem.setSiteId(siteId);
        sharePointListItem.setListId(listId);
        sharePointListItem.setId(String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemEntity.getProperty("id").getValue().toString()));

        return sharePointListItem;
    }

    public MObject buildManyWhoDynamicObject(String uniqueId, List<ClientProperty> clientProperties, List<ObjectDataTypeProperty> properties) {
        MObject object = new MObject();
        object.setDeveloperName(SharePointListItem.NAME);
        List<Property> mobjectProperties = new ArrayList<>();

        for (ObjectDataTypeProperty property: properties) {
            if (Objects.equals(property.getDeveloperName(), "ID")) {
                    object.setExternalId(uniqueId);
                    mobjectProperties.add(new Property("ID", object.getExternalId()));
            } else {
                Optional<ClientProperty> foundProperty = clientProperties.stream()
                        .filter(p -> Objects.equals(p.getName(), property.getDeveloperName())).findFirst();

                if (foundProperty.isPresent()) {
                    mobjectProperties.add(new Property(property.getDeveloperName(), foundProperty.get().getValue().asPrimitive().toValue()));
                } else {
                    mobjectProperties.add(new Property(property.getDeveloperName(), ""));
                }
            }
        }

        object.setProperties(mobjectProperties);

        return object;
    }

    public MObject buildManyWhoDynamicObject(String developerName, SPListItem spListItem, List<ObjectDataTypeProperty> properties) {
        MObject object = new MObject();
        List<Property> mobjectProperties = new ArrayList<>();
        String externalId = "";

        for (ObjectDataTypeProperty property: properties) {
            externalId = spListItem.getGUID();

            if (Objects.equals(property.getDeveloperName(), "ID")) {
                mobjectProperties.add(new Property("ID", externalId));
            } else {
                mobjectProperties.add(new Property(property.getDeveloperName(), spListItem.getData(property.getDeveloperName())));
            }
        }

        object.setDeveloperName(developerName);
        object.setExternalId(externalId);
        object.setProperties(mobjectProperties);

        return object;
    }

    public Drive buildManyWhoDriveObject(ClientEntity itemEntity) {

        Drive item = new Drive();

        item.setId("drives/" + itemEntity.getProperty("id").getValue().toString());
        item.setDriveType(itemEntity.getProperty("driveType").getValue().toString());
        item.setName(itemEntity.getProperty("name").getValue().toString());

        return item;
    }

    public DriveItem buildManyWhoDriveItemObject(ClientEntity driveItemEntity, String driveId, String parentDriveItemId) {

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

    public Group buildManyWhoGroupObject(ClientEntity groupEntity) {
        Group group = new Group();

        group.setId( "groups/" + groupEntity.getProperty("id").getValue().toString());
        group.setDescription(groupEntity.getProperty("description").getValue().toString());
        group.setDisplayName(groupEntity.getProperty("displayName").getValue().toString());
        
        return group;
    }

    public User buildManyWhoUserObject(ClientEntity userEntity) {
        User group = new User();

        group.setId( userEntity.getProperty("id").getValue().toString());
        group.setDisplayName(userEntity.getProperty("displayName").getValue().toString());
        group.setGivenName(userEntity.getProperty("givenName").getValue().toString());
        group.setJobTitle(userEntity.getProperty("jobTitle").getValue().toString());
        group.setMail(userEntity.getProperty("mail").getValue().toString());
        group.setMobilePhone(userEntity.getProperty("mobilePhone").getValue().toString());
        group.setOfficeLocation(userEntity.getProperty("officeLocation").getValue().toString());
        group.setPreferredLanguage(userEntity.getProperty("preferredLanguage").getValue().toString());
        group.setSurname(userEntity.getProperty("surname").getValue().toString());
        group.setUserPrincipalName(userEntity.getProperty("userPrincipalName").getValue().toString());

        return group;
    }
}