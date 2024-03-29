ManyWho SharePoint Service 

(This service is EOL and will be removed in the near future)

==========================

This service allows you to integrate your Flows with [SharePoint](https://products.office.com/en-gb/sharepoint).

Note:

Known limitations:
- the dynamic types are the list created by the users in the groups, only from the first level of each sites. E.g. if you have a site that have a child site with a list, this one it won't be include like type
- the limit of the files to be uploaded is 60MiB
- the token from the Add-in works but then the service use a odata calls, Super User approach

To authorize Boomi Flow app for all the organization the admin should give permissions following this link:

https://login.microsoftonline.com/common/oauth2/authorize?client_id={clientId}&response_type=code&redirect_uri={redirect-uri}%2Fauthorization&nonce={random-nonce}&resource=00000002-0000-0000-c000-000000000000&prompt=admin_consent

> The `redirect_uri` parameter should be URL-encoded, like `https%3A%2F%2Fservices.manywho.com%2Fapi%2Fsharepoint%2F1%2Fadmin`

#### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

To build the service, you will need to have Apache Ant, Maven 3 and a Java 8 implementation installed.

You will need to generate a configuration file for the service by running the provided build.xml script with Ant.

This service requires a SharePoint Add-In (e.g. https://github.com/manywho/sharepoint-flow-addin) and a oauth2 windows client that have support for the implicit flow.

At the moment of write this code you can register the SharePoint Add-In at https://{your sharepoint domain}/_layouts/15/AppRegNew.aspx
the redirect url should be https://{the domain of this project}/api/sharepoint/1/callback/run-flow

and for register the oauth2 client you should register it at https://apps.dev.microsoft.com/?mkt=en-us#/appList
the redirect URL should be https://flow.manywho.com/api/run/1/oauth2
The app should support Platforms Web and native, also the Allow Implicit Flow should be checked

Add-in considerations (from Microsoft docs)
In order to use a context token (add-in tokens) with SharePoint 2013 apps, you will need to create a provider-hosted app that uses a client ID and a client secret.  
This requires that the target SharePoint farm has a trust configured to Azure Access Control Services, or ACS.  
Office365 automatically configures this trust for you, so if you create your app using Office365 then this works just fine.  
If you create your app using your own SharePoint farm, then you will need to configure low-trust apps for on-premises deployments 
in order to establish a trust with Azure ACS for your SharePoint farm.

# SharePoint-ManyWho

$ ant -Doauth2.clientId=xxx \
-Doauth2.clientSecretxxx \
-Dapp.clientSecret=xxx \
-Dapp.clientId=xxx

Now you can build the runnable shaded JAR

##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/sharepoint/1`:

```bash
$ java -jar target/sharepoint-1.0-SNAPSHOT.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/sharepoint/1`):

```bash
$ java -Dserver.port=9090 -jar target/sharepoint-1.0-SNAPSHOT.jar
```

## Contributing

Contribution are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
