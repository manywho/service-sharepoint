package com.manywho.services.sharepoint.files;


//@Path("/file")
//@Consumes("application/json")
//@Produces("application/json")
public class FileController {
//public class FileController extends AbstractController {
//
//    private FileManager fileManager;
//    @Inject
//
//    public FileController(FileManager fileManager) {
//        this.fileManager = fileManager;
//    }
//
//    @Path("/copy")
//    @POST
//    @AuthorizationRequired
//    public ServiceResponse copyFile(ServiceRequest serviceRequest) throws Exception {
//        return null;
//        //return fileManager.copyFile(getAuthenticatedWho(), serviceRequest);
//    }
//
//    @Path("/")
//    @POST
//    @AuthorizationRequired
//    public ObjectDataResponse loadFiles(FileDataRequest fileDataRequest) throws Exception {
//        return null;
//        //return fileManager.loadFiles(getAuthenticatedWho(), fileDataRequest);
//    }
//
//    @POST
//    @Path("/content")
//    @Consumes({"multipart/form-data", "application/octet-stream"})
//    @AuthorizationRequired
//    public ObjectDataResponse uploadFile(@FormDataParam("FileDataRequest") FileDataRequest fileDataRequest, FormDataMultiPart file) throws Exception {
//        //fileManager.retrieveGraphServicesFile(getAuthenticatedWho(), fileDataRequest, file);
//        //return null;
//        return fileManager.uploadFile(getAuthenticatedWho().getToken(), fileDataRequest, file);
//    }
}
