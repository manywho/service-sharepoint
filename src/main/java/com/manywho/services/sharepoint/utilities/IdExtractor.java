package com.manywho.services.sharepoint.utilities;

import com.google.common.base.Strings;

public class IdExtractor {
    static public String extractSiteId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = idUnique.split("#");
        return parts[0];
    }


    static public String extractListId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the list ID Is empty");
        }

        String[] parts = idUnique.split("#");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        return parts[1];
    }


    static public String extractItemId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the item ID Is empty");
        }

        String[] parts = idUnique.split("#");

        if (parts.length < 3) {
            throw  new RuntimeException("Not valid item ID");
        }

        return parts[2];
    }
}
