package com.manywho.services.sharepoint.lists;

import com.google.common.base.Strings;

public class IdExtractorForLists {
    static public String extractSiteId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = idUnique.split("/lists/");
        return parts[0].replace("sites/", "");
    }


    static public String extractListId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the list ID Is empty");
        }

        String[] parts = idUnique.split("/lists/");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        return parts[1];
    }
}
