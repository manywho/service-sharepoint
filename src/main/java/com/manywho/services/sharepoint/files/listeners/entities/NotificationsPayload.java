package com.manywho.services.sharepoint.files.listeners.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NotificationsPayload {

    @JsonProperty("value")
    private List<Notification> notifications;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
