package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;

import javax.inject.Inject;

public class TypeItemManager {

    private SharepointFacadeFactory sharepointFacadeFactory;

    @Inject
    public TypeItemManager(SharepointFacadeFactory sharepointFacadeFactory){
        this.sharepointFacadeFactory = sharepointFacadeFactory;
    }

    public ObjectDataResponse loadTypeItems(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) {
        SharePointFacadeInterface sharePointFacade = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider());

        authenticatedWho.getToken();
        objectDataRequest.getObjectDataType().getDeveloperName();
        objectDataRequest.getObjectDataType().getProperties();

        return sharePointFacade.fetchDynamicType(null, authenticatedWho.getToken(),
                objectDataRequest.getObjectDataType().getDeveloperName(),
                objectDataRequest.getObjectDataType().getProperties());
    }
}
