package com.manywho.services.sharepoint.client;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.services.sharepoint.types.MyListClient;
import com.microsoft.services.sharepoint.Query;
import com.microsoft.services.sharepoint.QueryOrder;
import com.microsoft.services.sharepoint.SPListItem;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServicePaginator {
    public List<SPListItem> getEntities(String listName, Query query, ListFilter listFilter, MyListClient myListClient) {
        int limit = 11;

        if (listFilter.getOrderByDirectionType() == ListFilter.OrderByDirectionType.Ascending && listFilter.hasOrderByPropertyDeveloperName()) {
            query.orderBy(listFilter.getOrderByPropertyDeveloperName(), QueryOrder.Ascending);
        } else if (listFilter.hasOrderByPropertyDeveloperName()){
            query.orderBy(listFilter.getOrderByPropertyDeveloperName(), QueryOrder.Descending );
        } else {
            query.orderBy("Id", QueryOrder.Ascending);
        }

        if (listFilter.hasOffset()) {
            query.top(listFilter.getOffset());

        } else if (listFilter.hasLimit()){
            query.top(listFilter.getLimit());
            // there is not offset, so return the first page with "limit" elements
            try {
                return myListClient.getListItems(listName, query).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(String.format("Error fetching items from list %s", listName), e);
            }
        }

        if (listFilter.hasLimit()) {
            limit = listFilter.getLimit();
        }

        JSONObject pageObject = getPageAsJson(listName, query, myListClient);
        List<SPListItem> entities = new ArrayList<>();

        if (getNextUrl(pageObject) == null) {
            //return empty list
            return entities;
        }

        int count_entities = 0;

        List<SPListItem> items1 = getItemsNextPage(pageObject, myListClient);

        if (items1.isEmpty()) {
            return entities;
        }

        for (SPListItem item:items1) {
            if (count_entities < limit) {
                count_entities++;
                entities.add(item);
            }
        }

        return entities;
    }

    private List<SPListItem> getItemsNextPage(JSONObject jsonObject, MyListClient myListClient) {
        try {
            if (getNextUrl(jsonObject) == null) {
                return new ArrayList<>();
            }
            return myListClient.getListItems(getNextUrl(jsonObject)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(String.format("Error fetching page %s", getNextUrl(jsonObject)), e);
        }
    }

    private JSONObject getPageAsJson(String listName, Query query, MyListClient myListClient) {
        try {
            return myListClient.getListItemsJson(listName, query).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(String.format("Error fetching items from list %s", listName), e);
        }
    }

    private String getNextUrl(JSONObject jsonObject) {
        try {
            if (jsonObject.getJSONObject("d").has("__next")) {
                return jsonObject.getJSONObject("d").get("__next").toString();
            } else {
                return null;
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error accessing to list item next page url", e);
        }
    }
}
