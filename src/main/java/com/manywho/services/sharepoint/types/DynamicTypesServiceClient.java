package com.manywho.services.sharepoint.types;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.microsoft.services.sharepoint.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DynamicTypesServiceClient {

    private DynamicTypesMapper dynamicTypesMapper;

    @Inject
    public DynamicTypesServiceClient(DynamicTypesMapper dynamicTypesMapper) {
        this.dynamicTypesMapper = dynamicTypesMapper;
    }

    public List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite) {
        Credentials credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            List<SharePointList> objectCollection = new ArrayList<>();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(dynamicTypesMapper.buildManyWhoSharePointListObject(spList, idSite));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fetch list error:", e);
        }
    }


    public List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token) {
        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<List<SPList>> listsFuture = client.getLists(new Query());

        try {
            List<SharePointList> objectCollection = new ArrayList<>();
            List<SPList> lists = listsFuture.get();

            for (SPList spList : lists) {
                objectCollection.add(dynamicTypesMapper.buildManyWhoSharePointListObject(spList, ""));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName,
                                             List<ObjectDataTypeProperty> properties, ListFilter listFilter) {

        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);

        try {
            ListenableFuture<List<SPListItem>> listItems = client.getListItems(developerName, new Query());
            List<SPListItem> items = listItems.get();

            List<MObject> objectCollection = new ArrayList<>();

            DynamicTypesMapper dynamicTypesMapper = new DynamicTypesMapper();
            for (SPListItem spListItem : items) {
                objectCollection.add(dynamicTypesMapper.buildManyWhoDynamicObject(developerName, spListItem, properties));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
