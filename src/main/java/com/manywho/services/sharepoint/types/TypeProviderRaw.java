package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.managers.DescribeTypesManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TypeProviderRaw implements TypeProvider<ServiceConfiguration> {

    private DescribeTypesManager describeTypesManager;

    @Inject
    public TypeProviderRaw(DescribeTypesManager describeTypesManager) {
        this.describeTypesManager = describeTypesManager;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration serviceConfiguration, String s) {
        return true;
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration serviceConfiguration, DescribeServiceRequest describeServiceRequest) {
        if (Strings.isNullOrEmpty(serviceConfiguration.getUsername())) {
            return new ArrayList<>();
        }

        return describeTypesManager.getTypeElements(serviceConfiguration);
    }
}
