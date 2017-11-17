package com.manywho.services.sharepoint.facades;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.services.sharepoint.entities.ServiceConfiguration;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.microsoft.services.sharepoint.*;
import org.glassfish.jersey.media.multipart.BodyPart;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SharePointServiceFacade implements SharePointFacadeInterface {
    private ObjectMapperService objectMapperService;

    @Inject
    public SharePointServiceFacade(ObjectMapperService objectMapperService) {
        this.objectMapperService = objectMapperService;
    }

    @Override
    public ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token) throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token, String parentId) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchSite(ServiceConfiguration configuration, String token, String id) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType) {
        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            ObjectCollection objectCollection = new ObjectCollection();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(spList, idSite));
            }

            return new ObjectDataResponse(objectCollection);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeElementCollection fetchTypesListsForAllSites(ServiceConfiguration configuration, String token) {
        throw new RuntimeException("fetch types for all sites is not implemented for apps");
    }

    @Override
    public ObjectDataResponse fetchList(ServiceConfiguration configuration, String token, String idSite, String idList) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchListsRoot(ServiceConfiguration configuration, String token) {
        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            ObjectCollection objectCollection = new ObjectCollection();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(spList, ""));
            }

            return new ObjectDataResponse(objectCollection);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObjectDataResponse fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchItems(ServiceConfiguration configuration, String token, String siteId, String listId) {
        return null;
    }

    @Override
    public ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
        return null;
    }
}
