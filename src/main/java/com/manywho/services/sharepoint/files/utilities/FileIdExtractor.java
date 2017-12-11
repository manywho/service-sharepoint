package com.manywho.services.sharepoint.files.utilities;

import com.google.common.base.Strings;

public class FileIdExtractor {

    static public String extractDriveItemId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the drive item ID Is empty");
        }

        String[] parts = idUnique.split("#");

        if (parts.length < 2) {
            throw new RuntimeException("Not valid list drive item ID");
        }

        return parts[1];
    }
}