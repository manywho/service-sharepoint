package com.manywho.services.sharepoint.lists;

import com.google.common.base.Strings;

public class IdExtractorForLists {

    static public String extractSiteId(String listIdUnique) {

        if (Strings.isNullOrEmpty(listIdUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = listIdUnique.split("/lists/");
        return parts[0].replace("sites/", "");
    }

    static public String extractListId(String listIdUnique) {
        if (Strings.isNullOrEmpty(listIdUnique)) {
            throw new RuntimeException("the list ID Is empty");
        }

        String[] parts = listIdUnique.split("/lists/");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        return parts[1];
    }
}
