package com.manywho.services.sharepoint.services;

//import com.independentsoft.share.Folder;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.factories.SharePointFacadeFactory;
import javax.inject.Inject;

public class FolderSharePointService {
    private SharePointFacadeFactory sharePointFacadeFactory;

    @Inject
    public FolderSharePointService(SharePointFacadeFactory sharePointFacadeFactory) {
        this.sharePointFacadeFactory = sharePointFacadeFactory;
    }
    
//    public Folder createFolder(String token, String parentFolderId, String name, Configuration configuration) throws Exception {
//
//        return sharePointFacadeFactory.createSharePointFacade( configuration.getHost(),
//                configuration.getUsername(), configuration.getPassword())
//                .createFolder(parentFolderId + "/" + name);
//    }
//
//    public Folder fetchFolder(String token, Configuration configuration, String folderPath) {
//
//        return sharePointFacadeFactory.createSharePointFacade( configuration.getHost(),
//                configuration.getUsername(), configuration.getPassword())
//                .fetchFolder(folderPath);
//    }
}
