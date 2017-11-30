package com.manywho.services.sharepoint.controllers;

import com.manywho.sdk.entities.describe.DescribeServiceRequest;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.managers.DescribeTypesManager;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DescribeController extends AbstractController {

    private PropertyCollectionParser propertyCollectionParser;
    private DescribeTypesManager typeManager;

    @Inject
    DescribeController(PropertyCollectionParser propertyCollectionParser, DescribeTypesManager typeManager) {
        this.propertyCollectionParser = propertyCollectionParser;
        this.typeManager = typeManager;
    }

    @Path("/metadata")
    @POST
    @AuthorizationRequired
    public DescribeServiceResponse describe(DescribeServiceRequest describeServiceRequest) throws Exception {
        TypeElementCollection typeElements = new TypeElementCollection();

        if (describeServiceRequest.hasConfigurationValues()) {
            EngineValueCollection configurationValues = describeServiceRequest.getConfigurationValues();
            ServiceConfiguration configuration = propertyCollectionParser.parse(configurationValues, ServiceConfiguration.class);

            if (!StringUtils.isEmpty(configuration.getHost()) && !StringUtils.isEmpty(configuration.getUsername()) &&
                !StringUtils.isEmpty(configuration.getPassword())) {

                typeElements = typeManager.getTypeElements(configuration);
            }
        }


        return new DescribeServiceBuilder()
                .setProvidesDatabase(true)
                .setProvidesFiles(true)
                .setProvidesLogic(true)
                .setCulture(new Culture("EN", "US"))
                .addConfigurationValue(new DescribeValue("Host", ContentType.String, false))
                .addConfigurationValue(new DescribeValue("Username", ContentType.String, false))
                .addConfigurationValue(new DescribeValue("Password", ContentType.Password, false))
                .addConfigurationValue(new DescribeValue("Include Default Lists?", ContentType.Boolean, false))
                .addConfigurationValue(new DescribeValue("Only For Groups", ContentType.String, false))
                .setTypes(typeElements)
                .createDescribeService()
                .createResponse();
    }
}

