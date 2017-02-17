package com.manywho.services.sharepoint.oauth;

import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationResult;

public class AuthenticationCallbackImpl implements AuthenticationCallback {
    @Override
    public void onSuccess(AuthenticationResult result) {
        String a = "a";
    }

    @Override
    public void onFailure(Throwable exc) {
        String a = "b";
    }
}
