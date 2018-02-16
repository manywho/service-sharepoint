package com.manywho.services.sharepoint.files.listeners;

import com.manywho.sdk.services.listeners.Listener;
import com.manywho.sdk.services.listeners.ListenerData;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.client.HttpClient;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.files.listeners.persistence.ListenerRequestRepository;
import com.manywho.services.sharepoint.files.upload.facade.DriveFacadeOdata;
import com.manywho.services.sharepoint.types.Drive;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static com.manywho.services.sharepoint.constants.ApiConstants.GRAPH_ENDPOINT_V1;

@Listener.Metadata(event = "DRIVE_ROOT_UPDATED")
public class DriveListener implements Listener<ServiceConfiguration, Drive> {

    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private HttpClient httpClient;
    private UriInfo uriInfo;
    private ListenerRequestRepository listenerRequestRepository;
    private DriveFacadeOdata driveFacade;
    private TokenCompatibility tokenCompatibility;

    @Inject
    public DriveListener(AuthenticatedWhoProvider authenticatedWhoProvider, HttpClient httpClient,
                         @Context UriInfo uriInfo, ListenerRequestRepository listenerRequestRepository,
                         TokenCompatibility tokenCompatibility, DriveFacadeOdata driveFacade) {

        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.httpClient = httpClient;
        this.uriInfo = uriInfo;
        this.listenerRequestRepository = listenerRequestRepository;
        this.tokenCompatibility = tokenCompatibility;
        this.driveFacade = driveFacade;
    }

    @Override
    public void create(ServiceConfiguration configuration, Drive drive, ListenerData listenerData) {
        HttpPost httpPost = new HttpPost(GRAPH_ENDPOINT_V1 + "/subscriptions");

        try {
            tokenCompatibility.addinTokenNotSupported(configuration, "search drive");
            Drive rootDrive = driveFacade.fetchDrive(tokenCompatibility.getToken(configuration), "me/drive");

            if (drive != null && rootDrive.getId().equals(drive.getId()) == false) {
                throw new RuntimeException("This listener only support changes to items in \"me/drive\"");
            }

            // we will listen to changes into items in me/drive, to indicate that we want to listen to the items we add
            // the word root, currently there are a few webhooks supported.
            String resource = "me/drive/root";

            // the api only supports https, the replace is for working in local
            String callback = (uriInfo.getBaseUri().toString() + "callback/response")
                    .replace("http://", "https://");

            int MAX_SUPPORTED_API_FOR_DRIVE = 43200;
            String expirationTime =  OffsetDateTime.now().plusMinutes(MAX_SUPPORTED_API_FOR_DRIVE).toString();

            String body = String.format("{\"changeType\": \"%s\",\"notificationUrl\": \"%s\",\"resource\": \"%s\"," +
                            "\"expirationDateTime\": \"%s\",\"clientState\": \"%s\"}", "updated", callback,
                                resource, expirationTime, listenerData.getRequest().getStateId());

            httpPost.setEntity(new StringEntity(body));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", String.format("Bearer %s", authenticatedWhoProvider.get().getToken()));
            httpClient.executeRequest(httpPost);

            listenerRequestRepository.saveServiceRequestCredentials(listenerData.getRequest().getStateId(), authenticatedWhoProvider.get());
            listenerRequestRepository.saveServiceRequest(listenerData.getRequest());

        } catch (IOException e) {
            throw new RuntimeException("Error deserialization of the create subscription", e);
        }
    }

    @Override
    public void createMultiple(ServiceConfiguration configuration, List<Drive> values, ListenerData listenerData) {
        throw new RuntimeException("create Listener for multiple drives is not supported");
    }
}
