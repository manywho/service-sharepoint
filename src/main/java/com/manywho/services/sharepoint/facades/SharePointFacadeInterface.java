package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.groups.Group;
import com.manywho.services.sharepoint.lists.items.SharePointListItem;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.manywho.services.sharepoint.sites.Site;
import com.manywho.services.sharepoint.users.types.User;

import java.util.List;

public interface SharePointFacadeInterface {
    List<Group> fetchGroups(ServiceConfiguration configuration, String token, ListFilter listFilter);

    List<User> fetchUsers(ServiceConfiguration configuration, String token, ListFilter listFilter);

    List<Site> fetchSites(ServiceConfiguration configuration, String token, String groupId);

    List<Site> fetchSubsites(ServiceConfiguration configuration, String token, String parentId);

    Site fetchSite(ServiceConfiguration configuration, String token, String id);

    List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType);

    List<TypeElement> fetchAllListTypes(ServiceConfiguration configuration, String token);

    SharePointList fetchList(ServiceConfiguration configuration, String token, String idSite, String idList);

    List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token);

    SharePointListItem fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId);

    List<SharePointListItem> fetchItems(ServiceConfiguration configuration, String token, String listUniqueId);

    List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName, List<ObjectDataTypeProperty> properties, ListFilter listFilter);

    MObject fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName, String itemId, List<ObjectDataTypeProperty> properties);

    MObject updateTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties, String id);

    void deleteTypeList(ServiceConfiguration configuration, String token, String developerName, String id);

    MObject createTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties);

    String getUserId(ServiceConfiguration configuration, String token);
}
