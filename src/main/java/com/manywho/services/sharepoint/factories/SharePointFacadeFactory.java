package com.manywho.services.sharepoint.factories;

import com.independentsoft.share.Service;
import com.manywho.services.sharepoint.facades.SharepointFacade;

public class SharePointFacadeFactory {

    public SharepointFacade createSharePointFacade(String subdomain, String userName, String password) {
        return new SharepointFacade(new Service(subdomain, userName, password));
    }

}
