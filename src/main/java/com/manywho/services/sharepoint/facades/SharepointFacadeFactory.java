package com.manywho.services.sharepoint.facades;

import com.manywho.services.sharepoint.configuration.ServiceConfigurationImpl;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

public class SharepointFacadeFactory {
    private SharePointOdataFacade sharePointOdataFacade;
    private SharePointServiceFacade sharePointServiceFacade;

    @Context
    HttpHeaders headers;

    @Inject
    public SharepointFacadeFactory(SharePointOdataFacade sharePointOdataFacade, SharePointServiceFacade sharePointServiceFacade) {
        this.sharePointOdataFacade = sharePointOdataFacade;
        this.sharePointServiceFacade = sharePointServiceFacade;
    }

    public SharePointFacadeInterface get(String typeIdentityProvider) {
        if (typeIdentityProvider.equals(ServiceConfigurationImpl.AUTH_TYPE)) {

            return sharePointOdataFacade;
        } else {
            return sharePointServiceFacade;
        }
    }
}
