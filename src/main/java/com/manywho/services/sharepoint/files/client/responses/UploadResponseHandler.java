package com.manywho.services.sharepoint.files.client.responses;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class UploadResponseHandler implements ResponseHandler {

    @Override
    public UploadStatus handleResponse(HttpResponse httpResponse) throws IOException {
        int status = httpResponse.getStatusLine().getStatusCode();

        if (status >= 200 && status < 300) {
            HttpEntity entity2 = httpResponse.getEntity();
            if (entity2 == null) {
                throw new RuntimeException("Error when auth request");
            } else {
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                return mapper.readValue(EntityUtils.toString(entity2), UploadStatus.class);
            }
        }
        //IOUtils.toString(httpResponse.getEntity().getContent())
        throw new RuntimeException(httpResponse.getStatusLine().toString());
    }
}
