package com.manywho.services.sharepoint.guice;

import com.google.common.base.Strings;
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
        int port = 6379;

        if (Strings.isNullOrEmpty(serviceConfigurationDefault.getRedisPort()) == false) {
            port = Integer.parseInt(serviceConfigurationDefault.getRedisPort());
        }

        JedisPool pool = new JedisPool(new JedisPoolConfig(), serviceConfigurationDefault.getRedisUrl(), port, 2000);

        pool.addObjects(JedisPoolConfig.DEFAULT_MAX_IDLE);

        return pool;
    }
}
