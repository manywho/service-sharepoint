package com.manywho.services.sharepoint.services.file;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSharePointService {
    private final static String GRAPH_ENDPOINT_FILE = "https://graph.microsoft.com/v1.0";

    private CloseableHttpClient httpclient;

    public FileSharePointService() {
        this.httpclient = HttpClients.createDefault();
    }

    public String getAnUploadUrl(String token, BodyPart bodyPart, String filePath) {



        //HttpPost httpPost = new HttpPost(String.format("%s/%s/%s:/createUploadSession", GRAPH_ENDPOINT_FILE, filePath,
        //        bodyPart.getContentDisposition().getFileName()));
        HttpPost httpPost = new HttpPost("https://graph.microsoft.com/v1.0/me/drive/root:/Documents/josetest/Documents/Test.pdf:/createUploadSession");

        httpPost.addHeader("Authorization", String.format("Bearer %s", token));
        StringEntity requestEntity = new StringEntity("{\"item\": {\"@microsoft.graph.conflictBehavior\": \"rename\"}}", "UTF-8");
        requestEntity.setContentType("application/json");
        httpPost.setEntity(requestEntity);

        try {
            ResponseUrlUpload response = httpclient.execute(httpPost, new GenericResponseHandler<>(ResponseUrlUpload.class));

            return response.getUploadUrl();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void uploadFile(String token, String uploadPath, BodyPart bodyPart) {
        HttpPut httpPut = new HttpPut(uploadPath);
        //httpPut.addHeader("Authorization", String.format("Bearer %s", token));

        try (InputStream inputStream = bodyPart.getEntityAs(BodyPartEntity.class).getInputStream()) {
            //byte[] byteArray = IOUtils.toByteArray(inputStream);

            //InputStreamEntity entity = new InputStreamEntity(inputStream,byteArray.length, ContentType.APPLICATION_OCTET_STREAM);
            //entity.setChunked(true);
            //httpPut.setEntity(entity);

            //HttpEntity entity = MultipartEntityBuilder.create()
            //        .addBinaryBody("content", inputStream)
            //        .build();

            //httpPut.setEntity(entity);



            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("content", inputStream, ContentType.MULTIPART_FORM_DATA, bodyPart.getContentDisposition().getFileName())
                    .setMode(HttpMultipartMode.STRICT)
                    .build();

            entity.
            httpPut.setEntity(entity);
            httpPut.addHeader("Range", "bytes=0-0");

            httpclient.execute(httpPut, new GenericResponseHandler(ResponseUrlUpload.class));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void uploadSmallFile(String token, String path, BodyPart bodyPart) {
        HttpPut httpPut = new HttpPut(String.format("%s/%s/%s:/content",GRAPH_ENDPOINT_FILE, path,
                bodyPart.getContentDisposition().getFileName()));

        httpPut.addHeader("Authorization", String.format("Bearer %s", token));

        try (InputStream inputStream = bodyPart.getEntityAs(BodyPartEntity.class).getInputStream()) {
            InputStreamEntity entity = new InputStreamEntity(inputStream);
            entity.setContentType("text/plain");
            httpPut.setEntity(entity);
            CreatedFile response = (CreatedFile) httpclient.execute(httpPut, new CreateFileResponseHandler());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
