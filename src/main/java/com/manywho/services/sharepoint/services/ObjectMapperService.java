package com.manywho.services.sharepoint.services;

import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;
import com.microsoft.services.sharepoint.SPList;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class ObjectMapperService {
    public Object buildManyWhoFileSystemObject(ClientEntity fileItem) {

        PropertyCollection properties = new PropertyCollection();
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
        Object object = new Object();
        object.setDeveloperName("$File");
        object.setExternalId(fileItem.getProperty("id").getValue().toString());
        object.setProperties(properties);

        return object;
    }


    public Object buildManyWhoSiteObject(ClientEntity siteItem, String parentId) {
        PropertyCollection properties = new PropertyCollection();

        properties.add(new Property("ID", siteItem.getProperty("id").getValue().toString()));
        properties.add(new Property("Created Date Time", siteItem.getProperty("createdDateTime").getValue().toString()));
        properties.add(new Property("Last Modified Date Time", siteItem.getProperty("lastModifiedDateTime").getValue().toString()));
        properties.add(new Property("Description", siteItem.getProperty("description").getValue().toString()));
        properties.add(new Property("Name", siteItem.getProperty("name").getValue().toString()));
        properties.add(new Property("Web URL", siteItem.getProperty("webUrl").getValue().toString()));
        properties.add(new Property("Site ID", siteItem.getProperty("id").getValue().toString()));
        properties.add(new Property("Parent ID", parentId));

        Object object = new Object();
        object.setDeveloperName(Site.NAME);
        object.setExternalId(siteItem.getProperty("id").getValue().toString());
        object.setProperties(properties);

        return object;
    }

    public MObject buildManyWhoSharePointListObject(ClientEntity sharepointListEntity, String siteId, boolean fullType) {
        PropertyCollection properties = new PropertyCollection();

        properties.add(new Property("ID", sharepointListEntity.getProperty("id").getValue().toString()));
        properties.add(new Property("Created Date Time", sharepointListEntity.getProperty("createdDateTime").getValue().toString()));
        properties.add(new Property("Last Modified Date Time", sharepointListEntity.getProperty("lastModifiedDateTime").getValue().toString()));
        properties.add(new Property("Description", sharepointListEntity.getProperty("description").getValue().toString()));
        properties.add(new Property("Name", sharepointListEntity.getProperty("name").getValue().toString()));
        properties.add(new Property("Web URL", sharepointListEntity.getProperty("webUrl").getValue().toString()));
        properties.add(new Property("Site ID", siteId));

        Object object = new Object();
        object.setDeveloperName(SharePointList.NAME);

        if (fullType) {
            object.setDeveloperName(sharepointListEntity.getProperty("name").getValue().toString());
            PropertyCollection customProperties = new PropertyCollection();

            sharepointListEntity.getProperties().stream().filter(
                    p -> !p.getName().equals("id") && !p.getName().equals("createdDateTime") &&
                            !p.getName().equals("lastModifiedDateTime") && !p.getName().equals("description") &&
                            !p.getName().equals("name") && !p.getName().equals("webUrl"))

                    .forEach( p -> customProperties.add(new Property(p.getName(), p.getValue().toString())));

        }

        object.setExternalId(String.format("%s#%s", sharepointListEntity.getProperty("id").getValue().toString(), siteId));
        object.setProperties(properties);

        return object;
    }

    public MObject buildManyWhoSharePointListObject(SPList siteEntity, String siteId) {
        PropertyCollection properties = new PropertyCollection();

        properties.add(new Property("ID", siteEntity.getId()));
        properties.add(new Property("Created Date Time", siteEntity.getData("Created").toString()));
        properties.add(new Property("Last Modified Date Time", siteEntity.getData("LastItemUserModifiedDate").toString()));
        properties.add(new Property("Description", siteEntity.getData("Description").toString()));
        properties.add(new Property("Name", siteEntity.getData("Title").toString()));
        java.lang.Object metadata =siteEntity.getData("__metadata");
        String url = null;

        try {
            url = ((JSONObject) metadata).get("id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        properties.add(new Property("Web URL", url));
        properties.add(new Property("Site ID", siteId));

        Object object = new Object();
        object.setDeveloperName(SharePointList.NAME);
        object.setExternalId(String.format("%s#%s", siteEntity.getId(), siteId));
        object.setProperties(properties);

        return object;
    }


    public MObject buildManyWhoItemObject(ClientEntity siteEntity, String siteId, String listId) {
        PropertyCollection properties = new PropertyCollection();

        properties.add(new Property("ID", siteEntity.getProperty("id").getValue().toString()));
        properties.add(new Property("Created Date Time", siteEntity.getProperty("createdDateTime").getValue().toString()));
        properties.add(new Property("Last Modified Date Time", siteEntity.getProperty("lastModifiedDateTime").getValue().toString()));
        properties.add(new Property("e Tag", siteEntity.getProperty("eTag").getValue().toString()));
        properties.add(new Property("Web URL", siteEntity.getProperty("webUrl").getValue().toString()));
        properties.add(new Property("List Item ID", siteEntity.getProperty("listItemId").getValue().toString()));
        properties.add(new Property("Site ID", siteId));
        properties.add(new Property("List ID", listId));

        Object object = new Object();
        object.setDeveloperName(Item.NAME);
        object.setExternalId(String.format("%s#%s", listId, siteEntity.getProperty("id").getValue().toString()));
        object.setProperties(properties);

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
