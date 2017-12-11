package com.manywho.services.sharepoint.configuration;


public interface ServiceConfiguration {
    String get(String key);
    boolean has(String key);
}
