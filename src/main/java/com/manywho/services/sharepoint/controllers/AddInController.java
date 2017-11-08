package com.manywho.services.sharepoint.controllers;

import javax.ws.rs.*;

@Path("/callback")
public class AddInController {

    @Path("/run-flow")
    @Produces("text/html")
    @POST
    public String ShowFlowPost(@FormParam("SPAppToken") String contextToken) {


        return runFlow(contextToken);
    }

    public String runFlow(String accessToken) {

        String tenantId ="8b572d5b-76ba-473e-9e37-be06b6e8a396";
        String flowId= "0620de05-dd64-4c8f-b61d-1513fefeb917";
        String flowVersionId="23758157-410b-4d04-86f7-4c9c6ef59e98";
        String adminTenantId = "da497693-4d02-45db-bc08-8ea16d2ccbdf";
        String host = "flow.manywho.com";
        String player = "default";
        String navigationElementId = "null";
        String mode = "null";
        String reportingMode = "null";
        String theme = "null";
        String join = "null";
        String authorization = "null";
        String initialization = "null";

        String template =  "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" class=\"manywho\" style=\"height: 100%;\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no\">\n" +
                "    <title>ManyWho</title>\n" +
                "    <style>\n" +
                "        .mw-bs .wait-container {\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            min-height: 500px;\n" +
                "            z-index: 1100;\n" +
                "            background-color: rgba(255, 255, 255, 0.5);\n" +
                "        }\n" +
                "\n" +
                "        .mw-bs .wait-message {\n" +
                "            position: relative;\n" +
                "            text-align: center;\n" +
                "            margin-top: 1em;\n" +
                "            display: block;\n" +
                "            top: 40%;\n" +
                "            font-size: 2em;\n" +
                "            padding: 0 2em;\n" +
                "        }\n" +
                "\n" +
                "        /* outer */\n" +
                "        .mw-bs .wait-spinner {\n" +
                "            display: block;\n" +
                "            position: relative;\n" +
                "            left: 50%;\n" +
                "            width: 150px;\n" +
                "            height: 150px;\n" +
                "            margin: 200px 0 0 -75px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #268AAF;\n" +
                "            -webkit-animation: spin 2s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "            animation: spin 2s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        /* middle */\n" +
                "        .mw-bs .wait-spinner:before {\n" +
                "            content: \"\";\n" +
                "            position: absolute;\n" +
                "            top: 5px;\n" +
                "            left: 5px;\n" +
                "            right: 5px;\n" +
                "            bottom: 5px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #31B2E2;\n" +
                "            -webkit-animation: spin 3s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "              animation: spin 3s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        /* inner */\n" +
                "        .mw-bs .wait-spinner:after {\n" +
                "            content: \"\";\n" +
                "            position: absolute;\n" +
                "            top: 15px;\n" +
                "            left: 15px;\n" +
                "            right: 15px;\n" +
                "            bottom: 15px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #154E62;\n" +
                "            -webkit-animation: spin 1.5s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "              animation: spin 1.5s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        @-webkit-keyframes spin {\n" +
                "            0%   {\n" +
                "                -webkit-transform: rotate(0deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(0deg);  /* IE 9 */\n" +
                "                transform: rotate(0deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "            100% {\n" +
                "                -webkit-transform: rotate(360deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(360deg);  /* IE 9 */\n" +
                "                transform: rotate(360deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "        }\n" +
                "        @keyframes spin {\n" +
                "            0%   {\n" +
                "                -webkit-transform: rotate(0deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(0deg);  /* IE 9 */\n" +
                "                transform: rotate(0deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "            100% {\n" +
                "                -webkit-transform: rotate(360deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(360deg);  /* IE 9 */\n" +
                "                transform: rotate(360deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"height: 100%;\">\n" +

                "<div id=\"manywho\">\n" +
                "    <div id=\"loader\" class=\"mw-bs\" style=\"width: 100%; height: 100%;\">\n" +
                "        <div class=\"wait-container\">\n" +
                "            <div class=\"wait-spinner\"></div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "<script src=\"https://assets.manywho.com/js/vendor/jquery-2.1.4.min.js\"></script>\n" +
                "\n" +
                "<script>\n" +
                "    //syncOnUnload: false,\n" +
                "    var manywho = {\n" +
                "        cdnUrl: \"https://assets.manywho.com\",\n" +
                "        initialize: function () {\n" +
                "            manywho.settings.initialize({\n" +
                "                adminTenantId: \"{{adminTenantId}}\",\n" +
                "                playerUrl: [\"https\", '//', \"{{host}}\", \"/{{tenantId}}/play/{{player}}\"].join(''),\n" +
                "                joinUrl: [\"https\", '//', \"{{host}}\", \"/{{tenantId}}/play/{{player}}\"].join(''),\n" +
                "                platform: { \"uri\": \"https://\"+\"{{host}}\"}\n" +
                "            });\n" +
                "\n" +
                "            var options = {\n" +
                "                authentication: {\n" +
                "                    sessionId: \"{{accessToken}}\",\n" +
                "                    sessionUrl: null\n" +
                "                },\n" +
                "                navigationElementId: {{navigationElementId}},\n" +
                "                mode: {{mode}},\n" +
                "                reportingMode: {{reportingMode}},\n" +
                "                replaceUrl: false,\n" +
                "                collaboration: {\n" +
                "                    isEnabled: false\n" +
                "                },\n" +
                "                inputs: null,\n" +
                "                annotations: null,\n" +
                "                navigation: {\n" +
                "                    isFixed: true,\n" +
                "                    isWizard: false\n" +
                "                },\n" +
                "                callbacks: [],\n" +
                "                theme: {{theme}}\n" +
                "            };\n" +
                "\n" +
                "            console.log(options);\n" +
                "\n" +
                "            manywho.engine.initialize(\n" +
                "                \"{{tenantId}}\",\n" +
                "                \"{{flowId}}\",\n" +
                "                \"{{flowVersionId}}\",\n" +
                "                'main',\n" +
                "                {{join}},\n" +
                "                {{authorization}},\n" +
                "                options,\n" +
                "                {{initialization}}\n" +
                "            );\n" +
                "\n" +
                "        }\n" +
                "    };\n" +
                "</script>\n" +
                "\n" +
                "<script src=\"https://assets.manywho.com/js/loader.min.js\"></script>"+
                "\n" +
                "</body>\n" +
                "</html>\n";

        template = template.replace("{{tenantId}}", tenantId);
        template = template.replace("{{adminTenantId}}", adminTenantId);
        template = template.replace("{{flowId}}", flowId);
        template = template.replace("{{flowVersionId}}", flowVersionId);
        template = template.replace("{{adminTenant}}", adminTenantId);
        template = template.replace("{{host}}", host);
        template = template.replace("{{player}}", player);
        template = template.replace("{{navigationElementId}}", navigationElementId);
        template = template.replace("{{mode}}", mode);
        template = template.replace("{{reportingMode}}", reportingMode);
        template = template.replace("{{theme}}", theme);
        template = template.replace("{{join}}", join);
        template = template.replace("{{authorization}}", authorization);
        template = template.replace("{{initialization}}", initialization);
        template = template.replace("{{accessToken}}", accessToken);

        return template;
    }
}
