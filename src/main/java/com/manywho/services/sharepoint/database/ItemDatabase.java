package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.database.items.ItemManager;
import com.manywho.services.sharepoint.types.Item;

import java.util.List;

public class ItemDatabase implements Database<ServiceConfiguration, Item> {

    private ItemManager itemManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public ItemDatabase(ItemManager itemManager, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.itemManager = itemManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public Item find(ServiceConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Item> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        return itemManager.loadItems(authenticatedWhoProvider.get(), configuration, listFilter);
    }

    @Override
    public Item create(ServiceConfiguration configuration, Item item) {
        return null;
    }

    @Override
    public List<Item> create(ServiceConfiguration configuration, List<Item> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, Item item) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Item> list) {

    }

    @Override
    public Item update(ServiceConfiguration configuration, Item item) {
        return null;
    }

    @Override
    public List<Item> update(ServiceConfiguration configuration, List<Item> list) {
        return null;
    }
}
