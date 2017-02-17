package com.manywho.services.sharepoint.factories;

import com.manywho.services.sharepoint.facades.SharePointFacade;
import com.microsoft.services.graph.fetchers.GraphServiceClient;
//import com.microsoft.aad.adal4j.AuthenticationContext;
//import com.microsoft.aad.adal4j.AuthenticationResult;
//import com.microsoft.services.graph.fetchers.GraphServiceClient;
//import com.microsoft.services.orc.log.LogLevel;
//import com.microsoft.services.orc.resolvers.JavaDependencyResolver;

import javax.inject.Inject;

public class SharePointFacadeFactory {


    @Inject
    public SharePointFacadeFactory() {

    }

    public SharePointFacade createSharePointFacade(String resource, String userName, String password) {
//        AuthenticationResult result = null;
//        try {
//            resource = "https://manywho.sharepoint.com";
//            result = accessToken(resource, ID, PASSWORD);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("Access Token - " + result.getAccessToken());
//        System.out.println("Refresh Token - " + result.getRefreshToken());
//
//        JavaDependencyResolver resolver = new JavaDependencyResolver(result.getAccessToken());
//        resolver.getLogger().setEnabled(true);
//        resolver.getLogger().setLogLevel(LogLevel.VERBOSE);
//
//        return new SharePointFacade(new GraphServiceClient(resource, resolver));
//        return new SharePointFacade(null);
        return null;
    }

//    private static AuthenticationResult accessToken(String resource, String username, String password) throws Exception {
//        AuthenticationContext context;
//        AuthenticationResult result = null;
//        ExecutorService service = null;
//        try {
//            service = Executors.newFixedThreadPool(1);
//            context = new AuthenticationContext(AUTHORITY, false, service);
//            Future<AuthenticationResult> future = context.acquireToken(
//                    resource, CLIENT_ID, username, password,
//                    null);
//            result = future.get();
//        } finally {
//            service.shutdown();
//        }
//
//        if (result == null) {
//            throw new ServiceUnavailableException("authentication result was null");
//        }
//        return result;
//    }

}
