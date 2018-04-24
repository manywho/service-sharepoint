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
            return dynamicTypesServiceClient.createTypeList(configuration, tokenManager.getToken(configuration), object);
        }

        return dynamicTypesClient
                .createTypeList(tokenManager.getToken(configuration), object);
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Create a list of objects is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        String id = IdExtractorForDynamicTypes.extractItemId(object.getExternalId(), object.getDeveloperName());
        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());

        if (tokenManager.shouldUseServices(configuration)) {
            dynamicTypesServiceClient.deleteTypeList(configuration, tokenManager.getToken(configuration), resourceMetadata, id);
        } else {
            dynamicTypesClient.deleteTypeList(tokenManager.getToken(configuration), resourceMetadata, id);
        }
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Delete a list of objects is not currently supported");
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(objectDataType.getDeveloperName());

        String itemId = IdExtractorForDynamicTypes.extractItemId(id, objectDataType.getDeveloperName());

        if (tokenManager.shouldUseServices(configuration)) {
            return dynamicTypesServiceClient.fetchTypeFromList(configuration,tokenManager.getToken(configuration),
                    resourceMetadata, objectDataType.getProperties(), itemId);
        }

        return dynamicTypesClient.fetchTypeFromList(tokenManager.getToken(configuration),
                        resourceMetadata, itemId,  objectDataType.getProperties());
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(objectDataType.getDeveloperName());

        if (tokenManager.shouldUseServices(configuration)) {
            return dynamicTypesServiceClient.fetchTypesFromLists(configuration, tokenManager.getToken(configuration),
                    resourceMetadata, objectDataType.getProperties(), filter);
        }

        return dynamicTypesClient.fetchTypesFromLists(tokenManager.getToken(configuration), resourceMetadata,
                objectDataType.getProperties(), filter);
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        String itemId = IdExtractorForDynamicTypes.extractItemId(object.getExternalId(), object.getDeveloperName());

        if (tokenManager.shouldUseServices(configuration)) {
            return dynamicTypesServiceClient.updateTypeList(configuration, tokenManager.getToken(configuration), object);
        }

        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());
        return dynamicTypesClient.updateTypeList(tokenManager.getToken(configuration), resourceMetadata,
                        object.getProperties(), itemId);
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        throw new RuntimeException("Update a list of objects is not currently supported");
    }
}