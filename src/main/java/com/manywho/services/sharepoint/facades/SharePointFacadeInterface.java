package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.services.sharepoint.entities.Configuration;
import org.glassfish.jersey.media.multipart.BodyPart;

import java.util.concurrent.ExecutionException;


public interface SharePointFacadeInterface {
    ObjectDataResponse fetchSites(Configuration configuration, String token) throws ExecutionException, InterruptedException;

    ObjectDataResponse fetchSites(Configuration configuration, String token, String parentId);

    ObjectDataResponse fetchSite(Configuration configuration, String token, String id);

    ObjectDataResponse fetchLists(Configuration configuration, String token, String idSite);

    ObjectDataResponse fetchList(Configuration configuration, String token, String idSite, String idList);

    ObjectDataResponse fetchListsRoot(Configuration configuration, String token);

    ObjectDataResponse fetchItem(Configuration configuration, String token, String siteId, String listId, String itemId);

    ObjectDataResponse fetchItems(Configuration configuration, String token, String siteId, String listId);

    ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart);
}
