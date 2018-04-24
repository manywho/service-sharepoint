package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.microsoft.services.sharepoint.SPListItem;

public class IdExtractorForDynamicTypes {

    //sites/id1/list/id2/items/id3
    static public String extractItemId(String idUnique, String listId) {

        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException(String.format("The item ID Is empty, the ID should look like %s/items/{itemId}",
                    listId));
        }

        String[] parts = idUnique.split("/items/");

        if (parts.length < 2) {
            throw new RuntimeException(String.format("The item ID is not valid, it should look like %s/items/{itemId}",
                    listId));
        }
        return parts[1];
    }

    static public String extractItemId(SPListItem spListItem) {
        return String.valueOf(spListItem.getId());
    }

    static public String extractExternalItemId(ResourceMetadata resourceMetadata, String itemId) {
        return String.format("sites/%s/list/%s/items/%s",
                resourceMetadata.getSiteId(),
                resourceMetadata.getListId(),
                itemId);
    }

}
