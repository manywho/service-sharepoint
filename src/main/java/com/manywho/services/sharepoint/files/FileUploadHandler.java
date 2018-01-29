package com.manywho.services.sharepoint.files;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.FileListFilter;
import com.manywho.sdk.services.files.FileHandler;
import com.manywho.sdk.services.files.FileUpload;
import com.manywho.sdk.services.types.system.$File;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.files.client.FileClient;
import com.manywho.services.sharepoint.files.client.responses.SessionCreated;
import com.manywho.services.sharepoint.files.utilities.FileIdExtractor;

import java.util.List;

public class FileUploadHandler implements FileHandler<ServiceConfiguration> {

    private FileClient fileClient;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public FileUploadHandler(FileClient fileClient,
                             TokenCompatibility tokenCompatibility) {

        this.fileClient = fileClient;
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public List<$File> findAll(ServiceConfiguration configuration, FileListFilter listFilter, String path) {
        return Lists.newArrayList();
    }

    @Override
    public $File upload(ServiceConfiguration configuration, String path, FileUpload upload) {
        String driveId = FileIdExtractor.extractDriveIdFromFileId(path);
        String itemId = FileIdExtractor.extractDriveItemIdFromFileId(path);

        tokenCompatibility.addinTokenNotSupported(configuration, "upload file");

        String token = tokenCompatibility.getToken(configuration);

        SessionCreated fileSessionCreated = fileClient.getSession(token, driveId,
                itemId, upload.getName());

        String fileId = fileClient.uploadFileRaw(fileSessionCreated.getUploadUrl(), upload);

        return fileClient.moveFile(token, driveId, itemId, fileId, upload.getName());
    }
}
