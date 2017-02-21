package com.manywho.services.sharepoint.oauth;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AzureHttpClient {
    private CloseableHttpClient httpclient;

    public AzureHttpClient(){
        this.httpclient = HttpClients.createDefault();
    }

//    public AuthResponse getAccessTokenByUsernamePassword(String userName, String password, String clientId,
//                                                         String clientSecret, String resource) {
//        try {
//            SharePointList<NameValuePair> formParams = new ArrayList<>();
//            formParams.add(new BasicNameValuePair("grant_type", "password"));
//            formParams.add(new BasicNameValuePair("resource", "00000003-0000-0000-c000-000000000000"));
//            formParams.add(new BasicNameValuePair("client_id", "37b7a843-eb48-432a-b4cd-f201c8ee084d"));
//            formParams.add(new BasicNameValuePair("username", "email"));
//            formParams.add(new BasicNameValuePair("password", "pass"));
//            formParams.add(new BasicNameValuePair("client_secret", "LTOHbWa7oobfjQsWDQrqvkn"));
//
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
//
//            HttpPost httpPost = new HttpPost(String.format("%s/%s", AUTHORITY_URL, "oauth2/token"));
//            httpPost.setEntity(entity);
//
//            return (AuthResponse) httpclient.execute(httpPost, new AuthResponseHandler());
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
}
