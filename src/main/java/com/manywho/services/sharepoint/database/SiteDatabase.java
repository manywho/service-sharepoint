package com.manywho.services.sharepoint.database;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.TokenCompatibility;
import com.manywho.services.sharepoint.facades.SharePointFacadeInterface;
import com.manywho.services.sharepoint.types.Site;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SiteDatabase implements Database<ServiceConfiguration, Site> {
    private TokenCompatibility tokenCompatibility;

    @Inject
    public SiteDatabase(TokenCompatibility tokenCompatibility) {
        this.tokenCompatibility = tokenCompatibility;
    }

    @Override
    public Site find(ServiceConfiguration configuration, String id) {
        return tokenCompatibility.getSharePointFacade(configuration)
                .fetchSite(configuration, tokenCompatibility.getToken(configuration), id);
    }

    @Override
    public List<Site> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        SharePointFacadeInterface sharePointFacade = tokenCompatibility.getSharePointFacade(configuration);
        String token = tokenCompatibility.getToken(configuration);

        Optional<ListFilterWhere> parentId;

        if (listFilter != null && listFilter.getWhere() != null) {
            parentId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (parentId.isPresent()) {
                return sharePointFacade.fetchSites(configuration, token, parentId.get().getContentValue());
            }
        }

        return sharePointFacade.fetchSites(configuration, token);
    }

    @Override
    public Site create(ServiceConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> create(ServiceConfiguration configuration, List<Site> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, Site site) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, List<Site> list) {

    }

    @Override
    public Site update(ServiceConfiguration configuration, Site site) {
        return null;
    }

    @Override
    public List<Site> update(ServiceConfiguration configuration, List<Site> list) {
        return null;
    }
}
