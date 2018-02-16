package com.manywho.services.sharepoint.files.listeners;

import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.files.listeners.client.EngineClient;
import com.manywho.services.sharepoint.files.listeners.client.NotificationsClient;
import com.manywho.services.sharepoint.files.listeners.entities.Notification;
import com.manywho.services.sharepoint.files.listeners.entities.NotificationsPayload;
import com.manywho.services.sharepoint.files.listeners.persistence.ListenerRequestRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/callback")
public class CallbackListener {

    private EngineClient engineClient;
    private ListenerRequestRepository listenerRequestRepository;
    private NotificationsClient notificationsClient;

    @Inject
    public CallbackListener(EngineClient engineClient, ListenerRequestRepository listenerRequestRepository,
                            NotificationsClient notificationsClient) {

        this.engineClient = engineClient;
        this.listenerRequestRepository = listenerRequestRepository;
        this.notificationsClient = notificationsClient;
    }

    @POST
    @Path("/response")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public String receivedDecisionResponse(NotificationsPayload notificationPayload) {
        try{
            notificationPayload.getNotifications()
                    .forEach(notification -> processNotification(notification));

            return "Done";
        } catch (Exception e) {
            throw new RuntimeException("the notification can not be processed", e);
        }
    }

    @POST
    @Path("/response")
    @Produces(MediaType.TEXT_HTML)
    public String receivedDecisionResponse(@QueryParam("validationToken") String code) {
        return code;
    }

    private void processNotification(Notification notification) {
        try {
            AuthenticatedWho authenticatedWho = listenerRequestRepository.getServiceRequestCredentials(notification.getClientState());
            engineClient.sendResponseToEngine(
                    listenerRequestRepository.getServiceRequest(notification.getClientState()),
                    authenticatedWho
            );
           // notificationsClient.removeWebhook(notification.getSubscriptionId(), authenticatedWho.getToken());
        } catch (Exception e) {
            // if one of the notifications fails we continue with the others
        }
    }

}
