package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.types.SharePointListItem;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemDatabase implements Database<ServiceConfiguration, SharePointListItem> {

    private TokenCompatibility tokenCompatibility;

    @Inject
    public ItemDatabase(TokenCompatibility tokenCompatibility) {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public SharePointListItem find(ServiceConfiguration configuration, String id) {
        // get item id
        String itemId = id;
        // get site id
        String siteId = id;
        // get list id
        String listId = id;

        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchItem(configuration, tokenCompatibility.getToken(configuration), siteId, listId, itemId);
    }

    @Override
    public List<SharePointListItem> findAll(ServiceConfiguration configuration, ListFilter listFilter) {

        Optional<ListFilterWhere> listOptional = Optional.empty();

        if (listFilter!= null) {

            listOptional  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "List ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();
        }

        if (!listOptional.isPresent()) {
            throw new RuntimeException("List ID is mandatory");
        }

        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchItems(configuration, tokenCompatibility.getToken(configuration), listOptional.get().getContentValue());
    }

    @Override
    public SharePointListItem create(ServiceConfiguration configuration, SharePointListItem sharePointListItem) {
        return null;
    }

    @Override
    public List<SharePointListItem> create(ServiceConfiguration configuration, List<SharePointListItem> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, SharePointListItem sharePointListItem) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<SharePointListItem> list) {

    }

    @Override
    public SharePointListItem update(ServiceConfiguration configuration, SharePointListItem sharePointListItem) {
        return null;
    }

    @Override
    public List<SharePointListItem> update(ServiceConfiguration configuration, List<SharePointListItem> list) {
        return null;
    }
}
