package com.manywho.services.sharepoint.files.listeners.entities;

import com.manywho.sdk.api.run.elements.map.OutcomeAvailable;
import com.manywho.sdk.api.security.AuthenticatedWho;

import java.util.List;
import java.util.UUID;

public class ListenerBasicRequest {
    private UUID tenantId;
    private String token;
    private UUID stateId;
    private AuthenticatedWho authenticatedWho;
    private List<OutcomeAvailable> outcomeAvailables;

    public ListenerBasicRequest() {
    }

    public ListenerBasicRequest(UUID tenantId, String token, UUID stateId, List<OutcomeAvailable> outcomeAvailables,
                                AuthenticatedWho authenticatedWho) {
        this.tenantId = tenantId;
        this.token = token;
        this.stateId = stateId;
        this.outcomeAvailables = outcomeAvailables;
        this.authenticatedWho = authenticatedWho;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getToken() {
        return token;
    }

    public UUID getStateId() {
        return stateId;
    }

    public List<OutcomeAvailable> getOutcomeAvailables() {
        return outcomeAvailables;
    }

    public AuthenticatedWho getAuthenticatedWho() {
        return authenticatedWho;
    }
}
