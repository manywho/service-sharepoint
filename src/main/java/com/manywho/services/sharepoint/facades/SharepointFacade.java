package com.manywho.services.sharepoint.facades;
import com.independentsoft.share.File;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;
import java.io.InputStream;
import java.util.List;

public class SharepointFacade {

    private Service service;

    public SharepointFacade(Service service) {
        this.service = service;
    }

    public File createFile(String path, InputStream inputStream) {
        try {
            return service.createFile(path, inputStream);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public List<File> fetchFiles(String path) {
        try {
            return service.getFiles(path);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public File fetchFile(String path) {
        try {
            return service.getFile(path);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public File copyFile(String accessToken, String sourcePath, String newPath)  {
        try {
            if(service.copyFile(sourcePath, newPath)){
                return service.getFile(newPath);
            }

            throw new RuntimeException("File "+ newPath + "can not be copy");
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
}
