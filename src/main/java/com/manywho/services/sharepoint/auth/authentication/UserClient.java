package com.manywho.services.sharepoint.auth.authentication;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.sharepoint.Credentials;
import com.microsoft.services.sharepoint.SharePointClient;
import org.json.JSONObject;

public class UserClient extends SharePointClient {
    public UserClient(String serverUrl, String siteRelativeUrl, Credentials credentials) {
        super(serverUrl, siteRelativeUrl, credentials);
    }

    public ListenableFuture<JSONObject> getUserProperties() {
        final SettableFuture<JSONObject> result = SettableFuture.create();
        String url = this.getSiteUrl() + "/_api/web/currentUser";
        ListenableFuture<JSONObject> request = this.executeRequestJson(url, "GET");
        Futures.addCallback(request, new FutureCallback<JSONObject>() {
            public void onFailure(Throwable t) {
                result.setException(t);
            }

            public void onSuccess(JSONObject json) {
                result.set(json);
            }
        });

        return result;
    }
}
