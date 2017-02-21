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

public class ListManager {
    private PropertyCollectionParser propertyParser;
    private SharePointFacade sharePointFacade;

    @Inject
    public ListManager(PropertyCollectionParser propertyParser, SharePointFacade sharePointFacade) {
        this.propertyParser = propertyParser;
        this.sharePointFacade = sharePointFacade;
    }

    public ObjectDataResponse loadLists(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            String[] parts = objectDataRequest.getListFilter().getId().split("#");
            if (parts.length <2) {
                throw new RuntimeException(String.format("the external id %s is wrong", objectDataRequest.getListFilter().getId()));
            }
            return sharePointFacade.fetchList(authenticatedWho.getToken(), parts[0], parts[1]);
        }

        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {
            Optional<ListFilterWhere> siteId  = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            if (siteId.isPresent()) {
                return sharePointFacade.fetchLists(authenticatedWho.getToken(), siteId.get().getContentValue());
            }
        }

        return sharePointFacade.fetchListsRoot(authenticatedWho.getToken());
    }

}
