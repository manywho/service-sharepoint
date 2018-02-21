package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.manywho.services.sharepoint.lists.items.SharePointListItem;
import com.microsoft.services.sharepoint.SPList;
import com.microsoft.services.sharepoint.SPListItem;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DynamicTypesMapper {

    public MObject buildManyWhoDynamicObject(String uniqueId, List<ClientProperty> clientProperties, List<ObjectDataTypeProperty> properties) {
        MObject object = new MObject();
        object.setDeveloperName(SharePointListItem.NAME);
        List<Property> mobjectProperties = new ArrayList<>();

        for (ObjectDataTypeProperty property: properties) {
            if (Objects.equals(property.getDeveloperName(), "ID")) {
                object.setExternalId(uniqueId);
                mobjectProperties.add(new Property("ID", object.getExternalId()));
            } else {
                Optional<ClientProperty> foundProperty = clientProperties.stream()
                        .filter(p -> Objects.equals(p.getName(), property.getDeveloperName())).findFirst();

                if (foundProperty.isPresent()) {
                    mobjectProperties.add(new Property(property.getDeveloperName(), foundProperty.get().getValue().asPrimitive().toValue()));
                } else {
                    mobjectProperties.add(new Property(property.getDeveloperName(), ""));
                }
            }
        }

        object.setProperties(mobjectProperties);

        return object;
    }

    public MObject buildManyWhoDynamicObject(String developerName, SPListItem spListItem, List<ObjectDataTypeProperty> properties) {
        MObject object = new MObject();
        List<Property> mobjectProperties = new ArrayList<>();
        String externalId = "";

        for (ObjectDataTypeProperty property: properties) {
            externalId = spListItem.getGUID();

            if (Objects.equals(property.getDeveloperName(), "ID")) {
                mobjectProperties.add(new Property("ID", externalId));
            } else {
                mobjectProperties.add(new Property(property.getDeveloperName(), spListItem.getData(property.getDeveloperName())));
            }
        }

        object.setDeveloperName(developerName);
        object.setExternalId(externalId);
        object.setProperties(mobjectProperties);

        return object;
    }

    public SharePointList buildManyWhoSharePointListObject(SPList listEntity, String siteId) {
        SharePointList list = new SharePointList();
        list.setCreatedDateTime(OffsetDateTime.parse(listEntity.getData("Created").toString()));
        list.setModifiedDateTime(OffsetDateTime.parse(listEntity.getData("LastItemUserModifiedDate").toString()));
        list.setDescription(listEntity.getData("Description").toString());
        list.setName(listEntity.getData("Title").toString());

        java.lang.Object metadata = listEntity.getData("__metadata");

        list.setSiteId(siteId);
        try {
            list.setWebUrl(((JSONObject) metadata).get("id").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.setId(String.format("sites/%s/lists/%s", listEntity.getId(), siteId));

        return list;
    }
}
