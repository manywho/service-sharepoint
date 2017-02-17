package com.manywho.services.sharepoint.controllers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.services.sharepoint.managers.FileManager;
import com.manywho.services.sharepoint.types.File;
import com.manywho.services.sharepoint.types.Folder;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DataController extends AbstractDataController {

    private FileManager fileManager;

    @Inject
    public DataController(FileManager fileManager) {
        this.fileManager = fileManager;
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
//            case File.NAME:
//                return fileManager.loadFile(getAuthenticatedWho(), objectDataRequest);
//            case Folder.NAME:
//                return fileManager.loadFolder(getAuthenticatedWho(), objectDataRequest);
            //case Site
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