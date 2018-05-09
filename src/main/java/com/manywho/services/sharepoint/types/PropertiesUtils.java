package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import java.util.List;
import java.util.stream.Collectors;

public class PropertiesUtils {
    public static List<ObjectDataTypeProperty> mapToObjectProperty(List<Property> properties) {
        //todo use constructor with parameter after the next release of sdk-java
        return properties.stream().map(p -> {
            ObjectDataTypeProperty objectDataTypeProperty = new ObjectDataTypeProperty();
            objectDataTypeProperty.setDeveloperName(p.getDeveloperName());
            return objectDataTypeProperty;
        }).collect(Collectors.toList());

    }
}
