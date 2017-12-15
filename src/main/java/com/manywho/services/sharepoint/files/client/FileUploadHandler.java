package com.manywho.services.sharepoint.files.client;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.FileListFilter;
import com.manywho.sdk.services.files.FileHandler;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.sharepoint.AppConfiguration;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
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

public class FileUploadHandler implements FileHandler<ServiceConfiguration> {

    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private FileClient fileClient;

    @Inject
    public FileUploadHandler(AuthenticatedWhoProvider authenticatedWhoProvider, FileClient fileClient) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.fileClient = fileClient;
    }

    @Override
    public List<$File> findAll(ServiceConfiguration configuration, FileListFilter listFilter, String path) {
        return Lists.newArrayList();
    }

    @Override
    public $File upload(ServiceConfiguration configuration, String path, FileUpload upload) {
        String driveId = FileIdExtractor.extractDriveIdFromFileId(path);
        String itemId = FileIdExtractor.extractDriveItemIdFromFileId(path);

        SessionCreated fileSessionCreated = fileClient.getSession(authenticatedWhoProvider.get().getToken(), driveId, itemId,
                upload.getName());

        String fileId = fileClient.uploadFileRaw(fileSessionCreated.getUploadUrl(), upload);
        return fileClient.moveFile(authenticatedWhoProvider.get().getToken(), driveId, itemId, fileId, upload.getName());
    }
}
