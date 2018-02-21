package com.manywho.services.sharepoint.auth;

import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;

import javax.inject.Inject;

import static com.manywho.services.sharepoint.configuration.ApiConstants.AUTHENTICATION_TYPE_ADD_IN;

/**
 * If we are using a token from the sharepoint Add-in (from the context token), this token is not valid
 * for use with the new graph api
 *
 * The Authentication Strategy -> SuperUser will allow us to have more available functionality when using the addin.
 */
public class TokenManager {
    private OauthAuthenticationClient oauthAuthenticationClient;
    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private final static String AUTH_STRATEGY_SUPER_USER = "SuperUser";

    @Inject
    public TokenManager(OauthAuthenticationClient oauthAuthenticationClient, AuthenticatedWhoProvider authenticatedWhoProvider) {

        this.oauthAuthenticationClient = oauthAuthenticationClient;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    /**
     * If the strategy SuperUser is selected, then the token is fetch from username and password
     * @param configuration
     * @return
     */
    public String getToken(ServiceConfiguration configuration) {
        if (AUTH_STRATEGY_SUPER_USER.equals(configuration.getStrategy())) {

            return oauthAuthenticationClient.getAccessTokenFromUserCredentials(configuration.getUsername(), configuration.getPassword())
                    .getAccessToken();
        }

        return authenticatedWhoProvider.get().getToken();
    }

    public void addinTokenNotSupported(ServiceConfiguration configuration, String functionality) {

        if (AUTH_STRATEGY_SUPER_USER.equals(configuration.getStrategy()) == false &&
                authenticatedWhoProvider.get().getIdentityProvider().equals(AUTHENTICATION_TYPE_ADD_IN) == true) {

            String message = String.format("The %s functionality is only available for add-in with authentication strategy SuperUser",
                    functionality);

            throw new RuntimeException(message);
        }
    }

    /**
     * When we get the access token from the context token, then we can only use office api services (instead of the new odata)
     * @param serviceConfiguration
     * @return
     */
    public boolean shouldUseServices(ServiceConfiguration serviceConfiguration) {
        return ! AUTH_STRATEGY_SUPER_USER.equals(serviceConfiguration.getStrategy()) &&
                AUTHENTICATION_TYPE_ADD_IN.equals(authenticatedWhoProvider.get().getIdentityProvider());

    }
}
