package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.sharepoint.client.OauthAuthenticationClient;
import com.manywho.services.sharepoint.client.entities.AuthResponse;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.drives.Drive;
import com.manywho.services.sharepoint.drives.items.DriveItem;
import com.manywho.services.sharepoint.groups.Group;
import com.manywho.services.sharepoint.lists.SharePointList;
import com.manywho.services.sharepoint.lists.items.SharePointListItem;
import com.manywho.services.sharepoint.sites.Site;
import com.manywho.services.sharepoint.users.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TypeProviderRaw implements TypeProvider<ServiceConfiguration> {

    private DynamicTypesOdataClient dynamicTypesClient;
    private OauthAuthenticationClient oauthAuthenticationClient;

    @Inject
    public TypeProviderRaw(DynamicTypesOdataClient dynamicTypesClient, OauthAuthenticationClient oauthAuthenticationClient) {
        this.dynamicTypesClient = dynamicTypesClient;
        this.oauthAuthenticationClient = oauthAuthenticationClient;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration serviceConfiguration, String type) {
        // return false for static sites
        switch(type) {
            case SharePointListItem.NAME:
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
    public List<TypeElement> describeTypes(ServiceConfiguration configuration, DescribeServiceRequest describeServiceRequest) {
        if (Strings.isNullOrEmpty(configuration.getUsername())) {
            return new ArrayList<>();
        }

        AuthResponse authenticationResult  = oauthAuthenticationClient.getAccessTokenFromUserCredentials(
                configuration.getUsername(),
                configuration.getPassword());

        return this.dynamicTypesClient.fetchAllListTypes(authenticationResult.getAccessToken());
    }
}
