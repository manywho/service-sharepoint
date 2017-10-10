package com.manywho.services.sharepoint.facades;


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

    public SharePointFacadeInterface get() {
        return sharePointServiceFacade;
    }
}
