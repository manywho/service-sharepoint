package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.managers.TypeItemManager;

import java.util.List;

public class DatabaseDynamicTypes implements RawDatabase<ServiceConfiguration> {

    private TypeItemManager typeItemManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public DatabaseDynamicTypes(TypeItemManager typeItemManager, AuthenticatedWhoProvider authenticatedWhoProvider)
    {
        this.typeItemManager = typeItemManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
        return typeItemManager.createTypeItem(authenticatedWhoProvider.get(), configuration, object);
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        try{

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        //todo delete list of object;

        return;
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
        return typeItemManager.loadTypeItem(authenticatedWhoProvider.get(), configuration, objectDataType, id);

    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        return typeItemManager.loadTypeItems(authenticatedWhoProvider.get(), configuration, objectDataType, filter);
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        return typeItemManager.updateTypeItem(authenticatedWhoProvider.get(), configuration, object);
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }
}