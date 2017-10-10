package com.manywho.services.sharepoint;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.facades.*;
import com.manywho.services.sharepoint.managers.*;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.oauth.SharepointProvider;
import com.manywho.services.sharepoint.services.*;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SharepointProvider.class).to(AbstractOauth2Provider.class);

        bind(SharePointServiceFacade.class).to(SharePointServiceFacade.class);
        bind(SharePointOdataFacade.class).to(SharePointOdataFacade.class);
        bind(SharepointFacadeFactory.class).to(SharepointFacadeFactory.class);

        bind(AzureHttpClient.class).to(AzureHttpClient.class);
        bind(FileManager.class).to(FileManager.class);
        bind(SiteManager.class).to(SiteManager.class);
        bind(ListManager.class).to(ListManager.class);
        bind(AuthManager.class).to(AuthManager.class);
        bind(ItemManager.class).to(ItemManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(AuthorizationService.class).to(AuthorizationService.class);
        bind(SecurityConfiguration.class).to(SecurityConfiguration.class);
        bind(FolderManager.class).to(FolderManager.class);
        bind(FileService.class).to(FileService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(FileSharePointService.class).to(FileSharePointService.class);
        bind(SharePointOdataFacade.class).to(SharePointOdataFacade.class);
        bind(SharePointServiceFacade.class).to(SharePointServiceFacade.class);
        bind(FolderSharePointService.class).to(FolderSharePointService.class);

    }
}
