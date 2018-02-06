package com.manywho.services.sharepoint.database.dynamic;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.sharepoint.auth.oauth.entities.AuthResponse;
import com.manywho.services.sharepoint.auth.oauth.AuthenticationClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;

import javax.inject.Inject;
import java.util.List;

public class DescribeDynamicTypesManager {

    private SharePointOdataFacade sharePointOdataFacade;
    private AuthenticationClient authenticationClient;

    @Inject
    public DescribeDynamicTypesManager(SharePointOdataFacade sharePointOdataFacade, AuthenticationClient authenticationClient) {
        this.sharePointOdataFacade = sharePointOdataFacade;
        this.authenticationClient = authenticationClient;
    }

    /**
     *  This method is used in the description, each list created by the user will be a dynamic type in the sharepoint
     *  service
     *
     * @param configuration
     * @return
     */
    public List<TypeElement> getTypeElements(ServiceConfiguration configuration) {

        try {
            AuthResponse authenticationResult  = authenticationClient.getAccessTokenFromUserCredentials(
                    configuration.getUsername(),
                    configuration.getPassword());

            return this.sharePointOdataFacade.fetchAllListTypes(configuration, authenticationResult.getAccessToken());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}