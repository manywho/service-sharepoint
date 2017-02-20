package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointFacade;

import javax.inject.Inject;

public class SiteManager {
    private PropertyCollectionParser propertyParser;
    private SharePointFacade sharePointFacade;

    @Inject
    public SiteManager(PropertyCollectionParser propertyParser, SharePointFacade sharePointFacade) {
        this.propertyParser = propertyParser;
        this.sharePointFacade = sharePointFacade;
    }

    public ObjectDataResponse loadSites(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);
        return sharePointFacade.fetchSites(authenticatedWho.getToken());
    }
}
