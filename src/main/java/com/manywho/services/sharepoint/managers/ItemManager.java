package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.elements.type.ListFilterWhere;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class ItemManager {
    private PropertyCollectionParser propertyParser;
    private SharePointOdataFacade sharePointFacade;

    @Inject
    public ItemManager(PropertyCollectionParser propertyParser, SharePointOdataFacade sharePointFacade) {
        this.propertyParser = propertyParser;
        this.sharePointFacade = sharePointFacade;
    }

    public ObjectDataResponse loadItems(AuthenticatedWho authenticatedWho, ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        Optional<ListFilterWhere> itemOptional = Optional.empty();
        Optional<ListFilterWhere> siteOptional = Optional.empty();
        Optional<ListFilterWhere> listOptional = Optional.empty();

        if (objectDataRequest.getListFilter() != null) {

            if (objectDataRequest.getListFilter().getId() != null
                    && !StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
                String[] parts = objectDataRequest.getListFilter().getId().split("#");

                if(parts.length != 3) {
                    throw new RuntimeException(String.format("The ID for this Item (%s) is not a valid.",
                            objectDataRequest.getListFilter().getId()));
                }

                return sharePointFacade.fetchItem(configuration, authenticatedWho.getToken(), parts[0], parts[1],parts[2]);
            }

            listOptional  = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "List ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            siteOptional  = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            itemOptional  = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();
        }

        if (!siteOptional.isPresent()) {
            throw new RuntimeException("Site ID is mandatory");
        }

        if (!listOptional.isPresent()) {
            throw new RuntimeException("List ID is mandatory");
        }

        if( itemOptional.isPresent()) {
            return sharePointFacade.fetchItem(configuration, authenticatedWho.getToken(), siteOptional.get().getContentValue(),
                    listOptional.get().getContentValue(),  itemOptional.get().getContentValue());
        }

        return sharePointFacade.fetchItems(configuration, authenticatedWho.getToken(),siteOptional.get().getContentValue(),
                listOptional.get().getContentValue());
    }

}
