package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.microsoft.aad.adal4j.AuthenticationResult;
import javax.inject.Inject;

public class DescribeTypesManager {


    private SharePointOdataFacade sharePointOdataFacade;
    private AzureHttpClient azureHttpClient;

    @Inject
    public DescribeTypesManager(SharePointOdataFacade sharePointOdataFacade, AzureHttpClient azureHttpClient) {
        this.sharePointOdataFacade = sharePointOdataFacade;
        this.azureHttpClient = azureHttpClient;
    }

    public TypeElementCollection getTypeElements(ServiceConfiguration configuration) {

        try {
            AuthenticationResult authenticationResult  = azureHttpClient.getAccessTokenFromUserCredentials(
                    configuration.getUsername(),
                    configuration.getPassword());

           return this.sharePointOdataFacade.fetchAllListTypes(configuration, authenticationResult.getAccessToken());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
