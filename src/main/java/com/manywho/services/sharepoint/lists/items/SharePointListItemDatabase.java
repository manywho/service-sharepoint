package com.manywho.services.sharepoint.lists.items;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;
import com.manywho.services.sharepoint.lists.SharePointListClient;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SharePointListItemDatabase implements Database<ServiceConfiguration, SharePointListItem> {

    private TokenManager tokenManager;
    private SharePointListClient sharePointListClient;

    @Inject
    public SharePointListItemDatabase(TokenManager tokenManager, SharePointListClient client) {
        this.tokenManager = tokenManager;
        this.sharePointListClient = client;
    }

    @Override
    public SharePointListItem find(ServiceConfiguration configuration, String id) {

        return sharePointListClient
                .fetchItem(tokenManager.getToken(configuration),
                        IdExtractorForListItem.extractSiteId(id),
                        IdExtractorForListItem.extractListId(id),
                        IdExtractorForListItem.extractListItemId(id));
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

        return sharePointListClient
                .fetchItems(tokenManager.getToken(configuration), listOptional.get().getContentValue(), listFilter);
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
        sharePointListClient.deleteTypeList(tokenManager.getToken(configuration),
                        IdExtractorForListItem.extractSiteId(sharePointListItem.getId()),
                        IdExtractorForListItem.extractListId(sharePointListItem.getId()),
                        IdExtractorForListItem.extractListItemId(sharePointListItem.getId())
        );
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
