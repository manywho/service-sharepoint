package com.manywho.services.sharepoint.users;

public class GraphRestCompatibilityUtility {

    public static String getUserPrincipalName(String graphUserLogin) {
        String parts[] = graphUserLogin.split("\\|");
        if (parts.length != 3) {
            String error = String.format("Unexpected UserLogin %s (it is expected something like i:0#.f|membership|user@domain.com)",
                    graphUserLogin);

            throw new RuntimeException(error);
        }

        return parts[2];
    }
}
