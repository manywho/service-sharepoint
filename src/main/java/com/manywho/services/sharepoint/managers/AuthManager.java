package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.UserObject;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthorizationType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.entities.ServiceConfiguration;
import com.manywho.services.sharepoint.services.AuthenticationService;
import com.manywho.services.sharepoint.services.AuthorizationService;
import org.scribe.oauth.OAuthService;

import javax.inject.Inject;

public class AuthManager {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private PropertyCollectionParser propertyParser;

    @Inject
    public AuthManager(AuthenticationService authenticationService, AuthorizationService authorizationService,
                       PropertyCollectionParser propertyParser){
        this.authenticationService = authenticationService;
        this.propertyParser = propertyParser;
        this.authorizationService = authorizationService;
    }

    public AuthenticatedWhoResult authenticateUser(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {
        if (credentials.getSessionToken()!= null) {
            return authenticationService.getAuthenticatedWhoResult(credentials);
        }

        return authenticationService.getAuthenticatedWhoResult(provider, credentials);
    }

    public ObjectDataResponse authorizeUser(OAuthService oauthService, AbstractOauth2Provider provider, AuthenticatedWho user, ObjectDataRequest objectDataRequest) {
        // Check if the logged-in user is authorized for this flow
        String authorizationStatus = authorizationService.getUserAuthorizationStatus(objectDataRequest.getAuthorization(), user);

        UserObject userObject = new UserObject(
                provider.getName(),
                AuthorizationType.Oauth2,
                oauthService.getAuthorizationUrl(null),
                authorizationStatus
        );

        return new ObjectDataResponse(userObject);
    }

    public ObjectDataResponse loadGroups(ObjectDataRequest objectDataRequest) throws Exception {
        ServiceConfiguration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), ServiceConfiguration.class);

        return new ObjectDataResponse(new ObjectCollection());
        //todo restore this line when we made the changes to be safe again and remove ignore from test
        //return new ObjectDataResponse(authorizationService.loadGroups(configuration.getEnterpriseId()));
    }

    public ObjectDataResponse loadGroupAttributes() {
        return new ObjectDataResponse(authorizationService.loadGroupAttributes());
    }

    public ObjectDataResponse loadUsers(ObjectDataRequest objectDataRequest) throws Exception {
        ServiceConfiguration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), ServiceConfiguration.class);

        //todo restore this line when we made the changes to be safe again and remove ignore from test
        return new ObjectDataResponse(new ObjectCollection());
        //return new ObjectDataResponse(authorizationService.loadUsers(configuration.getEnterpriseId()));
    }

    public ObjectDataResponse loadUsersAttributes() {
        return new ObjectDataResponse(authorizationService.loadUsersAttributes());
    }
}
