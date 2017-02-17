package com.manywho.services.sharepoint.facades;

import com.google.common.util.concurrent.ListenableFuture;
import com.manywho.services.sharepoint.oauth.AuthenticationCallbackImpl;
import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.services.files.fetchers.FilesClient;
import com.microsoft.services.graph.Item;
import com.microsoft.services.graph.fetchers.ItemCollectionOperations;
import com.microsoft.services.graph.fetchers.ItemFetcher;
import com.microsoft.services.graph.fetchers.UserFetcher;
import com.microsoft.services.orc.core.OrcCollectionFetcher;
import com.microsoft.services.orc.log.LogLevel;
import com.microsoft.services.orc.resolvers.JavaDependencyResolver;
import com.microsoft.services.graph.fetchers.GraphServiceClient;
import org.apache.commons.io.IOUtils;
import javax.inject.Inject;
import javax.ws.rs.ServiceUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SharePointFacade {

    private final static String GRAPH_ENDPOINT = "https://graph.microsoft.com/beta/manywho";

    @Inject
    public SharePointFacade() {
    }


    public Item createFile(String token, String userId, String path, InputStream inputStream) throws ExecutionException, InterruptedException, IOException {

        String filename = UUID.randomUUID().toString() + ".txt";
        JavaDependencyResolver resolver = new JavaDependencyResolver(token);
        resolver.getLogger().setEnabled(true);
        resolver.getLogger().setLogLevel(LogLevel.VERBOSE);

        Item newFile = new Item();
        //com.microsoft.services.files.Item newFile = new com.microsoft.services.files.Item();
        newFile.setType("File");
        newFile.setName(filename);

        GraphServiceClient client = new GraphServiceClient(GRAPH_ENDPOINT, resolver);
        Item addedFile = client.getUsers().getById(userId).getFiles().add(newFile).get();


        client.getUsers().getById(userId).getFiles().getById(addedFile.getId()).asFile().getOperations()
                .uploadContent(IOUtils.toByteArray(inputStream)).get();

        //FilesClient client = new FilesClient("https://manywho.sharepoint.com/_api/v1.0/me", resolver);
//        com.microsoft.services.files.Item addedFile = client.getFiles().add(newFile).get();
//        client.getFiles().getById(addedFile.getId()).asFile().putContent(IOUtils.toByteArray(inputStream)).get();

        //return addedFile;
        return null;
    }

//    public Folder createFolder(String path) {
//
////        String folderName = "newfolder"+ UUID.randomUUID().toString();
////        Item newFolder = new Item();
////        newFolder.setType("Folder");
////        newFolder.setName(folderName);
////
////        Item addedFolder = client.getUsers().getById(ID).().add(newFolder).get();
////
////        client.getUsers().getById(ID).getFiles().getById(addedFolder.getId()).asFolder();
////
////        byte[] content = client.getUsers().getById(ID).g().getById(addedFolder.getId()).asFile().getOperations().content().get();
//        return null;
//
////        try {
////            return service.createFolder(path);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public List<File> fetchFiles(String path) {
//        return null;
//
//        //try {
//        //    return service.getFiles(path);
//        //} catch (ServiceException e) {
//        //    throw new RuntimeException(e);
//        //}
//    }
//
//    public File fetchFile(String path) {
//        return null;
////        try {
////            return service.getFile(path);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public File copyFile(String accessToken, String sourcePath, String newPath)  {
//        return null;
////        try {
////            if(service.copyFile(sourcePath, newPath)){
////                return service.getFile(newPath);
////            }
////
////            throw new RuntimeException("File "+ newPath + "can not be copy");
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }
//
//    public Folder fetchFolder(String folderPath) {
//        return null;
////        try {
////            return service.getFolder(folderPath);
////        } catch (ServiceException e) {
////            throw new RuntimeException(e);
////        }
//    }


    public String getAccessTokenFromUserCredentials(String resource, String clientId, String authority) {
        AuthenticationContext context;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            try {
                context = new AuthenticationContext(authority, false, service);
                Future<AuthenticationResult> future = context.acquireToken(resource, clientId, "j.ci@test.com", "1",null);
                result = future.get();
            } catch (MalformedURLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "authentication result was null");
        }
        return result.getAccessToken();
    }


    public String getAccessToken(String authorizationCode, String authorityUrl, String resource, String clientId, String clientSecret, String redirectUri) {
        AuthenticationContext context;
        ExecutorService service = null;

        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authorityUrl, false, service);
            ClientCredential clientCredential = new ClientCredential(clientId, clientSecret);
            AuthenticationCallback authenticationCallback = new AuthenticationCallbackImpl();
            URI redirectUriObj = new URI(redirectUri);

            Future<AuthenticationResult> future = context.acquireTokenByAuthorizationCode(
                    authorizationCode,
                    redirectUriObj,
                    clientCredential,
                    resource,
                    authenticationCallback);

            AuthenticationResult result = future.get();

            if (result == null) {
                throw new ServiceUnavailableException("authentication result was null");
            }

            return result.getAccessToken();
        } catch (InterruptedException | ExecutionException | MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("authentication error");
        } finally {
            service.shutdown();
        }
    }
}
