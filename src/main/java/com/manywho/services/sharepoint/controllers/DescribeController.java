package com.manywho.services.sharepoint.controllers;

import com.manywho.sdk.entities.describe.DescribeServiceRequest;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DescribeController extends AbstractController {

    @Path("/metadata")
    @POST
    @AuthorizationRequired
    public DescribeServiceResponse describe(DescribeServiceRequest describeServiceRequest) throws Exception {
        return new DescribeServiceBuilder()
                .setProvidesDatabase(true)
                .setProvidesFiles(true)
                .setProvidesLogic(true)
                .setCulture(new Culture("EN", "US"))
                .addConfigurationValue(new DescribeValue("Username", ContentType.String, true))
                .addConfigurationValue(new DescribeValue("Password", ContentType.Password, true))
                .addConfigurationValue(new DescribeValue("Host", ContentType.String, true))
                .createDescribeService()
                .createResponse();
    }
}

