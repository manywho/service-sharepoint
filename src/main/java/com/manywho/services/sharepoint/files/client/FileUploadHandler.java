package com.manywho.services.sharepoint.files.client;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.FileListFilter;
import com.manywho.sdk.services.files.FileHandler;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;
import com.manywho.services.sharepoint.files.client.responses.SessionCreated;
import com.manywho.services.sharepoint.files.client.responses.SessionResponseHandler;
import com.manywho.services.sharepoint.files.client.responses.UploadResponseHandler;
import com.manywho.services.sharepoint.files.client.responses.UploadStatus;
import com.manywho.services.sharepoint.files.utilities.FileIdExtractor;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;

import java.io.IOException;
import java.util.List;

public class FileUploadHandler implements FileHandler<ApplicationConfiguration> {


    private CloseableHttpClient httpclient;
    private ServiceConfigurationImpl configuration;
    private final static String GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0";
    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private final Tika tika;
    // max 60 MiB we can use chunk files if we need more in the future
    static private Integer MAX_FILE_SIZE = 62914560;

    @Inject
    public FileUploadHandler(AuthenticatedWhoProvider authenticatedWhoProvider, ServiceConfigurationImpl configuration,
                             Tika tika) {
        this.httpclient = HttpClients.createDefault();
        this.configuration = configuration;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.tika = tika;
    }

    @Override
    public List<$File> findAll(ApplicationConfiguration configuration, FileListFilter listFilter, String path) {
        return Lists.newArrayList();
    }

    @Override
    public $File upload(ApplicationConfiguration configuration, String path, FileUpload upload) {
        String driveId = FileIdExtractor.extractDriveIdFromFileId(path);
        String itemId = FileIdExtractor.extractDriveItemIdFromFileId(path);

        SessionCreated fileSessionCreated = getSession(authenticatedWhoProvider.get().getToken(), driveId, itemId,
                upload.getName());

        String fileId = uploadFileRaw(fileSessionCreated.getUploadUrl(), upload);
        return moveFile(authenticatedWhoProvider.get().getToken(), driveId, itemId, fileId, upload.getName());
    }

    private $File moveFile(String token, String driveId, String itemId, String fileId, String name) {
        CloseableHttpClient httpclient =  HttpClients.createDefault();
        String fileUrl = String.format("%s/drives/%s/items/%s", GRAPH_ENDPOINT, driveId, fileId);

        try {
            HttpPatch httpPatch = new HttpPatch(fileUrl);

            String jsonBody =String.format("{" +
                            "\"id\": \"%s\"," +
                            "\"name\": \"%s\"," +
                            "\"parentReference\":" +
                            "{" +
                                    "\"id\": \"%s\"" +
                            "}" +
            "}", fileId, name, itemId);

            httpPatch.setEntity(new StringEntity(jsonBody, Consts.UTF_8));
            httpPatch.addHeader("Authorization", String.format("Bearer %s", token));
            httpPatch.addHeader("Content-Type", "application/json");

            HttpResponse response =  httpclient.execute(httpPatch);

            if (response.getStatusLine().getStatusCode() >=200  && response.getStatusLine().getStatusCode() <300) {
                return new $File(fileId, name);
            }

            throw new RuntimeException("Error updating object");
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

    private String uploadFileRaw(String url, FileUpload upload) {
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

            UploadStatus uploadStatus = (UploadStatus) httpclient.execute(httpPut, new UploadResponseHandler());

            if (uploadStatus.isUploadFinished()) {
                return uploadStatus.getId();
            }

        } catch (IOException e) {
            throw new RuntimeException("problem uploading file");
        }

        throw new RuntimeException("problem uploading file");
    }

    private SessionCreated getSession(String token, String driveId, String itemId, String filename) {

        String fileUrl = String.format("%s/drives/%s/items/%s/createUploadSession",
                GRAPH_ENDPOINT,
                driveId,
                itemId
        );

        HttpPost httpPost = new HttpPost(fileUrl);

        String jsonBody = "{" +
                "  \"item\": {" +
                "    \"@microsoft.graph.conflictBehavior\": \"rename\"" +
                "  }" +
                "}";

        StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setHeader("Authorization", String.format("Bearer %s", token));
        httpPost.setEntity(requestEntity);

        try {
            return (SessionCreated) httpclient.execute(httpPost, new SessionResponseHandler());

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file");
        }
    }
}
