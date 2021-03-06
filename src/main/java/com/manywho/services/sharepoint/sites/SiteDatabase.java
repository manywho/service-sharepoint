package com.manywho.services.sharepoint.sites;

import com.google.inject.Inject;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.Database;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.auth.TokenManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SiteDatabase implements Database<ServiceConfiguration, Site> {
    private TokenManager tokenManager;
    private SiteClient siteClient;

    @Inject
    public SiteDatabase(TokenManager tokenManager, SiteClient siteClient) {
        this.tokenManager = tokenManager;
        this.siteClient = siteClient;
    }

    @Override
    public Site find(ServiceConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {
        SiteMapper siteMapper = new SiteMapper(null, null);
        tokenManager.addinTokenNotSupported(configuration, "search drive");

        return siteClient.fetchSite(tokenManager.getToken(configuration), id);
    }

    @Override
    public List<Site> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter listFilter, List<MObject> objects) {
        String token = tokenManager.getToken(configuration);
        //ToDo in future versions of engine the list filter will be provider with empty list and this check will not be needed
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        if (listFilter.getWhere() != null) {
            Optional<ListFilterWhere> parentId  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            Optional<ListFilterWhere> group  = listFilter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Group ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

                if (parentId.isPresent() && group.isPresent()) {
                throw new RuntimeException("Filter by Parent ID and Group ID is not supported");
            }

            if (parentId.isPresent()) {
                return siteClient.fetchSubsites(token, String.format("sites/%s", parentId.get().getContentValue()));
            } else if (group.isPresent()) {
                String groupId = group.get().getContentValue().replace("groups/","");
                return siteClient.fetchSites(token, groupId);
            }
        }

        return siteClient.fetchSites(token, null);
    }

    @Override
    public Site create(ServiceConfiguration configuration, ObjectDataType objectDataType, Site site) {
        return null;
    }

    @Override
    public List<Site> create(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Site> list) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, ObjectDataType objectDataType, Site site) {

    }

    @Override
    public void delete(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Site> list) {

    }

    @Override
    public Site update(ServiceConfiguration configuration, ObjectDataType objectDataType, Site site) {
        return null;
    }

    @Override
    public List<Site> update(ServiceConfiguration configuration, ObjectDataType objectDataType, List<Site> list) {
        return null;
    }
}
