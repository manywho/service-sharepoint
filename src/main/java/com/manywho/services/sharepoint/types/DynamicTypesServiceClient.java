package com.manywho.services.sharepoint.types;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.run.elements.type.*;
import com.manywho.services.sharepoint.client.HttpClient;
import com.manywho.services.sharepoint.client.ServicePaginator;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.microsoft.services.sharepoint.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DynamicTypesServiceClient {

    private DynamicTypesMapper dynamicTypesMapper;
    private HttpClient httpClient;
    private ServicePaginator servicePaginator;
    private CloseableHttpClient closeableHttpClient;

    @Inject
    public DynamicTypesServiceClient(DynamicTypesMapper dynamicTypesMapper, HttpClient httpClient,
                                     ServicePaginator servicePaginator, CloseableHttpClient closeableHttpClient) {
        this.dynamicTypesMapper = dynamicTypesMapper;
        this.httpClient = httpClient;
        this.servicePaginator = servicePaginator;
        this.closeableHttpClient = closeableHttpClient;
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

    public List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, ResourceMetadata resourceMetadata,
                                             List<ObjectDataTypeProperty> properties, ListFilter listFilter) {

        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        MyListClient client = new MyListClient(configuration.getHost(), "sites/" + resourceMetadata.getSiteName() , credentials);

        Query query = new Query();
        addWhereToQuery(listFilter, query);

        List<SPListItem> items = servicePaginator.getEntities(resourceMetadata.getListName(), query, listFilter, client);
        List<MObject> objectCollection = new ArrayList<>();

        DynamicTypesMapper dynamicTypesMapper = new DynamicTypesMapper();
        for (SPListItem spListItem : items) {
            objectCollection.add(dynamicTypesMapper.buildManyWhoDynamicObject(resourceMetadata, spListItem, properties));
        }

        return objectCollection;
    }

    private void addWhereToQuery(ListFilter filter, Query query) {
        boolean first = true;

        for (ListFilterWhere where:filter.getWhere()) {
            if (first == false) {
                if (filter.getComparisonType() == ComparisonType.And) {
                    query.and();
                } else {
                    query.or();
                }
            }

            first = false;

            query.field(where.getColumnName()).eq(where.getContentValue());
        }
    }

    public MObject updateTypeList(ServiceConfiguration configuration, String token, MObject object) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());
        String itemId = IdExtractorForDynamicTypes.extractItemId(object.getExternalId(), object.getDeveloperName());

        String uri = String.format("%s/sites/%s/_api/web/lists/GetByTitle('%s')/items(%s)", configuration.getHost(),
                resourceMetadata.getSiteName(), resourceMetadata.getListName(), itemId);

        // the service library have a bug, that is the reason that I am using a raw http client
        HttpPost httpPost = new HttpPost(uri);

        try {
            httpPost.setEntity(new StringEntity(getPayload(object.getProperties())));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding");
        }

        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("If-Match", "*");
        httpPost.addHeader("X-HTTP-Method", "MERGE");
        httpPost.setHeader("Authorization", String.format("Bearer %s", token));
        try {
            closeableHttpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error executing save for item %s", object.getDeveloperName()), e);
        }

        return fetchTypeFromList(configuration, token, resourceMetadata, PropertiesUtils.castToObjectProperty(object.getProperties()), itemId);
    }

    public MObject createTypeList(ServiceConfiguration configuration, String token, MObject object) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());
        String uri = String.format("%s/sites/%s/_api/web/lists/GetByTitle('%s')/items",
                configuration.getHost(), resourceMetadata.getSiteName(), resourceMetadata.getListName());

        // the service library have a bug, that is the reason that I am using a raw http client
        HttpPost httpPost = new HttpPost(uri);

        try {
            httpPost.setEntity(new StringEntity(getPayload(object.getProperties())));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("If-Match", "*");
            httpPost.setHeader("Authorization", String.format("Bearer %s", token));
            httpPost.addHeader("Accept", "application/json;odata=verbose");
            String response = httpClient.executeRequest(httpPost);
            JSONObject jsonObject = new JSONObject(response);
            String itemId = jsonObject.getJSONObject("d").get("ID").toString();

            return fetchTypeFromList(configuration, token, resourceMetadata, PropertiesUtils.castToObjectProperty(object.getProperties()), itemId);

        } catch (UnsupportedEncodingException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPayload(List<Property> properties) {
        JsonObject jsonObject = new JsonObject();
        for (Property property: properties) {
            if ("ID".equals(property.getDeveloperName()) == false) {
                jsonObject.addProperty(property.getDeveloperName(), property.getContentValue());
            }
        }

        return jsonObject.toString();
    }


    public MObject fetchTypeFromList(ServiceConfiguration configuration, String token, ResourceMetadata resourceMetadata,
                                             List<ObjectDataTypeProperty> properties, String itemId) {

        Credentials  credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "sites/" + resourceMetadata.getSiteName() , credentials);

        try {
            Query query = new Query();
            query.field("ID").eq(itemId);

            ListenableFuture<List<SPListItem>> listItems = client.getListItems(resourceMetadata.getListName(), query);
            List<SPListItem> items = listItems.get();

            if (items.isEmpty()) {
                throw new RuntimeException("Item not found");
            }

            return dynamicTypesMapper.buildManyWhoDynamicObject(resourceMetadata, items.get(0), properties);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTypeList(ServiceConfiguration configuration, String token, ResourceMetadata resourceMetadata, String id) {
        String uri = String.format("%s/sites/%s/_api/web/lists/GetByTitle('%s')/items(%s)",
                configuration.getHost(), resourceMetadata.getSiteName(), resourceMetadata.getListName(), id);

        HttpDelete httpPost = new HttpDelete(uri);
        httpPost.addHeader("If-Match", "*");
        httpPost.addHeader("X-HTTP-Method", "DELETE");
        httpPost.setHeader("Authorization", String.format("Bearer %s", token));

        try {
            closeableHttpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
