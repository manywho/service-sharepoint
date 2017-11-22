package com.manywho.services.sharepoint.services;

import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.enums.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class DynamicTypesService {
    public static void patchDynamicType(String token, String uri, PropertyCollection properties) {
        CloseableHttpClient httpclient =  HttpClients.createDefault();
        try {
            HttpPatch httpPatch = new HttpPatch(uri);
            httpPatch.setEntity(new StringEntity(getJsonObject(properties), Consts.UTF_8));
            httpPatch.addHeader("Authorization", String.format("Bearer %s", token));
            httpPatch.addHeader("Content-Type", "application/json");
            HttpResponse response =  httpclient.execute(httpPatch);

            if (response.getStatusLine().getStatusCode() >=200  && response.getStatusLine().getStatusCode() <300) {
                return;
            }

            throw new RuntimeException("Error updating object");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String insertDynamicType(String token, String uri) {
        CloseableHttpClient httpclient =  HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setEntity(new StringEntity("{}", Consts.UTF_8));

            httpPost.addHeader("Authorization", String.format("Bearer %s", token));
            httpPost.addHeader("Content-Type", "application/json");

            HttpResponse response =  httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() >=200  && response.getStatusLine().getStatusCode() <300) {
                String content = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                JSONObject object = new JSONObject(content);

                return object.getString("id");
            }

            throw new RuntimeException("Error inserting object");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getJsonObject(PropertyCollection properties) {
        StringBuilder response = new StringBuilder();
        boolean first = true;
        response.append("{");

        for (Property p: properties) {
            if (Objects.equals(p.getDeveloperName(), "ID") ) {
                break;
            }

            if (p.getContentType() == ContentType.DateTime) {
                //ToDo find the problem with this field, it doesn't seems to work with postman neither
                break;
            }

            if (!first) {
                response.append(",");
            }

            String name = Objects.equals(p.getDeveloperName(), "ID") ? "id": p.getDeveloperName();

            first = false;

            switch (p.getContentType()) {
                case Number:
                    response.append(String.format("\"%s\": %s",name, p.getContentValue()));
                    break;
                case Boolean:
                    response.append(String.format("\"%s\": %s",name, p.getContentValue()));
                    break;
                default:
                    response.append(String.format("\"%s\": \"%s\"",name, p.getContentValue()));
                    break;

            }

        }
        response.append("}");
        return response.toString();

    }
}
