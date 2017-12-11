package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.managers.ItemManager;
import com.manywho.services.sharepoint.types.Item;

import java.util.List;

public class ItemDatabase implements Database<ApplicationConfiguration, Item> {

    private ItemManager itemManager;
    private AuthenticatedWhoProvider authenticatedWhoProvider;

    @Inject
    public ItemDatabase(ItemManager itemManager, AuthenticatedWhoProvider authenticatedWhoProvider) {
        this.itemManager = itemManager;
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public Item find(ApplicationConfiguration configuration, String s) {
        return null;
    }

    @Override
    public List<Item> findAll(ApplicationConfiguration configuration, ListFilter listFilter) {
        return itemManager.loadItems(authenticatedWhoProvider.get(), configuration, listFilter);
    }

    @Override
    public Item create(ApplicationConfiguration configuration, Item item) {
        return null;
    }

    @Override
    public List<Item> create(ApplicationConfiguration configuration, List<Item> list) {
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, Item item) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, List<Item> list) {

    }

    @Override
    public Item update(ApplicationConfiguration configuration, Item item) {
        return null;
    }

    @Override
    public List<Item> update(ApplicationConfiguration configuration, List<Item> list) {
        return null;
    }
}
