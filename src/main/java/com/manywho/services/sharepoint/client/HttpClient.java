package com.manywho.services.sharepoint.client;

import com.google.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private CloseableHttpClient httpclient;
    @Inject

    public HttpClient(CloseableHttpClient closeableHttpClient) {
        this.httpclient = closeableHttpClient;
    }

    public String executeRequest(HttpRequestBase requestBase){

        try {
            return httpclient.execute(requestBase, (HttpResponse httpResponse) -> {
                int status = httpResponse.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = httpResponse.getEntity();
                    return EntityUtils.toString(entity);
                }

                String errorLine = httpResponse.getStatusLine().toString();

                // If we get an error about the admin consent, we send a better error description
                if (httpResponse.getEntity() != null) {
                    try {
                        String entityError = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject objectError = new JSONObject(entityError);
                        String errorDescription = objectError.getString("error_description");

                        // At the moment of adding these lines the errors for AAD are described at:
                        // https://docs.microsoft.com/en-us/azure/active-directory/develop/reference-aadsts-error-codes
                        // if more errors are needed they should be described in that documentation

                        if (errorDescription.contains("AADSTS65001")) {
                            errorLine = "The user or administrator has not consented to use this application." +
                                    " Send an interactive authorization request for this user and resource. ";
                        } else if (errorDescription.contains("AADSTS50126")) {
                            errorLine = "Error validating credentials due to invalid username or password.";
                        } else {
                            LOGGER.error("Unexpected error response: {}", entityError);
                        }

                    } catch (Exception e){
                        LOGGER.error("Unable to deserialize error ", e);
                    }
                }

                throw new RuntimeException(errorLine);
            });
        } catch (IOException e) {
            throw new RuntimeException("Error in the deserialization from SharePoint response", e);
        }
    }

    public void addAuthorizationHeader(HttpRequestBase requestBase, String token) {
        requestBase.addHeader("Authorization", "Bearer "+ token);
    }
}
