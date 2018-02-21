package com.manywho.services.sharepoint.lists.items;

import com.manywho.services.sharepoint.mappers.ObjectMapperBase;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.time.OffsetDateTime;

public class SharePointListItemMapper extends ObjectMapperBase<SharePointListItem> {
    private String siteId;
    private String listId;

    public SharePointListItemMapper(String siteId, String listId) {
        this.siteId = siteId;
        this.listId = listId;
    }

    public SharePointListItem getObject(ClientEntity itemEntity) {

            SharePointListItem sharePointListItem = new SharePointListItem();

            sharePointListItem.setCreatedDateTime(OffsetDateTime.parse(itemEntity.getProperty("createdDateTime").getValue().toString()));
            sharePointListItem.setModifiedDateTime(OffsetDateTime.parse(itemEntity.getProperty("lastModifiedDateTime").getValue().toString()));
            sharePointListItem.setWebUrl(itemEntity.getProperty("webUrl").getValue().toString());
            sharePointListItem.setSiteId(String.format("sites/%s", siteId));
            sharePointListItem.setListId(String.format("sites/%s/lists/%s", siteId, listId));
            sharePointListItem.setId(String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemEntity.getProperty("id").getValue().toString()));

            return sharePointListItem;
    }
}
