package com.manywho.services.sharepoint.services;

import com.manywho.services.sharepoint.facades.SharePointFacade;

import javax.inject.Inject;

public class FileSharePointService {
    final public static String DEFAULT_ROOT_PATH = "Shared Documents";
    private SharePointFacade sharePointFacade;

    @Inject
    public FileSharePointService(SharePointFacade sharePointFacade) {
        this.sharePointFacade = sharePointFacade;
    }
//
//    public Item uploadFileToSharepoint(AuthenticatedWho user, Configuration configuration, FileDataRequest fileDataRequest, BodyPart filePart) {
//        try {
//            InputStream inputStream = filePart.getEntityAs(BodyPartEntity.class).getInputStream();
//            String uploadPath = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "/" + DEFAULT_ROOT_PATH;
//            uploadPath = "https://manywho.sharepoint.com/josetest/Shared%20Documents/Test.pdf";
//            Item createdItem = sharePointFacade.createFile(user.getToken(), user.getUserId(), uploadPath, inputStream);
//
//            return createdItem;
//        } catch (IOException | InterruptedException | ExecutionException ex) {
//            throw new RuntimeException(ex);
//        }
//    }

}
