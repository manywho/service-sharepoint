package com.manywho.services.sharepoint.files.listeners.entities;

import java.time.OffsetDateTime;

public class Notification {
    private String subscriptionId;
    private OffsetDateTime subscriptionExpirationDateTime;
    private String clientState;
    private String changeType;
    private String resource;
    private ResourceData resourceData;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public OffsetDateTime getSubscriptionExpirationDateTime() {
        return subscriptionExpirationDateTime;
    }

    public void setSubscriptionExpirationDateTime(String subscriptionExpirationDateTime) {
        this.subscriptionExpirationDateTime = OffsetDateTime.parse(subscriptionExpirationDateTime);
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ResourceData getResourceData() {
        return resourceData;
    }

    public void setResourceData(ResourceData resourceData) {
        this.resourceData = resourceData;
    }
}
