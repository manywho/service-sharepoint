package com.manywho.services.sharepoint.facades;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;
import com.microsoft.services.sharepoint.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SharePointServiceFacade implements SharePointFacadeInterface {

    private ObjectMapperService objectMapperService;

    @Inject
    public SharePointServiceFacade(ObjectMapperService objectMapperService) {
        this.objectMapperService = objectMapperService;
    }

    @Override
    public List<Site> fetchSites(ServiceConfiguration configuration, String token) throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public List<Site> fetchSites(ServiceConfiguration configuration, String token, String parentId) {
        return null;
    }

    @Override
    public Site fetchSite(ServiceConfiguration configuration, String token, String id) {
        return null;
    }

    @Override
    public List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType) {
        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            List<SharePointList> objectCollection = new ArrayList<>();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(spList, idSite));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TypeElement> fetchAllListTypes(ServiceConfiguration configuration, String token) {
        throw new RuntimeException("fetch types for all sites is not implemented for apps");
    }

    @Override
    public SharePointList fetchList(ServiceConfiguration configuration, String token, String idSite, String idList) {
        return null;
    }

    @Override
    public List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token) {
        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            List<SharePointList> objectCollection = new ArrayList<>();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(spList, ""));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId) {
        return null;
    }

    @Override
    public List<Item> fetchItems(ServiceConfiguration configuration, String token, String listId) {
        return null;
    }

//    @Override
//    public MObject uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
//        return null;
//    }

    @Override
    public List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName,
                                             List<ObjectDataTypeProperty>  properties) {

        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);

        try {
            ListenableFuture<List<SPListItem>> listItems = client.getListItems(developerName, new Query());
            List<SPListItem> items = listItems.get();

            List<MObject> objectCollection = new ArrayList<>();

            for (SPListItem spListItem : items) {
                objectCollection.add(this.objectMapperService.buildManyWhoDynamicObject(developerName, spListItem, properties));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MObject fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName, String itemId, List<ObjectDataTypeProperty> properties) {
        return null;
     }

    @Override
    public MObject updateTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties, String id) {
        return null;
    }

    @Override
    public MObject createTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties) {
        return null;
    }
}
