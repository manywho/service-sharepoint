package com.manywho.services.sharepoint;

import com.manywho.services.sharepoint.facades.SharepointFacade;
import com.manywho.services.sharepoint.factories.SharePointFacadeFactory;
import com.manywho.services.sharepoint.managers.FileManager;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.FileSharePointService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(FileManager.class).to(FileManager.class);
        bind(FileService.class).to(FileService.class);
        bind(FileSharePointService.class).to(FileSharePointService.class);
        bind(SharepointFacade.class).to(SharepointFacade.class);
        bind(SharePointFacadeFactory.class).to(SharePointFacadeFactory.class);
    }
}
