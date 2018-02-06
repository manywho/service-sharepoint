package com.manywho.services.sharepoint.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClient {
    private CloseableHttpClient httpclient;

    public HttpClient() {
        this.httpclient = HttpClients.createDefault();
    }

    public String executeRequest(HttpRequestBase requestBase){

        try {
            return httpclient.execute(requestBase, (HttpResponse httpResponse) -> {
                int status = httpResponse.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = httpResponse.getEntity();

                    return EntityUtils.toString(entity);
                }

                throw new RuntimeException(httpResponse.getStatusLine().toString());
            });
        } catch (IOException e) {
            throw new RuntimeException("Error in the deserialization from SharePoint response", e);
        }
    }

    public void addAuthorizationHeader(HttpRequestBase requestBase, String token) {
        requestBase.addHeader("Authorization", "Bearer "+ token);
    }
}
