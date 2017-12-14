package com.manywho.services.sharepoint.configuration.reader;

import java.util.Map;

public class ServiceConfigurationEnvironmentVariables implements ServiceConfigurationInterface {
    protected Map<String, String> environmentVariables;

    public ServiceConfigurationEnvironmentVariables() {
        environmentVariables = System.getenv();
    }

    @Override
    public String get(String key) {
        return environmentVariables.get(key);
    }

    @Override
    public boolean has(String key) {
        return environmentVariables.containsKey(key);
    }
}