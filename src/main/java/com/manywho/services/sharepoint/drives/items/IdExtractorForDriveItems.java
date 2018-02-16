package com.manywho.services.sharepoint.drives.items;

import com.google.common.base.Strings;

public class IdExtractorForDriveItems {

    static public String extractDriveId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The Drive ID Is empty");
        }

        String[] parts = idUnique.split("/items/");

        return parts[0].replace("drives/", "");
    }

    static public String extractDriveItemId(String idUnique) {

        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The Drive Item ID Is empty");
        }

        String[] parts = idUnique.split("/items/");

        return parts[1];
    }
}
