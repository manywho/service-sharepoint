package com.manywho.services.sharepoint.files.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.sharepoint.client.HttpClient;
import com.manywho.services.sharepoint.files.client.responses.SessionCreated;
import com.manywho.services.sharepoint.files.client.responses.UploadStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_V1;

public class FileClient {
    private HttpClient httpclient;
    private ObjectMapper mapper;
    private final Tika tika;

    // max 60 MiB we can use chunk files if we need more in the future
    static private Integer MAX_FILE_SIZE = 62914560;

    @Inject
    public FileClient(Tika tika, HttpClient httpClient, ObjectMapper mapper) {
        this.httpclient = httpClient;
        this.tika = tika;
        this.mapper = mapper;
    }

    public $File moveFile(String token, String driveId, String itemId, String fileId, String name) {
        String fileUrl = String.format("%s/drives/%s/items/%s", GRAPH_ENDPOINT_V1, driveId, fileId);

        try {
            HttpPatch httpPatch = new HttpPatch(fileUrl);

            String jsonBody = String.format("{\"id\": \"%s\",\"name\": \"%s\",\"parentReference\":{\"id\": \"%s\"}}",
                    fileId, name, itemId);

            httpPatch.setEntity(new StringEntity(jsonBody, Consts.UTF_8));
            httpclient.addAuthorizationHeader(httpPatch, token);
            httpPatch.addHeader("Content-Type", "application/json");

            HttpResponse response =  mapper.readValue(httpclient.executeRequest(httpPatch), HttpResponse.class);

            if (response.getStatusLine().getStatusCode() >=200  && response.getStatusLine().getStatusCode() <300) {

                return new $File(fileId, name);
            }

            JSONObject errorStatus = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
            throw new RuntimeException(errorStatus.getJSONObject("error").getString("message"));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String uploadFileRaw(String url, FileUpload upload) {
        try {
            HttpPut httpPut = new HttpPut(url);
            TikaInputStream inputStream = TikaInputStream.get(upload.getContent());
            String mimeType = tika.detect(inputStream);

            byte[] bytes = IOUtils.toByteArray(inputStream);
            int size = bytes.length;

            if (size > MAX_FILE_SIZE) {
                throw new RuntimeException(String.format("The size of your file is \"%s\", the max size supported is %s MiB",
                        size, MAX_FILE_SIZE));
            }

            inputStream.reset();
            InputStreamEntity entity = new InputStreamEntity(inputStream, size, ContentType.parse(mimeType));
            httpPut.setEntity(entity);
            httpPut.setHeader("Content-Range", String.format("bytes %s-%s/%s", 0, size - 1, size));

            UploadStatus uploadStatus = mapper.readValue(httpclient.executeRequest(httpPut), UploadStatus.class);


            if (uploadStatus.isUploadFinished()) {
                return uploadStatus.getId();
            }

        } catch (IOException e) {
            throw new RuntimeException("Problem uploading file");
        }

        throw new RuntimeException("Problem uploading file");
    }

    public SessionCreated getSession(String token, String driveId, String itemId, String filename) {

        String fileUrl = String.format("%s/drives/%s/items/%s/createUploadSession",
                GRAPH_ENDPOINT_V1,
                driveId,
                itemId
        );

        HttpPost httpPost = new HttpPost(fileUrl);

        String jsonBody = "{\"item\": {\"@microsoft.graph.conflictBehavior\": \"rename\"}}";

        StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpclient.addAuthorizationHeader(httpPost, token);

        httpPost.setEntity(requestEntity);

        try {
            return mapper.readValue(httpclient.executeRequest(httpPost), SessionCreated.class);

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file");
        }
    }
}
