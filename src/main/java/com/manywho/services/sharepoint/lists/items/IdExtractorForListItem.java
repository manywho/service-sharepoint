package com.manywho.services.sharepoint.lists.items;

import com.google.common.base.Strings;

public class IdExtractorForListItem {


    static public String extractSiteId(String listItemIdUnique) {

        if (Strings.isNullOrEmpty(listItemIdUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = listItemIdUnique.split("/lists/");
        return parts[0].replace("sites/", "");
    }

    static public String extractListId(String listItemIdUnique) {
        if (Strings.isNullOrEmpty(listItemIdUnique)) {
            throw new RuntimeException("the list ID Is empty");
        }

        String[] parts = listItemIdUnique.split("/lists/");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        String[] parts2 = parts[1].split("/items/");

        if (parts2.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        return parts2[0];
    }


    static public String extractListItemId(String listItemIdUnique) {
        if (Strings.isNullOrEmpty(listItemIdUnique)) {
            throw new RuntimeException("the SharePoint List Item ID Is empty");
        }

        String[] parts = listItemIdUnique.split("/items/");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid SharePoint List Item ID");
        }

        return parts[1];
    }
}
