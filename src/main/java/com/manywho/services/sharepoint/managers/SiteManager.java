package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.manywho.services.sharepoint.types.Site;
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

    public Site loadSite(AuthenticatedWho authenticatedWho, ServiceConfiguration configuration,
                                  ObjectDataType objectDataRequest, String id) throws Exception {

        return sharePointFacade.fetchSite(configuration, authenticatedWho.getToken(), id);
    }

    public List<Site> loadSites(AuthenticatedWho authenticatedWho, ServiceConfiguration configuration, ListFilter filter) {

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
