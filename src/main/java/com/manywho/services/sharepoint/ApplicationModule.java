package com.manywho.services.sharepoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.guice.JedisPoolProvider;
import com.manywho.services.sharepoint.guice.ObjectMapperProvider;
import com.manywho.services.sharepoint.guice.RunClientProvider;
import com.manywho.services.sharepoint.types.TypeProviderRaw;
import redis.clients.jedis.JedisPool;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(TypeProviderRaw.class);
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        bind(RunClient.class).toProvider(RunClientProvider.class).in(Singleton.class);
        bind(JedisPool.class).toProvider(JedisPoolProvider.class).in(Singleton.class);
    }
}
