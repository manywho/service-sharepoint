package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.auth.oauth.AuthenticationClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.constants.ApiConstants;

import javax.inject.Inject;

/**
 * If we are using a token from the sharepoint Add-in (from the context token), this token is not valid
 * for use with the new graph api
 *
 * The Authentication Strategy -> SuperUser will allow us to have more available functionality when using the addin.
 */
public class TokenCompatibility {
    private AuthenticationClient authenticationClient;
    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private SharePointOdataFacade sharePointOdataFacade;
    private SharePointServiceFacade sharePointServiceFacade;
    private final static String AUTH_STRATEGY_SUPER_USER = "SuperUser";

    @Inject
    public TokenCompatibility(AuthenticationClient authenticationClient, AuthenticatedWhoProvider authenticatedWhoProvider,
                              SharePointOdataFacade sharePointOdataFacade, SharePointServiceFacade sharePointServiceFacade) {

        this.authenticationClient = authenticationClient;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.sharePointOdataFacade = sharePointOdataFacade;
        this.sharePointServiceFacade = sharePointServiceFacade;
    }

    /**
     * If the strategy SuperUser is selected, then the token is fetch from username and password
     * @param configuration
     * @return
     */
    public String getToken(ServiceConfiguration configuration) {
        if (AUTH_STRATEGY_SUPER_USER.equals(configuration.getStrategy())) {

            return authenticationClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword())
                    .getAccessToken();
        }

        return authenticatedWhoProvider.get().getToken();
    }

    /**
     * If the strategy SuperUser is selected then we use SharePoint Odata
     * else if the user have a token using oauth we use odata and if the user have a toke from the addin (contextToken)
     * we use sharepoint services
     *
     * @param serviceConfiguration
     * @return
     */
    public SharePointFacadeInterface getSharePointFacade(ServiceConfiguration serviceConfiguration) {
        if (authenticatedWhoProvider.get().getIdentityProvider().equals(ApiConstants.AUTHENTICATION_TYPE_AZURE_AD)) {

            return sharePointOdataFacade;
        } else {
            if (serviceConfiguration.getStrategy().equals(AUTH_STRATEGY_SUPER_USER)) {
                return sharePointOdataFacade;
            }

            return sharePointServiceFacade;
        }
    }

    public void addinTokenNotSupported(ServiceConfiguration configuration, String functionality) {

        if (AUTH_STRATEGY_SUPER_USER.equals(configuration.getStrategy()) == false &&
                authenticatedWhoProvider.get().getIdentityProvider().equals(ApiConstants.AUTHENTICATION_TYPE_ADD_IN) == true) {

            String message = String.format("The %s functionality is only available for add-in with authentication strategy SuperUser",
                    functionality);

            throw new RuntimeException(message);
        }
    }
}
