package com.manywho.services.sharepoint;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.Servlet3Server;
import com.manywho.sdk.services.servers.undertow.UndertowServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends Servlet3Server {
    final static Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        logger.info("Entering application.");
        this.addModule(new ApplicationModule());
        this.setApplication(Application.class);
        this.start();
    }

    public static void main(String[] args) throws Exception {
        logger.info("Entering main.");
        EmbeddedServer server = new UndertowServer();

        server.addModule(new ApplicationModule());
        server.setApplication(Application.class);
        server.start();
    }
}
