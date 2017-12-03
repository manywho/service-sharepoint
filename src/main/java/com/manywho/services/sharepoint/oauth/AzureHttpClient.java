package com.manywho.services.sharepoint.oauth;

import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AzureHttpClient {
    private final static String AUTHORITY = "https://login.windows.net/common";
    private ApplicationConfiguration applicationConfiguration;
    private CloseableHttpClient httpclient;
    private final static String RESOURCE_GRAPH = "00000003-0000-0000-c000-000000000000";

    @Inject
    public AzureHttpClient(ApplicationConfiguration applicationConfiguration){
        this.applicationConfiguration = applicationConfiguration;
        this.httpclient = HttpClients.createDefault();
    }

    public AuthResponse getAccessTokenByAuthCode(String authCode, String redirectUri, String clientId,
                                                 String clientSecret, String resource) {
        try {
            HttpPost httpPost = new HttpPost(String.format("%s/%s", SharepointProvider.AUTHORITY_URI, "oauth2/token"));

            List<NameValuePair> formParams = new ArrayList<>();

            formParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
            formParams.add(new BasicNameValuePair("code", authCode));
            formParams.add(new BasicNameValuePair("resource", resource));
            formParams.add(new BasicNameValuePair("client_id", clientId));
            formParams.add(new BasicNameValuePair("redirect_uri", redirectUri));
            formParams.add(new BasicNameValuePair("client_secret", clientSecret));



            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams);
            entity.setChunked(false);
            httpPost.setEntity(entity);

            return (AuthResponse) httpclient.execute(httpPost, new AuthResponseHandler());
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


    public AuthResponse getAccessTokenByContextToken(String uri, String grantType, String clientId, String clientSecret,
                                                     String refreshToken, String resource) {
        try {
            HttpPost httpPost = new HttpPost(uri);


            List<NameValuePair> formParams = new ArrayList<>();

            formParams.add(new BasicNameValuePair("grant_type", grantType));
            formParams.add(new BasicNameValuePair("resource", resource));
            formParams.add(new BasicNameValuePair("client_id", clientId));
            formParams.add(new BasicNameValuePair("client_secret", clientSecret));
            formParams.add(new BasicNameValuePair("refresh_token", refreshToken));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
            httpPost.setEntity(entity);

            return (AuthResponse) httpclient.execute(httpPost, new AuthResponseHandler());
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

    public AuthenticationResult getAccessTokenFromUserCredentials(String username,
                                                                   String password) throws Exception {
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    RESOURCE_GRAPH, applicationConfiguration.getOauth2ClientId(), username, password,
                    null);
            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }

        return result;
    }
}
