package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.elements.type.ListFilterWhere;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointFacade;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class SiteManager {
    private PropertyCollectionParser propertyParser;
    private SharePointFacade sharePointFacade;

    @Inject
    public SiteManager(PropertyCollectionParser propertyParser, SharePointFacade sharePointFacade) {
        this.propertyParser = propertyParser;
        this.sharePointFacade = sharePointFacade;
    }

    public ObjectDataResponse loadSites(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return sharePointFacade.fetchSite(authenticatedWho.getToken(), objectDataRequest.getListFilter().getId());
        }

        Optional<ListFilterWhere> parentId;

        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {
            parentId  = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Parent ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (parentId.isPresent()) {
                return sharePointFacade.fetchSites(authenticatedWho.getToken(), parentId.get().getContentValue());
            }
        }

        return sharePointFacade.fetchSites(authenticatedWho.getToken());
    }
}
