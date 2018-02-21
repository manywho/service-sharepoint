package com.manywho.services.sharepoint.users;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.microsoft.services.sharepoint.Credentials;
import com.microsoft.services.sharepoint.ListClient;

import java.util.concurrent.ExecutionException;

public class UserServiceClient {

    public static String getUserId(ServiceConfiguration configuration, String token) {
        Credentials credentials = request -> request.addHeader("Authorization", "Bearer " + token);
        ListClient client = new ListClient(configuration.getHost(), "" , credentials);
        ListenableFuture<String> properties = client.getUserProperties();
        try {
            return properties.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching user Id", e);
        }
    }
}
