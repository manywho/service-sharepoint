package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.entities.Configuration;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TypeManager {

    private final static String AUTHORITY = "https://login.windows.net/common";
    private final static String RESOURCE_GRAPH = "00000003-0000-0000-c000-000000000000";

    private SecurityConfiguration securityConfiguration;

    @Inject
    public TypeManager(SecurityConfiguration configuration) {
        this.securityConfiguration = configuration;
    }

    public TypeElementCollection getTypeElements(Configuration configuration) {
        try {
            AuthenticationResult authenticationResult  = getAccessTokenFromUserCredentials(RESOURCE_GRAPH,
                    configuration.getUsername(),
                    configuration.getPassword());

            authenticationResult.getAccessToken();


        } catch (Exception e) {
            return new TypeElementCollection();
        }

        return new TypeElementCollection();
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
