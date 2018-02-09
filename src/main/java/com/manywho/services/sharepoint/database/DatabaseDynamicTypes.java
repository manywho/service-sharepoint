package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.utilities.IdExtractorForDynamicTypes;

import java.util.List;

public class DatabaseDynamicTypes implements RawDatabase<ServiceConfiguration> {

    private TokenCompatibility tokenCompatibility;

    @Inject
    public DatabaseDynamicTypes(TokenCompatibility tokenCompatibility)
    {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {

        return tokenCompatibility.getSharePointFacade(configuration)
                .createTypeList(configuration, tokenCompatibility.getToken(configuration), object.getDeveloperName(), object.getProperties());
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        String id = IdExtractorForDynamicTypes.extractItemId(object.getExternalId());

        tokenCompatibility.getSharePointFacade(configuration)
                .deleteTypeList(configuration, tokenCompatibility.getToken(configuration), object.getDeveloperName(), id);
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        //todo delete list of object;

        return;
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {

        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchTypeFromList(configuration, tokenCompatibility.getToken(configuration),
                        objectDataType.getDeveloperName(), id,  objectDataType.getProperties());
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchTypesFromLists(configuration, tokenCompatibility.getToken(configuration),
                        objectDataType.getDeveloperName(), objectDataType.getProperties(), filter);
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        String itemId = IdExtractorForDynamicTypes.extractItemId(object.getExternalId());
        return tokenCompatibility.getSharePointFacade(configuration)
                .updateTypeList(configuration, tokenCompatibility.getToken(configuration), object.getDeveloperName(),
                        object.getProperties(), itemId);
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }
}