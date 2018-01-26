package com.manywho.services.sharepoint.constants;

public class ApiConstants {
    public final static String AUTHORITY = "https://login.windows.net/common";
    public final static String RESOURCE_GRAPH = "00000003-0000-0000-c000-000000000000";
    public static final String REDIRECT_URI = "https://flow.manywho.com/api/run/1/oauth2";
    public static final String AUTHORITY_URI = "https://login.microsoftonline.com/common";
    public final static String GRAPH_ENDPOINT_V1 = "https://graph.microsoft.com/v1.0";
    public final static String GRAPH_ENDPOINT_BETA = "https://graph.microsoft.com/beta";
    public final static String AUTHENTICATION_TYPE_AZURE_AD = "SharePoint Service";
    public final static String AUTHENTICATION_TYPE_ADD_IN = "SharePoint Add-In";
    public final static String AUTH_STRATEGY_SUPER_USER = "SuperUser";
}
