package com.manywho.services.sharepoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.mapper.MapperProvider;
import com.manywho.services.sharepoint.types.TypeProviderRaw;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(TypeProviderRaw.class);
        bind(ObjectMapper.class).toProvider(MapperProvider.class).in(Singleton.class);
    }
}
