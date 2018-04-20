package com.manywho.services.sharepoint.types;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.sharepoint.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class MyListClient extends ListClient {
    MyListClient(String serverUrl, String siteRelativeUrl, Credentials credentials) {
        super(serverUrl, siteRelativeUrl, credentials);
    }

    public ListenableFuture<List<SPListItem>> getListItems(String nextUrl) {
        final SettableFuture<List<SPListItem>> result = SettableFuture.create();
        ListenableFuture<JSONObject> request = this.executeRequestJson(nextUrl, "GET");
        Futures.addCallback(request, new FutureCallback<JSONObject>() {
            public void onFailure(Throwable t) {
                result.setException(t);
            }

            public void onSuccess(JSONObject json) {
                try {
                    result.set(SPListItem.listFromJson(json));
                } catch (JSONException var3) {
                    MyListClient.this.log(var3);
                }
            }
        });
        return result;
    }

    public ListenableFuture<JSONObject> getListItemsJson(String listName, Query query) {
        final SettableFuture<JSONObject> result = SettableFuture.create();
        String listNamePart = String.format("_api/web/lists/GetByTitle('%s')/Items?", this.urlEncode(listName));
        String getListUrl = this.getSiteUrl() + listNamePart + this.generateODataQueryString(query);
        ListenableFuture<JSONObject> request = this.executeRequestJson(getListUrl, "GET");
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
