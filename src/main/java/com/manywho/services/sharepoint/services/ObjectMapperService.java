package com.manywho.services.sharepoint.services;

import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;
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
    public Object buildManyWhoFileSystemObject(ClientEntity fileItem) {
        MObject object = new MObject();
        List<Property> properties = new ArrayList<>();
//        properties.add(new Property("Kind", FilenameUtils.getExtension(fileItem.getName())));
        properties.add(new Property("ID", fileItem.getProperty("id").getValue().toString()));
//        properties.add(new Property("Mime Type"));
//        properties.add(new Property("Name", fileItem.getProperty("name").getValue().toString()));
//        properties.add(new Property("Description", fileItem.getProperty("name").getValue().toString()));
//        properties.add(new Property("Date Created", fileItem.getDateTimeCreated()));
//        properties.add(new Property("Date Modified", fileItem.getDateTimeLastModified()));
//        properties.add(new Property("Download Uri", fileItem.getWebUrl()));
//        properties.add(new Property("Embed Uri"));
        properties.add(new Property("Icon Uri"));

        object.setDeveloperName("$File");
        object.setExternalId(fileItem.getProperty("id").getValue().toString());
        object.setProperties(properties);

        return object;
    }


    public MObject buildManyWhoSiteObject(ClientEntity siteItem, String parentId) {
        List<Property> properties = new ArrayList<>();

        properties.add(new Property("ID", siteItem.getProperty("id").getValue().toString()));
        properties.add(new Property("Created Date Time", siteItem.getProperty("createdDateTime").getValue().toString()));
        properties.add(new Property("Last Modified Date Time", siteItem.getProperty("lastModifiedDateTime").getValue().toString()));
        properties.add(new Property("Description", siteItem.getProperty("description").getValue().toString()));
        properties.add(new Property("Name", siteItem.getProperty("name").getValue().toString()));
        properties.add(new Property("Web URL", siteItem.getProperty("webUrl").getValue().toString()));
        properties.add(new Property("Site ID", siteItem.getProperty("id").getValue().toString()));
        properties.add(new Property("Parent ID", parentId));

        MObject object = new MObject();
        object.setDeveloperName(Site.NAME);
        object.setExternalId(siteItem.getProperty("id").getValue().toString());
        object.setProperties(properties);

        return object;
    }

    public SharePointList buildManyWhoSharePointListObject(ClientEntity sharepointListEntity, String siteId) {
        SharePointList sharePointList = new SharePointList();

        sharePointList.setListId(sharepointListEntity.getProperty("id").getValue().toString());
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
        sharePointList.setId(String.format("%s#%s", sharePointList.getSiteId(), sharePointList.getListId()));

        return sharePointList;
    }

    public SharePointList buildManyWhoSharePointListObject(SPList listEntity, String siteId) {
        SharePointList list = new SharePointList();
        list.setListId(listEntity.getId());
        list.setCreatedDateTime(listEntity.getData("Created").toString());
        list.setModifiedDateTime(listEntity.getData("LastItemUserModifiedDate").toString());
        list.setDescription(listEntity.getData("Description").toString());
        list.setName(listEntity.getData("Title").toString());

        java.lang.Object metadata =listEntity.getData("__metadata");
        String url = null;

        try {
            url = ((JSONObject) metadata).get("id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.setSiteId(siteId);
        list.setWebUrl(url);
        list.setId(String.format("%s#%s", listEntity.getId(), siteId));

        list.setListId(listEntity.getId());

        return list;
    }


    public MObject buildManyWhoItemObject(ClientEntity siteEntity, String siteId, String listId) {
        List<Property> properties = new ArrayList<>();

        properties.add(new Property("Item ID", siteEntity.getProperty("id").getValue().toString()));
        properties.add(new Property("Created Date Time", siteEntity.getProperty("createdDateTime").getValue().toString()));
        properties.add(new Property("Last Modified Date Time", siteEntity.getProperty("lastModifiedDateTime").getValue().toString()));
        properties.add(new Property("e Tag", siteEntity.getProperty("eTag").getValue().toString()));
        properties.add(new Property("Web URL", siteEntity.getProperty("webUrl").getValue().toString()));
        properties.add(new Property("List Item ID", siteEntity.getProperty("listItemId").getValue().toString()));
        properties.add(new Property("Site ID", siteId));
        properties.add(new Property("List ID", listId));

        MObject object = new MObject();
        object.setDeveloperName(Item.NAME);
        object.setExternalId(String.format("%s#%s", listId, siteEntity.getProperty("id").getValue().toString()));
        object.setProperties(properties);

        return object;
    }

    public MObject buildManyWhoDynamicObject(List<ClientProperty> clientProperties, List<ObjectDataTypeProperty> properties) {
        List<Property> mobjectProperties = new ArrayList<>();
        String externalId = null;

        for (ObjectDataTypeProperty property: properties) {
            if (Objects.equals(property.getDeveloperName(), "ID")) {
                Optional<ClientProperty> id = clientProperties.stream().filter(p -> Objects.equals(p.getName(), "id")).findFirst();
                if (id.isPresent()) {
                    externalId = id.get().getValue().toString();
                    mobjectProperties.add(new Property("ID", externalId));
                }
            } else {
                Optional<ClientProperty> foundProperty = clientProperties.stream()
                        .filter(p -> Objects.equals(p.getName(), property.getDeveloperName())).findFirst();

                if (foundProperty.isPresent()) {
                    mobjectProperties.add(new Property(property.getDeveloperName(), foundProperty.get().getValue().asPrimitive().toValue()));
                }
            }
        }

        MObject object = new MObject();
        object.setDeveloperName(Item.NAME);
        object.setExternalId(externalId);
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

//
//    public Object buildManyWhoObjectFile(File file, String content) {
//        PropertyCollection properties = new PropertyCollection();
//        properties.add(new Property("ID", file.getServerRelativeUrl()));
//        properties.add(new Property("Name", file.getName()));
//        properties.add(new Property("Description", file.getTitle()));
//        properties.add(new Property("Content", content));
//
//        properties.add(new Property("Parent Folder", content));
//        properties.add(new Property("Comments", content));
//
//        properties.add(new Property("Created At", file.getCreatedTime()));
//        properties.add(new Property("Modified At", file.getLastModifiedTime()));
//
//        Object object = new Object();
//        object.setDeveloperName(com.manywho.services.sharepoint.types.File.NAME);
//        object.setExternalId(file.getServerRelativeUrl());
//        object.setProperties(properties);
//
//        return object;
//    }
//
//    public MObject buildManyWhoObjectFolder(Folder folderSharepoint) {
//        SharePointList<com.manywho.services.sharepoint.types.File> files = null;
//
//
//        PropertyCollection properties = new PropertyCollection();
//        properties.add(new Property("ID", folderSharepoint.getServerRelativeUrl()));
//        properties.add(new Property("Name", folderSharepoint.getName()));
//        properties.add(new Property("Description", folderSharepoint.getName()));
//        properties.add(new Property("Files", files));
//        properties.add(new Property("Created At", folderSharepoint.getCreatedTime()));
//        properties.add(new Property("Modified At", folderSharepoint.getLastModifiedTime()));
//
//        Object object = new Object();
//        object.setDeveloperName(com.manywho.services.sharepoint.types.Folder.NAME);
//        object.setExternalId(folderSharepoint.getServerRelativeUrl());
//        object.setProperties(properties);
//
//        return object;
//    }
}
