package com.manywho.services.sharepoint;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.facades.SharePointFacade;
import com.manywho.services.sharepoint.factories.SharePointFacadeFactory;
import com.manywho.services.sharepoint.managers.AuthManager;
import com.manywho.services.sharepoint.managers.FileManager;
import com.manywho.services.sharepoint.managers.FolderManager;
import com.manywho.services.sharepoint.oauth.SharepointProvider;
import com.manywho.services.sharepoint.services.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SharepointProvider.class).to(AbstractOauth2Provider.class);
        bind(FileManager.class).to(FileManager.class);
        bind(AuthManager.class).to(AuthManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(AuthorizationService.class).to(AuthorizationService.class);
        bind(SecurityConfiguration.class).to(SecurityConfiguration.class);
        bind(FolderManager.class).to(FolderManager.class);
        bind(FileService.class).to(FileService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(FileSharePointService.class).to(FileSharePointService.class);
        bind(SharePointFacade.class).to(SharePointFacade.class);
        bind(SharePointFacadeFactory.class).to(SharePointFacadeFactory.class);
        bind(FolderSharePointService.class).to(FolderSharePointService.class);

    }
}
