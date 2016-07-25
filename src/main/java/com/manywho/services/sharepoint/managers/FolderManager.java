package com.manywho.services.sharepoint.managers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.sharepoint.entities.Configuration;
import com.manywho.services.sharepoint.entities.request.FolderCreate;
import com.manywho.services.sharepoint.services.FolderSharePointService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.types.Folder;

import javax.inject.Inject;

public class FolderManager {
    private PropertyCollectionParser propertyParser;
    private FolderSharePointService folderSharepointService;
    private ObjectMapperService objectMapperService;

    @Inject
    public FolderManager(PropertyCollectionParser propertyParser, FolderSharePointService folderSharepointService,
                         ObjectMapperService objectMapperService) {

        this.propertyParser = propertyParser;
        this.folderSharepointService = folderSharepointService;
        this.objectMapperService = objectMapperService;
    }

    public ServiceResponse createFolder(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        FolderCreate folderCreate = propertyParser.parse(serviceRequest.getInputs(), FolderCreate.class);
        if (folderCreate == null) {
            throw new Exception("Unable to parse the incoming FolderCreate request");
        }
        Configuration configuration = propertyParser.parse(serviceRequest.getConfigurationValues(), Configuration.class);

        MObject folder = objectMapperService.buildManyWhoObjectFolder(
                folderSharepointService.createFolder(user.getToken(), folderCreate.getFolder().getId(), folderCreate.getName(), configuration)
        );

        EngineValue folderValue = new EngineValue("Folder", ContentType.Object, Folder.NAME, folder);

        return new ServiceResponse(InvokeType.Forward, folderValue, serviceRequest.getToken());
    }
}
