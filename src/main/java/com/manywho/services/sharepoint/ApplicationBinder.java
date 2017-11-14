package com.manywho.services.sharepoint;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.sharepoint.configuration.SecurityConfiguration;
import com.manywho.services.sharepoint.facades.*;
import com.manywho.services.sharepoint.managers.*;
import com.manywho.services.sharepoint.oauth.AzureHttpClient;
import com.manywho.services.sharepoint.oauth.SharepointProvider;
import com.manywho.services.sharepoint.services.*;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SharepointProvider.class).to(AbstractOauth2Provider.class);
    }
}
