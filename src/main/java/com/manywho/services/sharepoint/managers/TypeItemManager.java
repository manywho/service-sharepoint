package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
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

        return sharePointFacade.fetchTypesFromLists(getServiceConfiguration(objectDataRequest.getConfigurationValues()),
                authenticatedWho.getToken(), objectDataRequest.getObjectDataType().getDeveloperName(),
                objectDataRequest.getObjectDataType().getProperties());
    }

    public ObjectDataResponse saveTypeItems(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) {
        SharePointFacadeInterface sharePointFacade = sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider());

        return sharePointFacade.saveTypeList(getServiceConfiguration(objectDataRequest.getConfigurationValues()),
                authenticatedWho.getToken(), objectDataRequest.getObjectDataType().getDeveloperName(),
                objectDataRequest.getObjectData().get(0).getProperties());
    }


    private ServiceConfiguration getServiceConfiguration(EngineValueCollection engineValues) {
        String username = null;
        String password = null;
        String host = null;
        Boolean includeDefaultList = false;
        String onlyGroups = "";

        for (EngineValue value:engineValues) {
            switch (value.getDeveloperName()){
                case "Username":
                    username = value.getContentValue();
                    break;
                case "Password":
                    password = value.getContentValue();
                    break;
                case "Host":
                    host = value.getContentValue();
                    break;
                case "include Default Lists?":
                    includeDefaultList = "True".equals(value.getContentValue());
                    break;
                case "Only For Groups":
                    onlyGroups = value.getContentValue();
                    break;
            }
        }

        return new ServiceConfiguration(username, password, host, includeDefaultList, onlyGroups);
    }
}
