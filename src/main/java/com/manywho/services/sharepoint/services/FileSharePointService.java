package com.manywho.services.sharepoint.services;

import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointFacade;
import com.microsoft.services.graph.Item;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class FileSharePointService {
    final public static String DEFAULT_ROOT_PATH = "Shared Documents";
    private SharePointFacade sharePointFacade;

    @Inject
    public FileSharePointService(SharePointFacade sharePointFacade) {
        this.sharePointFacade = sharePointFacade;
    }

    public Item uploadFileToSharepoint(AuthenticatedWho user, Configuration configuration, FileDataRequest fileDataRequest, BodyPart filePart) {
        try {
            InputStream inputStream = filePart.getEntityAs(BodyPartEntity.class).getInputStream();
            String uploadPath = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "/" + DEFAULT_ROOT_PATH;
            uploadPath = "https://manywho.sharepoint.com/josetest/Shared%20Documents/Test.pdf";
            Item createdItem = sharePointFacade.createFile(user.getToken(), user.getUserId(), uploadPath, inputStream);

            return createdItem;
        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
