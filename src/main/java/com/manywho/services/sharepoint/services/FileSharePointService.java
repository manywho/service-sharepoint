package com.manywho.services.sharepoint.services;

import com.independentsoft.share.File;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.factories.SharePointFacadeFactory;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileSharePointService {
    final public static String DEFAULT_ROOT_PATH = "Shared Documents";

    @Inject
    private SharePointFacadeFactory sharePointFacadeFactory;

    public File uploadFileToSharepoint(String token, Configuration configuration, FileDataRequest fileDataRequest, BodyPart filePart) {
        try {
            InputStream inputStream = filePart.getEntityAs(BodyPartEntity.class).getInputStream();
            String uploadPath = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "/" + DEFAULT_ROOT_PATH;

            try {
                return sharePointFacadeFactory.createSharePointFacadeForSubdomain( configuration.getSubdomain(),
                        configuration.getUsername(), configuration.getPassword())
                        .createFile(uploadPath + "/" + filePart.getContentDisposition().getFileName(), inputStream);
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<File> fetchFiles(String token, Configuration configuration, FileDataRequest fileDataRequest) {
        String folderPath = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "/" + DEFAULT_ROOT_PATH;

        return sharePointFacadeFactory.createSharePointFacadeForSubdomain( configuration.getSubdomain(),
                configuration.getUsername(), configuration.getPassword())
                .fetchFiles(folderPath);

    }
}
