package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.draw.elements.type.TypeElement;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.entities.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.util.Optional;
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
        TypeElementCollection typeElements = new TypeElementCollection();
        try {

            AuthenticationResult authenticationResult  = getAccessTokenFromUserCredentials(RESOURCE_GRAPH,
                    configuration.getUsername(),
                    configuration.getPassword());

            String token = authenticationResult.getAccessToken();
            ObjectDataResponse objectDataResponse = this.sharePointOdataFacade.fetchSites(configuration, token);
            ObjectCollection sitesCollection = objectDataResponse.getObjectData();

            for (MObject site: sitesCollection) {

                Optional<Property> siteNameOptional= site.getProperties().stream().filter(p -> p.getDeveloperName().equals("Name"))
                        .findFirst();

                String siteName= "";
                if (siteNameOptional.isPresent()) {
                    siteName = siteNameOptional.get().getContentValue();
                }

                ObjectDataResponse lists = sharePointOdataFacade.fetchLists(configuration, token, site.getExternalId(), true);

                for (MObject list: lists.getObjectData() ) {
                    String typeDeveloperName = String.format("Type %s (%s)", list.getDeveloperName(), siteName);
                    String typeDeveloperSummary =  String.format("Type for list \"%s\" in site \"%s\"", list.getDeveloperName(), siteName);

                    TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                            .setDeveloperName(typeDeveloperName)
                            .setTableName(list.getDeveloperName());

                    for (Property property:list.getProperties()) {
                        typeBuilder.addProperty(property.getDeveloperName(), ContentType.String, property.getDeveloperName());
                    }

                    TypeElement typeElement= typeBuilder.build();
                    typeElement.setDeveloperSummary(typeDeveloperSummary);

                    typeElements.add(typeElement);
                }
            }

        } catch (Exception e) {
            return typeElements;
        }

        return typeElements;
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
