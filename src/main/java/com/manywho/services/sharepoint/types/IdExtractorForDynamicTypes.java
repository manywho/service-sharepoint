package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;

public class IdExtractorForDynamicTypes {
    static public String extractItemId(String idUnique) {
        if (Strings.isNullOrEmpty(idUnique)) {
            throw new RuntimeException("The site ID Is empty");
        }

        String[] parts = idUnique.split("/items/");
        return parts[1].replace("sites/", "");
    }
}
