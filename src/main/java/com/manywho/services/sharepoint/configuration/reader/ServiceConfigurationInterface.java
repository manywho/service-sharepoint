package com.manywho.services.sharepoint.configuration.reader;


public interface ServiceConfigurationInterface {
    String get(String key);
    boolean has(String key);
}
