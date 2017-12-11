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

public class ItemManager {
    private SharePointOdataFacade sharePointFacade;

    @Inject
    public ItemManager(SharePointOdataFacade sharePointFacade) {
        this.sharePointFacade = sharePointFacade;
    }

    public MObject loadItem(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                   ObjectDataType objectDataRequest, String id) throws Exception {
        // get item id
        String itemId = id;
        // get site id
        String siteId = id;
        // get list id
        String listId = id;

        return sharePointFacade.fetchItem(configuration, authenticatedWho.getToken(), siteId, listId, itemId);
    }


    public List<MObject> loadItems(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                   ObjectDataType objectDataRequest, ListFilter filter) throws Exception {

        Optional<ListFilterWhere> itemOptional = Optional.empty();
        Optional<ListFilterWhere> siteOptional = Optional.empty();
        Optional<ListFilterWhere> listOptional = Optional.empty();

        if (filter!= null) {

            listOptional  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "List ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            siteOptional  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "Site ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();

            itemOptional  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();
        }

        if (!siteOptional.isPresent()) {
            throw new RuntimeException("Site ID is mandatory");
        }

        if (!listOptional.isPresent()) {
            throw new RuntimeException("List ID is mandatory");
        }

        return sharePointFacade.fetchItems(configuration, authenticatedWho.getToken(), siteOptional.get().getContentValue(),
                listOptional.get().getContentValue());
    }

}
