package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SiteManager {
    private SharePointOdataFacade sharePointFacade;

    @Inject
    public SiteManager(SharePointOdataFacade sharePointFacade) {
        this.sharePointFacade = sharePointFacade;
    }

    public MObject loadSite(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                  ObjectDataType objectDataRequest, String id) throws Exception {

        return sharePointFacade.fetchSite(configuration, authenticatedWho.getToken(), id);
    }

    public List<MObject> loadSites(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration, ObjectDataType objectDataRequest, ListFilter filter) throws Exception {

        Optional<ListFilterWhere> parentId;

        if (filter!= null && filter.getWhere() != null) {
            parentId  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (parentId.isPresent()) {
                return sharePointFacade.fetchSites(configuration, authenticatedWho.getToken(), parentId.get().getContentValue());
            }
        }

        return sharePointFacade.fetchSites(configuration, authenticatedWho.getToken());
    }
}
