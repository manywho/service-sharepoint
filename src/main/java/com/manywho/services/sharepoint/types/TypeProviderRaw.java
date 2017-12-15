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
import java.util.Objects;

public class TypeProviderRaw implements TypeProvider<ServiceConfiguration> {

    private DescribeTypesManager describeTypesManager;

    @Inject
    public TypeProviderRaw(DescribeTypesManager describeTypesManager) {
        this.describeTypesManager = describeTypesManager;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration serviceConfiguration, String type) {
        // return false for static sites
        if (Objects.equals(type, Item.NAME) || Objects.equals(type, SharePointList.NAME)
                || Objects.equals(type, Site.NAME) || Objects.equals(type, Group.NAME) ||
                Objects.equals(type, User.NAME)) {

            return false;
        }

        // return true for all other types
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
