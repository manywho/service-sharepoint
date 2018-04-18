package com.manywho.services.sharepoint.types;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.client.HttpClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.microsoft.services.sharepoint.*;
import org.apache.http.client.methods.CloseableHttpResponse;
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

    @Inject
    public DynamicTypesServiceClient(DynamicTypesMapper dynamicTypesMapper, HttpClient httpClient) {
        this.dynamicTypesMapper = dynamicTypesMapper;
        this.httpClient = httpClient;
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
        ListClient client = new ListClient(configuration.getHost(), "sites/" + resourceMetadata.getSiteName() , credentials);

        try {
            ListenableFuture<List<SPListItem>> listItems = client.getListItems(resourceMetadata.getListName(), new Query());
            List<SPListItem> items = listItems.get();

            List<MObject> objectCollection = new ArrayList<>();

            DynamicTypesMapper dynamicTypesMapper = new DynamicTypesMapper();
            for (SPListItem spListItem : items) {
                objectCollection.add(dynamicTypesMapper.buildManyWhoDynamicObject(resourceMetadata, spListItem, properties));
            }

            return objectCollection;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public MObject updateTypeList(ServiceConfiguration configuration, String token, MObject object) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());
        String itemId = IdExtractorForDynamicTypes.extractItemId(object.getExternalId());

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException("Error executing save");
        }

        return fetchTypeFromList(configuration, token, resourceMetadata, castToObjectProperty(object.getProperties()), itemId);
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

            return fetchTypeFromList(configuration, token, resourceMetadata, castToObjectProperty(object.getProperties()), itemId);

        } catch (UnsupportedEncodingException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPayload(List<Property> properties) {
        JsonObject jsonObject = new JsonObject();
        for (Property property: properties) {
            if ("ID".equals(property.getDeveloperName()) == false)
                jsonObject.addProperty(property.getDeveloperName(), property.getContentValue());
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

    private List<ObjectDataTypeProperty> castToObjectProperty(List<Property> properties) {
        List<ObjectDataTypeProperty> properties1 = new ArrayList<>();
        properties.forEach(p -> {
            ObjectDataTypeProperty objectDataTypeProperty1 = new ObjectDataTypeProperty();
            objectDataTypeProperty1.setDeveloperName(p.getDeveloperName());
            properties1.add(objectDataTypeProperty1);
        });

        return properties1;
    }
}
