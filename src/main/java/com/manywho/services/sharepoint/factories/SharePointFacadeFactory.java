package com.manywho.services.sharepoint.factories;

import com.independentsoft.share.Service;
import com.manywho.services.sharepoint.facades.SharepointFacade;

public class SharePointFacadeFactory {

    final public static String SHAREPOINT_URL = "https://%s.sharepoint.com";

    public SharepointFacade createSharePointFacade(String uri, String userName, String password) {
        return new SharepointFacade(new Service(uri, userName, password));
    }

    public SharepointFacade createSharePointFacadeForSubdomain(String uriSubdomain, String userName, String password) {
        return new SharepointFacade(new Service(String.format(SHAREPOINT_URL, uriSubdomain), userName, password));
    }

}
