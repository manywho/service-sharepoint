package com.manywho.services.sharepoint.files;

import  com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.FileListFilter;
import com.manywho.sdk.services.files.FileHandler;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;
import com.manywho.services.sharepoint.files.upload.responses.SessionCreated;
import com.manywho.services.sharepoint.files.upload.responses.UploadStatus;
import com.manywho.services.sharepoint.files.upload.FileClient;

import java.util.List;

public class FileUploadHandler implements FileHandler<ServiceConfiguration> {

    private FileClient fileClient;
    private TokenManager tokenManager;

    @Inject
    public FileUploadHandler(FileClient fileClient,
                             TokenManager tokenManager) {

        this.fileClient = fileClient;
        this.tokenManager = tokenManager;
    }

    @Override
    public List<$File> findAll(ServiceConfiguration configuration, FileListFilter listFilter, String path) {
        return Lists.newArrayList();
    }

    @Override
    public $File upload(ServiceConfiguration configuration, String path, FileUpload upload) {
        String driveId = FileIdExtractor.extractDriveIdFromUniqueId(path);
        //folder
        String folderId = FileIdExtractor.extractDriveItemIdFromUniqueId(path);
        tokenManager.addinTokenNotSupported(configuration, "upload file");
        String token = tokenManager.getToken(configuration);
        SessionCreated fileSessionCreated = fileClient.getSession(token, driveId, folderId, upload.getName());
        UploadStatus uploadStatus = fileClient.uploadBigFile(fileSessionCreated.getUploadUrl(), upload);

        return fileClient.setFileName(token, driveId, folderId, uploadStatus.getId(), upload.getName());
    }
}
