package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.facades.SharePointOdataFacade;
import com.manywho.services.sharepoint.types.Item;
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

    public Item loadItem(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                   ObjectDataType objectDataRequest, String id) throws Exception {
        // get item id
        String itemId = id;
        // get site id
        String siteId = id;
        // get list id
        String listId = id;

        return sharePointFacade.fetchItem(configuration, authenticatedWho.getToken(), siteId, listId, itemId);
    }


    public List<Item> loadItems(AuthenticatedWho authenticatedWho, ApplicationConfiguration configuration,
                                ListFilter filter) {

        Optional<ListFilterWhere> itemOptional = Optional.empty();
        Optional<ListFilterWhere> siteOptional = Optional.empty();
        Optional<ListFilterWhere> listOptional = Optional.empty();

        if (filter!= null) {

            listOptional  = filter.getWhere().stream()
                    .filter(p -> Objects.equals(p.getColumnName(), "List ID") && !StringUtils.isEmpty(p.getContentValue()))
                    .findFirst();
        }

        if (!listOptional.isPresent()) {
            throw new RuntimeException("List ID is mandatory");
        }

        return sharePointFacade.fetchItems(configuration, authenticatedWho.getToken(), listOptional.get().getContentValue());
    }

}
