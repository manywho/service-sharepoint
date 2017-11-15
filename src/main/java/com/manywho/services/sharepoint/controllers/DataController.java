package com.manywho.services.sharepoint.controllers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.services.sharepoint.managers.FileManager;
import com.manywho.services.sharepoint.managers.ItemManager;
import com.manywho.services.sharepoint.managers.ListManager;
import com.manywho.services.sharepoint.managers.SiteManager;
import com.manywho.services.sharepoint.types.Item;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DataController extends AbstractDataController {

    private SiteManager siteManager;
    private ListManager listManager;
    private ItemManager itemManager;

    @Inject
    public DataController(SiteManager siteManager, ListManager listManager, ItemManager itemManager)
    {
        this.siteManager = siteManager;
        this.listManager = listManager;
        this.itemManager = itemManager;
    }

    @Override
    public ObjectDataResponse delete(ObjectDataRequest objectDataRequest) throws Exception {
        throw new Exception("Deleting isn't currently supported in the SharePoint Service");
    }

    @Path("/data")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse load(ObjectDataRequest objectDataRequest) throws Exception {
        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            case Site.NAME:
                return siteManager.loadSites(getAuthenticatedWho(), objectDataRequest);
            case SharePointList.NAME:
                return listManager.loadLists(getAuthenticatedWho(), objectDataRequest);
            case Item.NAME:
                return itemManager.loadItems(getAuthenticatedWho(), objectDataRequest);
        }

        throw new Exception("object not found");
    }

    @Path("/data")
    @PUT
    @AuthorizationRequired
    public ObjectDataResponse save(ObjectDataRequest objectDataRequest) throws Exception {
        throw new Exception("Save isn't currently supported in the SharePoint Service");
    }
}