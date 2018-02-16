package com.manywho.services.sharepoint.drives.items;

import com.google.common.base.Strings;

public class IdExtractorForDriveItems {

    static public String extractDriveId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The Drive ID Is empty");
        }

        return idUnique.replace("drives/", "");
    }
}
