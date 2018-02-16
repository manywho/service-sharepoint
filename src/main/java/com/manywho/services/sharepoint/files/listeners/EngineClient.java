package com.manywho.services.sharepoint.files.listeners;

import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class EngineClient {

    private RunClient runClient;
    private AuthorizationEncoder authorizationEncoder;

    @Inject
    public EngineClient(RunClient runClient, AuthorizationEncoder authorizationEncoder) {
        this.runClient = runClient;
        this.authorizationEncoder = authorizationEncoder;
    }

    public void sendResponseToEngine(ListenerServiceRequest listenerServiceRequest, AuthenticatedWho authenticatedWho) {
        try {

            EngineValue engineValue = listenerServiceRequest.getValueForListening();
            engineValue.setDeveloperName("");
            ServiceResponse serviceResponse = new ServiceResponse(InvokeType.Forward, engineValue, listenerServiceRequest.getToken());
            serviceResponse.setTenantId(listenerServiceRequest.getTenantId());
            serviceResponse.setAnnotations(listenerServiceRequest.getAnnotations());

            Response<InvokeType> response = runClient.callback(authorizationEncoder.encode(authenticatedWho),
                    listenerServiceRequest.getTenantId(), serviceResponse).execute();
            // the engine response that there aren't any action, need to figure out why that response
        } catch (IOException e) {
            throw new RuntimeException("There was an unexpected error when re-joining the flow", e);
        }
    }

}
