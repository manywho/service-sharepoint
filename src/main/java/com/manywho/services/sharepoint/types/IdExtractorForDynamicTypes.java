package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.microsoft.services.sharepoint.SPListItem;

public class IdExtractorForDynamicTypes {

    //sites/id1/list/id2/items/id3
    static public String extractItemId(String idUnique) {

        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The item ID Is empty");
        }

        String[] parts = idUnique.split("/items/");
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
