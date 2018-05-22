package com.manywho.services.sharepoint.auth.authorization;

import com.google.common.base.Strings;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/admin")
public class AdminAuthorizationController {

    @Path("/authorization")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public String adminAuthorizationSuccess(@QueryParam("code") String code, @QueryParam("admin_consent") String adminConsent) {

        String title = "Authorization Success";

        String body = "<h1>Authorization Success</h1>" +
                "<p>The client have been approved to be used in your organization.</p>" +
                "<p>You can now install the SharePoint Service, for more information please check " +
                "<a href=\"https://docs.manywho.com\">docs.manywho.com</a></p>";
        if (Strings.isNullOrEmpty(code) || Boolean.valueOf(adminConsent) != true) {
            title = "It have been a problem ";
            body = "<h1>It have been a problem authorizing the client.</h1>" +
                    "<p>This method should be called by a Microsoft client, never directly by a user.</p>" +
                    "<p> Please check the documentation at <a href=\"https://docs.manywho.com\">docs.manywho.com</a>.</p>";
        }

        return populateWeb(title, body);
    }

    private String populateWeb(String title, String body) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "    <title>%s</title>\n" +
                "</head>\n" +
                "<body>%s</body>\n" +
                "</html>", title, body);
    }
}
