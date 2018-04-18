package com.manywho.services.sharepoint.users;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.services.sharepoint.auth.authentication.UserClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.microsoft.services.sharepoint.Credentials;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class UserServiceClient {

    public static String getUserId(ServiceConfiguration configuration, String token) {
        Credentials credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        UserClient client = new UserClient(configuration.getHost(), "", credentials);

        ListenableFuture<JSONObject> properties = client.getUserProperties();
        try {
            return properties.get().getJSONObject("d").getJSONObject("UserId").get("NameId").toString();
        } catch (InterruptedException | JSONException | ExecutionException e) {
            throw new RuntimeException("Error fetching user Id", e);
        }
    }
}
