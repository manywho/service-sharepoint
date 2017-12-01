package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.facades.SharepointFacadeFactory;
import com.manywho.services.sharepoint.types.SharePointList;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListManager {
    private SharepointFacadeFactory sharepointFacadeFactory;

    @Inject
    public ListManager(SharepointFacadeFactory sharepointFacadeFactory) {
        this.sharepointFacadeFactory = sharepointFacadeFactory;
    }

    public SharePointList loadList(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                   String id) {

            String[] parts = id.split("#");
            if (parts.length <2) {
                throw new RuntimeException(String.format("the external id %s is wrong", id));
            }

            return sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider())
                    .fetchList(configuration, authenticatedWho.getToken(), parts[0], parts[1]);
    }


    public List<SharePointList> loadLists(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration, ListFilter filter) {

        if (filter != null && filter.getWhere() != null) {
            Optional<ListFilterWhere> siteId  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (siteId.isPresent()) {
                return  sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider()).fetchLists(configuration, authenticatedWho.getToken(), siteId.get().getContentValue(), false);
            }
        }

        return  sharepointFacadeFactory.get(authenticatedWho.getIdentityProvider()).fetchListsRoot(configuration, authenticatedWho.getToken());
    }

}
