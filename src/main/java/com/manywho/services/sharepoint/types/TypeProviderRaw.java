package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.database.dynamic.DescribeDynamicTypesManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TypeProviderRaw implements TypeProvider<ServiceConfiguration> {

    private DescribeDynamicTypesManager describeTypesManager;

    @Inject
    public TypeProviderRaw(DescribeDynamicTypesManager describeTypesManager) {
        this.describeTypesManager = describeTypesManager;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration serviceConfiguration, String type) {
        // return false for static sites
        switch(type) {
            case Item.NAME:
            case SharePointList.NAME:
            case Site.NAME:
            case Group.NAME:
            case User.NAME:
            case Drive.NAME:
            case DriveItem.NAME:
                return false;
            default:
                return true;
        }
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration serviceConfiguration, DescribeServiceRequest describeServiceRequest) {
        if (Strings.isNullOrEmpty(serviceConfiguration.getUsername())) {
            return new ArrayList<>();
        }

        return describeTypesManager.getTypeElements(serviceConfiguration);
    }
}
