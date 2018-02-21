package com.manywho.services.sharepoint.lists;

import com.google.common.base.Strings;
import com.manywho.services.sharepoint.mappers.ObjectMapperBase;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.time.OffsetDateTime;

public class SharePointListMapper extends ObjectMapperBase<SharePointList> {
    private String siteId;

    public SharePointListMapper(String siteId) {
        this.siteId = siteId;
    }

    public SharePointList getObject(ClientEntity sharepointListEntity) {
        SharePointList sharePointList = new SharePointList();
        sharePointList.setCreatedDateTime(OffsetDateTime.parse(sharepointListEntity.getProperty("createdDateTime").getValue().toString()));
        sharePointList.setModifiedDateTime(OffsetDateTime.parse(sharepointListEntity.getProperty("lastModifiedDateTime").getValue().toString()));
        sharePointList.setDescription(sharepointListEntity.getProperty("description").getValue().toString());
        sharePointList.setName(sharepointListEntity.getProperty("name").getValue().toString());
        sharePointList.setWebUrl(sharepointListEntity.getProperty("webUrl").getValue().toString());

        if (Strings.isNullOrEmpty(siteId)) {
            sharePointList.setSiteId("root");
        } else {
            sharePointList.setSiteId(String.format("sites/%s", siteId));
        }

        sharePointList.setId(String.format("sites/%s/lists/%s", sharePointList.getSiteId(), sharepointListEntity.getProperty("id").getValue().toString()));

        return sharePointList;
    }
}
