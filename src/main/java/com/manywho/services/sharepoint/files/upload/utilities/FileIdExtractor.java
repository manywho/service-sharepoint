package com.manywho.services.sharepoint.files.upload.utilities;

import com.google.common.base.Strings;

public class FileIdExtractor {

    /**
     * unique id looks like drives/{drive-id}/items/{item-id}
     * @param idUnique
     * @return
     */
    static public String extractDriveItemIdFromUniqueId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("the drive item ID Is empty");
        }

        String[] parts = idUnique.split("/items/");

        if (parts.length < 2) {
            throw new RuntimeException("Not valid list drive item ID");
        }

        return parts[1];
    }

    static public String extractDriveIdFromUniqueId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = idUnique.split("/items/");
        return parts[0].replace("drives/", "");
    }
}