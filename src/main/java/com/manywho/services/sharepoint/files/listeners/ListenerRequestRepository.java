package com.manywho.services.sharepoint.files.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

public class ListenerRequestRepository {

    private JedisPool jedisPool;
    private ObjectMapper objectMapper;
    private final static String REDIS_KEY_SERVICE_REQUEST             = "service:sharepoint:listener:state:%s:request";
    private final static String REDIS_KEY_SERVICE_REQUEST_CREDENTIALS = "service:sharepoint:listener:state:%s:credentials";


    @Inject
    public ListenerRequestRepository(JedisPool jedisPool, ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    public void saveServiceRequest(ListenerServiceRequest request) {
        try(Jedis jedis = jedisPool.getResource()) {

            String value = objectMapper.writeValueAsString(request);
            jedis.set(String.format(REDIS_KEY_SERVICE_REQUEST, request.getStateId()), value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("There was an error while serializing the request for caching", e);
        }
    }

    public ListenerServiceRequest getServiceRequest(String stateId) {
        try(Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_SERVICE_REQUEST, stateId));

            if (Strings.isNullOrEmpty(json) == false) {
                try {
                    return objectMapper.readValue(json, ListenerServiceRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException("Listener service request not found");
    }

    public void saveServiceRequestCredentials(UUID stateId, AuthenticatedWho authenticatedWho) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_SERVICE_REQUEST_CREDENTIALS, stateId), objectMapper.writeValueAsString(authenticatedWho));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("There was an error while serializing the credentials", e);
        }
    }

    public AuthenticatedWho getServiceRequestCredentials(String stateId) {
        try(Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_SERVICE_REQUEST_CREDENTIALS, stateId));

            if (Strings.isNullOrEmpty(json) == false) {
                try {
                    return objectMapper.readValue(json, AuthenticatedWho.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException("Listener service request credentials not found");
    }
}