package com.manywho.services.sharepoint.sites;

import com.google.common.base.Strings;
import com.manywho.services.sharepoint.mappers.ObjectMapperBase;
import org.apache.olingo.client.api.domain.ClientEntity;

import java.time.OffsetDateTime;

public class SiteMapper extends ObjectMapperBase<Site> {
    private String parentId;
    private String groupId;

    public SiteMapper(String parentId, String groupId) {
        this.parentId = parentId;
        this.groupId = groupId;
    }

    public Site getObject(ClientEntity siteItem) {
         Site site = new Site();

            site.setId("sites/" + siteItem.getProperty("id").getValue().toString());
            site.setCreatedDateTime(OffsetDateTime.parse(siteItem.getProperty("createdDateTime").getValue().toString()));
            site.setModifiedDateTime(OffsetDateTime.parse(siteItem.getProperty("lastModifiedDateTime").getValue().toString()));
            site.setDescription(siteItem.getProperty("description").getValue().toString());
            site.setName(siteItem.getProperty("name").getValue().toString());
            site.setGroupId(String.format("groups/%s", groupId));
            site.setParentId(parentId);

            if(Strings.isNullOrEmpty(parentId)) {
                site.setParentId("root");
            }

            site.setWebUrl(siteItem.getProperty("webUrl").getValue().toString());

            return site;
    }
}
