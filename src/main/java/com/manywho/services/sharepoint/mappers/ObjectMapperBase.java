package com.manywho.services.sharepoint.mappers;

import com.manywho.sdk.services.types.Type;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectMapperBase<T extends Type>{
    public abstract T getObject(ClientEntity itemEntity);

    public List<T> getObjects(List<ClientEntity> items) {

        List<T> objects = new ArrayList<>();
        items.forEach(element -> objects.add(getObject(element)));

        return objects;
    }
}
