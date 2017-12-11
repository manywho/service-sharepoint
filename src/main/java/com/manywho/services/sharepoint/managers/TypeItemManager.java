package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;

import javax.inject.Inject;
import java.util.List;

public class TypeItemManager {

    private SharepointFacadeFactory sharepointFacadeFactory;

    @Inject
    public TypeItemManager(SharepointFacadeFactory sharepointFacadeFactory){
        this.sharepointFacadeFactory = sharepointFacadeFactory;
    }

    public List<MObject> loadTypeItems(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                       ObjectDataType objectDataRequest, ListFilter filter) {

        SharePointFacadeInterface sharePointFacade = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider());

        return sharePointFacade.fetchTypesFromLists(configuration, authenticatedWho.getToken(),
                objectDataRequest.getDeveloperName(), objectDataRequest.getProperties());
    }

    public MObject loadTypeItem(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                       ObjectDataType objectDataRequest, String id) {

        SharePointFacadeInterface sharePointFacade = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider());

        return sharePointFacade.fetchTypeFromList(configuration, authenticatedWho.getToken(),
                objectDataRequest.getDeveloperName(), id,  objectDataRequest.getProperties());
    }

    public MObject updateTypeItem(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration, MObject objectDataRequest) {
        SharePointFacadeInterface sharePointFacade = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider());

        return sharePointFacade.createTypeList(configuration, authenticatedWho.getToken(), objectDataRequest.getDeveloperName(),
                objectDataRequest.getProperties());
    }
}
