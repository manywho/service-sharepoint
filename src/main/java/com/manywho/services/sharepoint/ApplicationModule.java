package com.manywho.services.sharepoint;

import com.google.inject.AbstractModule;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.types.TypeProviderRaw;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(TypeProviderRaw.class);
    }
}
