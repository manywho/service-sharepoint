package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.managers.DescribeTypesManager;

import javax.inject.Inject;
import java.util.List;

public class TypeProviderRaw implements TypeProvider<ApplicationConfiguration> {

    private DescribeTypesManager describeTypesManager;

    @Inject
    public TypeProviderRaw(DescribeTypesManager describeTypesManager) {
        this.describeTypesManager = describeTypesManager;
    }

    @Override
    public boolean doesTypeExist(ApplicationConfiguration applicationConfiguration, String s) {
        return true;
    }

    @Override
    public List<TypeElement> describeTypes(ApplicationConfiguration applicationConfiguration, DescribeServiceRequest describeServiceRequest) {
        return describeTypesManager.getTypeElements(applicationConfiguration);
    }
}
