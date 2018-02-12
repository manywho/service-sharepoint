package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.types.SharePointListItem;
import com.manywho.services.sharepoint.utilities.IdExtractorForDynamicTypes;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListItemDatabase implements Database<ServiceConfiguration, SharePointListItem> {

    private TokenCompatibility tokenCompatibility;

    @Inject
    public ListItemDatabase(TokenCompatibility tokenCompatibility) {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public SharePointListItem find(ServiceConfiguration configuration, String id) {
        // todo those id doesn't look right
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
        throw new RuntimeException("Create is not supported for SharePoint List Item");
    }

    @Override
    public List<SharePointListItem> create(ServiceConfiguration configuration, List<SharePointListItem> list) {
        throw new RuntimeException("Create is not supported for SharePoint List Item");
    }

    @Override
    public void delete(ServiceConfiguration configuration, SharePointListItem sharePointListItem) {
        String id = IdExtractorForDynamicTypes.extractItemId(sharePointListItem.getId());

        tokenCompatibility.getSharePointFacade(configuration)
                .deleteTypeList(configuration, tokenCompatibility.getToken(configuration), sharePointListItem.getId(), id);
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<SharePointListItem> list) {
        throw new RuntimeException("Delete list of SharePoint List Item is not supported");
    }

    @Override
    public SharePointListItem update(ServiceConfiguration configuration, SharePointListItem sharePointListItem) {
        throw new RuntimeException("Update is not supported for SharePoint List Item");
    }

    @Override
    public List<SharePointListItem> update(ServiceConfiguration configuration, List<SharePointListItem> list) {
        throw new RuntimeException("Update list of SharePoint List Item is not supported");
    }
}
