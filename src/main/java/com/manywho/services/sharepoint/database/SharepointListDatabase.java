package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.types.SharePointList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SharepointListDatabase implements Database<ServiceConfiguration, SharePointList> {
    private TokenCompatibility tokenCompatibility;

    @Inject
    public SharepointListDatabase(TokenCompatibility tokenCompatibility) {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public SharePointList find(ServiceConfiguration configuration, String id) {

        String[] parts = id.split("#");

        if (parts.length <2) {
            throw new RuntimeException(String.format("The external id %s is wrong", id));
        }
        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchList(configuration, tokenCompatibility.getToken(configuration), parts[0], parts[1]);
    }

    @Override
    public List<SharePointList> findAll(ServiceConfiguration configuration, ListFilter listFilter) {

        SharePointFacadeInterface sharePointFacade = tokenCompatibility.getSharePointFacade(configuration);
        String token = tokenCompatibility.getToken(configuration);

        if (listFilter != null && listFilter.getWhere() != null) {
            Optional<ListFilterWhere> siteId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (siteId.isPresent()) {
                return  sharePointFacade
                        .fetchLists(configuration, token, siteId.get().getContentValue(), false);
            }
        }

        return  sharePointFacade.fetchListsRoot(configuration, token);
    }

    @Override
    public SharePointList create(ServiceConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> create(ServiceConfiguration configuration, List<SharePointList> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, SharePointList sharePointList) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<SharePointList> list) {

    }

    @Override
    public SharePointList update(ServiceConfiguration configuration, SharePointList sharePointList) {
        return null;
    }

    @Override
    public List<SharePointList> update(ServiceConfiguration configuration, List<SharePointList> list) {
        return null;
    }
}
