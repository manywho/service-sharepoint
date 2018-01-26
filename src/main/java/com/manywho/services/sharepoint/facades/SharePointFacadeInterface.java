package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.types.*;

import java.util.List;

public interface SharePointFacadeInterface {
    List<Group> fetchGroups(ServiceConfiguration configuration, String token, ListFilter listFilter);

    List<User> fetchUsers(ServiceConfiguration configuration, String token, ListFilter listFilter);

    List<Site> fetchSites(ServiceConfiguration configuration, String token);

    List<Site> fetchSites(ServiceConfiguration configuration, String token, String parentId);

    Site fetchSite(ServiceConfiguration configuration, String token, String id);

    List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType);

    List<TypeElement> fetchAllListTypes(ServiceConfiguration configuration, String token);

    SharePointList fetchList(ServiceConfiguration configuration, String token, String idSite, String idList);

    List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token);

    Item fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId);

    List<Item> fetchItems(ServiceConfiguration configuration, String token, String listUniqueId);

    List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName, List<ObjectDataTypeProperty>  properties);

    MObject fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName, String itemId, List<ObjectDataTypeProperty> properties);

    MObject updateTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties, String id);

    MObject createTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties);

    String getUserId(ServiceConfiguration configuration, String token);
}
