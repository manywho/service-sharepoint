package com.manywho.services.sharepoint.guice;

import com.google.inject.Provider;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;


public class ODataClientProvider implements Provider<ODataClient> {
    @Override
    public ODataClient get() {
        return ODataClientFactory.getClient();
    }
}
