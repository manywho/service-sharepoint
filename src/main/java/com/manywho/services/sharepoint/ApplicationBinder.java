package com.manywho.services.sharepoint;

import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.manywho.services.sharepoint.facades.SharePointServiceFacade;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;
import com.manywho.services.sharepoint.managers.*;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.oauth.SharepointProvider;
import com.manywho.services.sharepoint.services.AuthenticationService;
import com.manywho.services.sharepoint.services.AuthorizationService;
import com.manywho.services.sharepoint.services.FileService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SharepointProvider.class).to(AbstractOauth2Provider.class);
        bind(SharePointServiceFacade.class).to(SharePointServiceFacade.class);
        bind(SharePointOdataFacade.class).to(SharePointOdataFacade.class);
        bind(SharepointFacadeFactory.class).to(SharepointFacadeFactory.class);
        bind(PropertyCollectionParser.class).to(PropertyCollectionParser.class);
        bind(AzureHttpClient.class).to(AzureHttpClient.class);
        bind(FileManager.class).to(FileManager.class);
        bind(SiteManager.class).to(SiteManager.class);
        bind(ListManager.class).to(ListManager.class);
        bind(AuthManager.class).to(AuthManager.class);
        bind(ItemManager.class).to(ItemManager.class);
        bind(TypeItemManager.class).to(TypeItemManager.class);
        bind(DescribeTypesManager.class).to(DescribeTypesManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(AuthorizationService.class).to(AuthorizationService.class);
        bind(ApplicationConfiguration.class).to(ApplicationConfiguration.class);
        bind(FileService.class).to(FileService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(FileSharePointService.class).to(FileSharePointService.class);
        bind(SharePointOdataFacade.class).to(SharePointOdataFacade.class);
        bind(SharePointServiceFacade.class).to(SharePointServiceFacade.class);
    }
}
