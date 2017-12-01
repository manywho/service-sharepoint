package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.managers.ItemManager;
import com.manywho.services.sharepoint.managers.ListManager;
import com.manywho.services.sharepoint.managers.SiteManager;
import com.manywho.services.sharepoint.managers.TypeItemManager;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;

import java.util.List;

public class DatabaseDynamicTypes implements RawDatabase<ApplicationConfiguration> {

    private SiteManager siteManager;

    private ItemManager itemManager;
    private TypeItemManager typeItemManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public DatabaseDynamicTypes(SiteManager siteManager, ItemManager itemManager,
                                TypeItemManager typeItemManager, AuthenticatedWhoProvider authenticatedWhoProvider)
    {
        this.siteManager = siteManager;
        this.itemManager = itemManager;
        this.typeItemManager = typeItemManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public MObject create(ApplicationConfiguration configuration, MObject object) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public List<MObject> create(ApplicationConfiguration configuration, List<MObject> objects) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, MObject object) {
        try{

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<MObject> objects) {
        //todo delete list of object;

        return;
    }

    @Override
    public MObject find(ApplicationConfiguration configuration, ObjectDataType objectDataType, String id) {
        try {
            switch (objectDataType.getDeveloperName()) {
                case Site.NAME:
                    return siteManager.loadSite(authenticatedWhoProvider.get(), configuration, objectDataType, id);
                case Item.NAME:
                    return itemManager.loadItem(authenticatedWhoProvider.get(), configuration, objectDataType, id);
                default:
                    return typeItemManager.loadTypeItem(authenticatedWhoProvider.get(), configuration, objectDataType, id);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MObject> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {

        try {
            switch (objectDataType.getDeveloperName()) {
                case Site.NAME:
                    return siteManager.loadSites(authenticatedWhoProvider.get(), configuration, objectDataType, filter);
                case Item.NAME:
                    return itemManager.loadItems(authenticatedWhoProvider.get(), configuration, objectDataType, filter);
                default:
                    return typeItemManager.loadTypeItems(authenticatedWhoProvider.get(), configuration, objectDataType, filter);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MObject update(ApplicationConfiguration configuration, MObject object) {

        try {
            if (Site.NAME.equals(object.getDeveloperName()) || SharePointList.NAME.equals(object.getDeveloperName()) ||
                    Item.NAME.equals(object.getDeveloperName())) {

                throw new RuntimeException(String.format("Type \"%s\" not supported", object.getDeveloperName()));
            }

            return typeItemManager.updateTypeItem(authenticatedWhoProvider.get(), configuration, object);

        } catch (Exception e) {
            throw new RuntimeException("Problem updating object " + e.getMessage());
        }
    }

    @Override
    public List<MObject> update(ApplicationConfiguration configuration, List<MObject> objects) {
        return null;
    }
}