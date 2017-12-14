package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SharePointFacadeInterface {
    List<Site> fetchSites(ServiceConfiguration configuration, String token) throws ExecutionException, InterruptedException;

    List<Site> fetchSites(ServiceConfiguration configuration, String token, String parentId);

    Site fetchSite(ServiceConfiguration configuration, String token, String id);

    List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType);

    List<TypeElement> fetchAllListTypes(ServiceConfiguration configuration, String token);

    SharePointList fetchList(ServiceConfiguration configuration, String token, String idSite, String idList);

    List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token);

    Item fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId);

    List<Item> fetchItems(ServiceConfiguration configuration, String token, String listUniqueId);

//    MObject uploadFileToSharePoint(String token, String path, BodyPart bodyPart);

    List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName, List<ObjectDataTypeProperty>  properties);

    MObject fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName, String itemId, List<ObjectDataTypeProperty> properties);

    MObject updateTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties, String id);

    MObject createTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties);
}
