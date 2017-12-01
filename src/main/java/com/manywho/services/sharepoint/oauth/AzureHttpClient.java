package com.manywho.services.sharepoint.oauth;

import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;
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

    public AuthResponse getAccessTokenByAuthCode(String authCode, String redirectUri, String clientId,
                                                 String clientSecret, String resource) {
        try {
            HttpPost httpPost = new HttpPost(String.format("%s/%s", ServiceConfigurationImpl.AUTHORITY_URI, "oauth2/token"));

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
//            try {
//                //httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
//            try {
//                httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
