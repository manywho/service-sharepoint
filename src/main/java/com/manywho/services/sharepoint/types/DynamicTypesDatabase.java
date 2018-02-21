package com.manywho.services.sharepoint.types;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;

import java.util.List;

public class DynamicTypesDatabase implements RawDatabase<ServiceConfiguration> {

    private TokenManager tokenManager;
    private DynamicTypesOdataClient dynamicTypesClient;
    private DynamicTypesServiceClient dynamicTypesServiceClient;

    @Inject
    public DynamicTypesDatabase(TokenManager tokenManager, DynamicTypesOdataClient dynamicTypesClient,
                                DynamicTypesServiceClient dynamicTypesServiceClient)
    {
        this.tokenManager = tokenManager;
        this.dynamicTypesClient = dynamicTypesClient;
        this.dynamicTypesServiceClient = dynamicTypesServiceClient;
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {
        if (tokenManager.shouldUseServices(configuration)) {
            throw new RuntimeException("Insert new Types of objects is not currently supported for add-in tokens");
        }

        return dynamicTypesClient
                .createTypeList(tokenManager.getToken(configuration), object.getDeveloperName(), object.getProperties());
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Create a list of objects is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        if (tokenManager.shouldUseServices(configuration)) {
            throw new RuntimeException("Delete Types is not currently supported for add-in tokens");
        }

        String id = IdExtractorForDynamicTypes.extractItemId(object.getExternalId());

        dynamicTypesClient.deleteTypeList(tokenManager.getToken(configuration), object.getDeveloperName(), id);
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Delete a objects is not currently supported");
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
        if (tokenManager.shouldUseServices(configuration)) {
            throw new RuntimeException("Load types from list is not implemented for apps");
        }

        return dynamicTypesClient.fetchTypeFromList(tokenManager.getToken(configuration),
                        objectDataType.getDeveloperName(), id,  objectDataType.getProperties());
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        if (tokenManager.shouldUseServices(configuration)) {
            return dynamicTypesServiceClient.fetchTypesFromLists(configuration, tokenManager.getToken(configuration),
                    objectDataType.getDeveloperName(), objectDataType.getProperties(), filter);
        }

        return dynamicTypesClient.fetchTypesFromLists(tokenManager.getToken(configuration),
                        objectDataType.getDeveloperName(), objectDataType.getProperties(), filter);
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        String itemId = IdExtractorForDynamicTypes.extractItemId(object.getExternalId());

        if (tokenManager.shouldUseServices(configuration)) {
            throw new RuntimeException("Update a type is not currently supported when using add-in");
        }


        return dynamicTypesClient.updateTypeList(tokenManager.getToken(configuration), object.getDeveloperName(),
                        object.getProperties(), itemId);
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Update a list of objects is not currently supported");
    }
}