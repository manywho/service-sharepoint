package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.run.elements.type.ObjectDataTypePropertyCollection;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.sharepoint.entities.ServiceConfiguration;
import org.glassfish.jersey.media.multipart.BodyPart;
import java.util.concurrent.ExecutionException;

public interface SharePointFacadeInterface {
    ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token) throws ExecutionException, InterruptedException;

    ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token, String parentId);

    ObjectDataResponse fetchSite(ServiceConfiguration configuration, String token, String id);

    ObjectDataResponse fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType);

    TypeElementCollection fetchTypesListsForAllSites(ServiceConfiguration configuration, String token);

    ObjectDataResponse fetchList(ServiceConfiguration configuration, String token, String idSite, String idList);

    ObjectDataResponse fetchListsRoot(ServiceConfiguration configuration, String token);

    ObjectDataResponse fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId);

    ObjectDataResponse fetchItems(ServiceConfiguration configuration, String token, String siteId, String listId);

    ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart);

    ObjectDataResponse fetchItemsDynamicType(ServiceConfiguration configuration, String token, String developerName, ObjectDataTypePropertyCollection properties);

    ObjectDataResponse fetchItemDynamicType(ServiceConfiguration configuration, String token, String developerName, String itemId, ObjectDataTypePropertyCollection properties);

    ObjectDataResponse saveDynamicType(ServiceConfiguration configuration, String token, String developerName, PropertyCollection properties);
}