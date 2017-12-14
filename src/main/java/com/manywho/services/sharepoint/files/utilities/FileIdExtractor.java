package com.manywho.services.sharepoint.files.utilities;

import com.google.common.base.Strings;

public class FileIdExtractor {

    static public String extractDriveItemIdFromUniqueId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the drive item ID Is empty");
        }

        String[] parts = idUnique.split("#");

        if (parts.length < 2) {
            throw new RuntimeException("Not valid list drive item ID");
        }

        return parts[1];
    }

    static public String extractDriveIdFromFileId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = idUnique.split("#");
        return parts[0];
    }


    static public String extractDriveItemIdFromFileId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the list ID Is empty");
        }

        String[] parts = idUnique.split("#");

        if (parts.length < 2) {
            throw  new RuntimeException("Not valid list ID");
        }

        return parts[1];
    }
}