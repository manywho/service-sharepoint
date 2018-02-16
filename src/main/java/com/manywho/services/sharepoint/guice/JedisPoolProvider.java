package com.manywho.services.sharepoint.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.manywho.services.sharepoint.AppConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolProvider implements Provider<JedisPool> {

    private AppConfiguration serviceConfigurationDefault;

    @Inject
    public JedisPoolProvider(AppConfiguration serviceConfigurationDefault) {
        this.serviceConfigurationDefault = serviceConfigurationDefault;
    }

    @Override
    public JedisPool get() {

        JedisPool pool = new JedisPool(serviceConfigurationDefault.getRedisUrl());

        pool.addObjects(JedisPoolConfig.DEFAULT_MAX_IDLE);

        return pool;
    }
}
