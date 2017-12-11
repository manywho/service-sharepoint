package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SharePointFacadeInterface {
    List<Site> fetchSites(ApplicationConfiguration configuration, String token) throws ExecutionException, InterruptedException;

    List<Site> fetchSites(ApplicationConfiguration configuration, String token, String parentId);

    Site fetchSite(ApplicationConfiguration configuration, String token, String id);

    List<SharePointList> fetchLists(ApplicationConfiguration configuration, String token, String idSite, boolean fullType);

    List<TypeElement> fetchAllListTypes(ApplicationConfiguration configuration, String token);

    SharePointList fetchList(ApplicationConfiguration configuration, String token, String idSite, String idList);

    List<SharePointList> fetchListsRoot(ApplicationConfiguration configuration, String token);

    MObject fetchItem(ApplicationConfiguration configuration, String token, String siteId, String listId, String itemId);

    List<MObject> fetchItems(ApplicationConfiguration configuration, String token, String siteId, String listId);

//    MObject uploadFileToSharePoint(String token, String path, BodyPart bodyPart);

    List<MObject> fetchTypesFromLists(ApplicationConfiguration configuration, String token, String developerName, List<ObjectDataTypeProperty>  properties);

    MObject fetchTypeFromList(ApplicationConfiguration configuration, String token, String developerName, String itemId, List<ObjectDataTypeProperty> properties);

    MObject updateTypeList(ApplicationConfiguration configuration, String token, String developerName, List<Property> properties, String id);

    MObject createTypeList(ApplicationConfiguration configuration, String token, String developerName, List<Property> properties);
}
