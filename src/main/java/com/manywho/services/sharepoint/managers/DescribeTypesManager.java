package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DescribeTypesManager {

    private final static String AUTHORITY = "https://login.windows.net/common";
    private final static String RESOURCE_GRAPH = "00000003-0000-0000-c000-000000000000";
    private ApplicationConfiguration securityConfiguration;
    private SharePointOdataFacade sharePointOdataFacade;

    @Inject
    public DescribeTypesManager(ApplicationConfiguration configuration, SharePointOdataFacade sharePointOdataFacade) {
        this.securityConfiguration = configuration;
        this.sharePointOdataFacade = sharePointOdataFacade;
    }

    public TypeElementCollection getTypeElements(ServiceConfiguration configuration) {

        try {
            AuthenticationResult authenticationResult  = getAccessTokenFromUserCredentials(RESOURCE_GRAPH,
                    configuration.getUsername(),
                    configuration.getPassword());

           return this.sharePointOdataFacade.fetchAllListTypes(configuration, authenticationResult.getAccessToken());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AuthenticationResult getAccessTokenFromUserCredentials(String resource, String username,
                                                                          String password) throws Exception {
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                    resource, securityConfiguration.getOauth2ClientId(), username, password,
                    null);
            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }

        return result;
    }
}
