package com.manywho.services.sharepoint.auth.authentication;

import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.controllers.AbstractAuthenticationController;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/authentication")
public class AuthenticationController extends AbstractAuthenticationController {
    private final UserFetcher authenticationService;

    @Inject
    public AuthenticationController(UserFetcher authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Override
    public AuthenticatedWhoResult authentication(AuthenticationCredentials credentials) throws Exception {

        if (credentials.getSessionToken()!= null) {
            return authenticationService.getAuthenticatedWhoResultByContextToken(credentials);
        }

        return authenticationService.getAuthenticatedWhoResultByAuthCode(credentials);
    }
}
