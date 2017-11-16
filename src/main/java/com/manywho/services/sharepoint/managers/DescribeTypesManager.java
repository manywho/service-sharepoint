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
import org.apache.commons.codec.binary.Hex;

import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();

            AuthenticationResult authenticationResult  = getAccessTokenFromUserCredentials(RESOURCE_GRAPH,
                    configuration.getUsername(),
                    configuration.getPassword());

            String token = authenticationResult.getAccessToken();
            ObjectDataResponse objectDataResponse = this.sharePointOdataFacade.fetchSites(configuration, token);
            ObjectCollection sitesCollection = objectDataResponse.getObjectData();


            for (MObject site: sitesCollection) {
                ObjectDataResponse lists = sharePointOdataFacade.fetchLists(configuration, token, site.getExternalId(), true);

                for (MObject list1: lists.getObjectData() ) {
                    TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                            .setDeveloperName(list1.getDeveloperName() + " " + getMd5(list1.getExternalId(), messageDigest))
                            .setTableName(list1.getDeveloperName());

                    for (Property property:list1.getProperties()) {
                        typeBuilder.addProperty(property.getDeveloperName(), ContentType.String, property.getDeveloperName());
                    }
                    typeElements.add(typeBuilder.build());
                }
            }

        } catch (Exception e) {
            return typeElements;
        }

        return typeElements;
    }

    private String getMd5(String longId, MessageDigest messageDigest) throws NoSuchAlgorithmException {

        messageDigest.update(longId.getBytes(Charset.forName("UTF8")));
        final byte[] resultByte = messageDigest.digest();
        return new String(Hex.encodeHex(resultByte));
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
