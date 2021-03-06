package com.manywho.services.sharepoint.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.client.entities.AuthResponse;
import com.manywho.services.sharepoint.client.entities.UserResponse;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.manywho.services.sharepoint.configuration.ApiConstants.*;

public class OauthAuthenticationClient {
    private HttpClient httpclient;
    private AppConfiguration configuration;
    private ObjectMapper mapper;

    private static final String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";

    @Inject
    public OauthAuthenticationClient(AppConfiguration configuration, HttpClient httpClient, ObjectMapper mapper) {
        this.httpclient = httpClient;
        this.configuration = configuration;
        this.mapper = mapper;
    }

    public AuthResponse getAccessTokenByAuthCode(String authCode, String resource) {
        try {
            HttpPost httpPost = new HttpPost(String.format("%s/%s", AUTHORITY_URI, "oauth2/token"));

            List<NameValuePair> formParams = new ArrayList<>();

            formParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
            formParams.add(new BasicNameValuePair("code", authCode));
            formParams.add(new BasicNameValuePair("resource", resource));
            formParams.add(new BasicNameValuePair("client_id", configuration.getOauth2ClientId()));
            formParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
            formParams.add(new BasicNameValuePair("client_secret", configuration.getOauth2ClientSecret()));

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
            throw new RuntimeException("Error deserialization of access getToken");
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
            throw new RuntimeException("Error deserialization of access getToken", e);
        }
    }

    public UserResponse getCurrentUser(String token) {
        HttpGet httpGet = new HttpGet(GRAPH_ENDPOINT_V1 + "/me");
        httpclient.addAuthorizationHeader(httpGet, token);

        try {
            String body = httpclient.executeRequest(httpGet);

            return mapper.readValue(body, UserResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing user", e);
        }
    }
}