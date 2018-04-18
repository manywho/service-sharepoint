package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import org.apache.olingo.client.api.domain.ClientEntity;

/**
 * Sharepoint Ids are dependent of the api used, if we use rest it will use the RestID values, if we use the new
 * graph api the it will look like sites/../lists/..  I have created this class to handle this complexity
 *
 */
public class ResourceMetadata {

    private static String LIST_ID_SPLIT="~#RestID#~";
    private String metadata;

    //variable to identify the graph resource
    private String resource;

    private String siteId;
    private String listId;
    private String listName;
    private String siteName;

    ResourceMetadata(ClientEntity list, String siteId, String listName, String siteName) {
        this.siteId = siteId;
        listId = list.getProperty("id").getValue().asPrimitive().toString();
        this.listName = listName;
        this.siteName = siteName;

        resource = String.format("sites/%s/lists/%s", siteId, listId);

        metadata = String.format("%s%s%s%s%s",
                resource,
                LIST_ID_SPLIT,
                listName,
                LIST_ID_SPLIT,
                siteName
        );
    }

    ResourceMetadata(String metadataName) {
        this.metadata = metadataName;

        if (Strings.isNullOrEmpty(metadataName)) {
            throw new RuntimeException("Id not valid");
        }

        String parts[] = metadataName.split(LIST_ID_SPLIT);

        if (parts.length < 3) {
            throw new RuntimeException("Id not valid");
        }

        this.resource = parts[0];

        this.listName = parts[1];
        this.siteName = parts[2];

        String partsId[] = parts[0].split("/lists/");

        if (partsId.length < 2) {
            throw new RuntimeException("Id not valid");
        }

        this.listId = partsId[1];
        this.siteId = partsId[0].replace("sites/", "");
    }

    public String getMetadata() {
        return metadata;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getListId() {
        return listId;
    }

    public String getListName() {
        return listName;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getResource() {
        return resource;
    }
}
