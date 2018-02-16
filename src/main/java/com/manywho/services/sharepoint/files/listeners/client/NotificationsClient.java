package com.manywho.services.sharepoint.files.listeners.client;

import com.google.inject.Inject;
import com.manywho.services.sharepoint.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_V1;

public class NotificationsClient {
    private HttpClient httpClient;

    @Inject
    public NotificationsClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    public void removeWebhook(String webhookId, String token) {
        HttpDelete delete = new HttpDelete(String.format("%s/subscriptions/%s", GRAPH_ENDPOINT_V1, webhookId));
        delete.addHeader("Authorization", String.format("Bearer %s", token));
        httpClient.executeRequest(delete);
    }

}
