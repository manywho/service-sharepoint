package com.manywho.services.sharepoint.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.client.HttpClient;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.manywho.services.sharepoint.constants.ApiConstants.*;

public class AuthenticationClient {
    private HttpClient httpclient;
    private AppConfiguration configuration;
    private ObjectMapper mapper;

    @Inject
    public AuthenticationClient(AppConfiguration configuration, HttpClient httpClient, ObjectMapper mapper) {
        this.httpclient = httpClient;
        this.configuration = configuration;
        this.mapper = mapper;
    }

    public AuthResponse getAccessTokenByAuthCode(String authCode, String redirectUri, String clientId,
                                                 String clientSecret, String resource) {
        try {
            HttpPost httpPost = new HttpPost(String.format("%s/%s", AUTHORITY_URI, "oauth2/token"));

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

            return mapper.readValue(httpclient.executeRequest(httpPost), AuthResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public AuthResponse getAccessTokenByContextToken(String uri, String grantType, String clientId, String clientSecret,
                                                     String refreshToken, String resource) {
        HttpPost httpPost = new HttpPost(uri);

        List<NameValuePair> formParams = new ArrayList<>();

        formParams.add(new BasicNameValuePair("grant_type", grantType));
        formParams.add(new BasicNameValuePair("resource", resource));
        formParams.add(new BasicNameValuePair("client_id", clientId));
        formParams.add(new BasicNameValuePair("client_secret", clientSecret));
        formParams.add(new BasicNameValuePair("refresh_token", refreshToken));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        httpPost.setEntity(entity);
        String body = httpclient.executeRequest(httpPost);

        try {
            return mapper.readValue(body, AuthResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserialization of access token");
        }

    }

    public AuthResponse getAccessTokenFromUserCredentials(String username, String password) {

        HttpPost httpPost = new HttpPost(AUTHORITY + "/oauth2/token");
        List<NameValuePair> formParams = new ArrayList<>();

        formParams.add(new BasicNameValuePair("grant_type", "password"));
        formParams.add(new BasicNameValuePair("username", username));
        formParams.add(new BasicNameValuePair("password", password));
        formParams.add(new BasicNameValuePair("resource", RESOURCE_GRAPH));
        formParams.add(new BasicNameValuePair("client_id", configuration.getOauth2ClientId()));
        formParams.add(new BasicNameValuePair("client_secret", configuration.getOauth2ClientSecret()));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        httpPost.setEntity(entity);
        String body = httpclient.executeRequest(httpPost);

        try {
            return mapper.readValue(body, AuthResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserialization of access token", e);
        }
    }

    public UserResponse getCurrentUser(String token) {
        HttpGet httpGet = new HttpGet(GRAPH_ENDPOINT_BETA + "/me");
        httpclient.addAuthorizationHeader(httpGet, token);

        try {
            String body = httpclient.executeRequest(httpGet);

            return mapper.readValue(body, UserResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing user", e);
        }
    }
}