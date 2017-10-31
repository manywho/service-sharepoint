package com.manywho.services.sharepoint.facades;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.services.sharepoint.entities.Configuration;
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
    public ObjectDataResponse fetchSites(Configuration configuration, String token) throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public ObjectDataResponse fetchSites(Configuration configuration, String token, String parentId) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchSite(Configuration configuration, String token, String id) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchLists(Configuration configuration, String token, String idSite) {
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
    public ObjectDataResponse fetchList(Configuration configuration, String token, String idSite, String idList) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchListsRoot(Configuration configuration, String token) {
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
    public ObjectDataResponse fetchItem(Configuration configuration, String token, String siteId, String listId, String itemId) {
        return null;
    }

    @Override
    public ObjectDataResponse fetchItems(Configuration configuration, String token, String siteId, String listId) {
        return null;
    }

    @Override
    public ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
        return null;
    }
}
