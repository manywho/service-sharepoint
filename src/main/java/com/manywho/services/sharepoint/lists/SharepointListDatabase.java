package com.manywho.services.sharepoint.lists;

import com.google.inject.Inject;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SharepointListDatabase implements Database<ServiceConfiguration, SharePointList> {
    private TokenManager tokenManager;
    private SharePointListClient sharePointListClient;

    @Inject
    public SharepointListDatabase(TokenManager tokenManager, SharePointListClient sharePointListClient) {
        this.tokenManager = tokenManager;
        this.sharePointListClient = sharePointListClient;
    }

    @Override
    public SharePointList find(ServiceConfiguration configuration, String id) {

        String idSite = IdExtractorForLists.extractSiteId(id);
        String idList = IdExtractorForLists.extractListId(id);

        return sharePointListClient
                .fetchList(tokenManager.getToken(configuration), idSite, idList);
    }

    @Override
    public List<SharePointList> findAll(ServiceConfiguration configuration, ListFilter listFilter) {
        String token = tokenManager.getToken(configuration);

        //ToDo in future versions of engine the list filter will be provider with empty list and this check will not be needed
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        if (listFilter.getWhere() != null) {
            Optional<ListFilterWhere> pathSiteId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (pathSiteId.isPresent()) {
                String siteId = pathSiteId.get().getContentValue().replace("sites/", "");

                return  sharePointListClient.fetchLists(token, siteId, false, listFilter);
            }
        }

        return  sharePointListClient.fetchListsRoot(token, listFilter);
    }

    @Override
    public SharePointList create(ServiceConfiguration configuration, SharePointList sharePointList) {
        throw new RuntimeException("Create a list is not currently supported");
    }

    @Override
    public List<SharePointList> create(ServiceConfiguration configuration, List<SharePointList> list) {
        throw new RuntimeException("Create a list of lists is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, SharePointList sharePointList) {
        throw new RuntimeException("Delete a list is not currently supported");
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<SharePointList> list) {
        throw new RuntimeException("Delete a list of lists is not currently supported");
    }

    @Override
    public SharePointList update(ServiceConfiguration configuration, SharePointList sharePointList) {
        throw new RuntimeException("Update a list is not currently supported");
    }

    @Override
    public List<SharePointList> update(ServiceConfiguration configuration, List<SharePointList> list) {
        throw new RuntimeException("Update a list of lists is not currently supported");
    }
}
