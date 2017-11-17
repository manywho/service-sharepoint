package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.entities.draw.elements.type.TypeElement;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.sharepoint.entities.ServiceConfiguration;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.core.ODataClientFactory;
import org.glassfish.jersey.media.multipart.BodyPart;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SharePointOdataFacade implements SharePointFacadeInterface {
    private final static String GRAPH_ENDPOINT = "https://graph.microsoft.com/beta";

    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;
    private FileSharePointService fileSharePointService;

    @Inject
    public SharePointOdataFacade(ObjectMapperService objectMapperService, FileSharePointService fileSharePointService) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getClient();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
        this.fileSharePointService = fileSharePointService;
    }

    @Override
    public ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token) throws ExecutionException, InterruptedException {
        //this line should work but it doesn't
        //ODataRetrieveResponse<ODataEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "sites/root/sites");
        // we fetch groups
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "groups?$filter=groupTypes/any(c:c+eq+'Unified')");
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        for (ClientEntity group : listGroups) {
            List<ClientEntity> sites = fetchSiteByGroup(configuration, token, group.getProperty("id").getValue().toString());
            listSitesByGroups.addAll(sites);
        }

        return responseSites(listSitesByGroups, "");
    }

    private List<ClientEntity> fetchSiteByGroup(ServiceConfiguration configuration, String token, String groupId) {
        String urlEntity = String.format("groups/%s/sites/root?expand=columns", groupId);
        List<ClientEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return sites;
    }

    @Override
    public ObjectDataResponse fetchSites(ServiceConfiguration configuration, String token, String parentId) {
        String url = String.format("sites/%s/sites", parentId);
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, url);

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), parentId);
    }

    @Override
    public ObjectDataResponse fetchSite(ServiceConfiguration configuration, String token, String id) {
        String urlEntity = String.format("sites/%s", id);
        List<ClientEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return responseSites(sites, "");
    }

    @Override
    public ObjectDataResponse fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType) {
        String urlEntity = String.format("sites/%s/lists", idSite);
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseLists(entitySetResponse.getBody().getEntities(), idSite, fullType);
    }

    @Override
    public TypeElementCollection fetchTypesListsForAllSites(ServiceConfiguration configuration, String token) {
        TypeElementCollection typeElements = new TypeElementCollection();

        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "groups?$filter=groupTypes/any(c:c+eq+'Unified')");
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        for (ClientEntity group : listGroups) {
            List<ClientEntity> sites = fetchSiteByGroup(configuration, token, group.getProperty("id").getValue().toString());
            listSitesByGroups.addAll(sites);
        }

        for (ClientEntity site : listSitesByGroups) {
            String siteName = site.getProperty("name").getValue().toString();
            String urlEntity = String.format("sites/%s/lists", site.getProperty("id").getValue().toString());
            ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);
            List<ClientEntity> lists = entitySetResponse.getBody().getEntities();

            for (ClientEntity list : lists) {
                if (!configuration.getIncludeDefaultLists()) {
                    if (Objects.equals("IWConvertedForms", list.getProperty("name").getValue().toString()) ||
                            Objects.equals("Shared Documents", list.getProperty("name").getValue().toString()) ||
                            Objects.equals("SitePages ", list.getProperty("name").getValue().toString()) ||
                            Objects.equals("users", list.getProperty("name").getValue().toString())) {
                        break;
                    }
                }

                String typeDeveloperName = String.format("%s (%s) Ref#%s", list.getProperty("name").getValue().toString(), siteName, list.getProperty("id").getValue().toString());
                String typeDeveloperSummary = String.format("Type for list \"%s\" in site \"%s\"", list.getProperty("name").getValue().toString(), siteName);

                TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                        .setDeveloperName(typeDeveloperName)
                        .setTableName(list.getProperty("name").getValue().toString() + " " + siteName);

                for (ClientProperty property : list.getProperties()) {
                    ContentType contentType = null;
                    switch (property.getValue().getTypeName()) {
                        case "Edm.Boolean":
                            contentType = ContentType.Boolean;
                            break;
                        case "Edm.DateTime":
                            contentType = ContentType.DateTime;
                            break;
                        case "Edm.Decimal":
                        case "Edm.Double":
                        case "Edm.Single":
                        case "Edm.Int16":
                        case "Edm.Int32":
                        case "Edm.Int64":
                            contentType = ContentType.Number;
                            break;
                        case "Edm.Guid":
                        case "Edm.String":
                            contentType = ContentType.String;
                            break;
                        case "Edm.Time":
                        case "Edm.DateTimeOffset":
                            contentType = ContentType.DateTime;
                            break;
                        case "Edm.SByte":
                            break;
                        case "Edm.Byte":
                            break;
                        case "Edm.Binary":
                            break;
                        default:
                            break;
                    }

                    if (contentType != null) {
                        typeBuilder.addProperty(property.getName(), contentType, property.getName());
                    }
                }

                TypeElement typeElement = typeBuilder.build();
                typeElement.setDeveloperSummary(typeDeveloperSummary);
                typeElements.add(typeElement);
            }
        }

        return typeElements;
    }

    @Override
    public ObjectDataResponse fetchList(ServiceConfiguration configuration, String token, String idSite, String idList) {
        String entryPoint = String.format("sites/%s/lists/%s", idSite, idList);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);
        List<ClientEntity> lists = new ArrayList<>();
        lists.add(0, entitySetResponse.getBody());

        return responseLists(lists, idSite, false);
    }

    @Override
    public ObjectDataResponse fetchListsRoot(ServiceConfiguration configuration, String token) {
        return responseLists(getEntitiesSetResponse(token, "sites/root/lists").getBody().getEntities(), "", false);
    }


    @Override
    public ObjectDataResponse fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId) {
        String entryPoint = String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);

        List<ClientEntity> items = new ArrayList<>();
        items.add(0, entitySetResponse.getBody());

        return responseItems(items, siteId, listId);
    }

    @Override
    public ObjectDataResponse fetchItems(ServiceConfiguration configuration, String token, String siteId, String listId) {
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseItems(entitySetResponse.getBody().getEntities(), siteId, listId);
    }

    public ObjectDataResponse uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
        //String uploadPath = fileSharePointService.getAnUploadUrl(token, bodyPart, path);
        //fileSharePointService.uploadFile(token, uploadPath, bodyPart);
        fileSharePointService.uploadSmallFile(token, path, bodyPart);
        return new ObjectDataResponse();
    }

    private ODataRetrieveResponse<ClientEntitySet> getEntitiesSetResponse(String token, String urlEntity) {
        URI entitySetURI2 = client.newURIBuilder(GRAPH_ENDPOINT).build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest2 = retrieveRequestFactory.getEntitySetRequest(entitySetURI2);


        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(urlEntity).build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);

        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));

        return entitySetRequest.execute();
    }

    private ODataRetrieveResponse<ClientEntity> getEntitySetResponse(String token, String entryPoint) {
        URI entityUri = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(entryPoint).build();
        ODataEntityRequest<ClientEntity> entitySetRequest = retrieveRequestFactory.getEntityRequest(entityUri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        return entitySetRequest.execute();
    }

    private ObjectDataResponse responseSites(List<ClientEntity> sites, String parentId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ClientEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoSiteObject(siteEntity, parentId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseItems(List<ClientEntity> sites, String siteId, String listId) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ClientEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoItemObject(siteEntity, siteId, listId));
        }

        return new ObjectDataResponse(objectCollection);
    }

    private ObjectDataResponse responseLists(List<ClientEntity> lists, String siteId, boolean fullType) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ClientEntity listEntity : lists) {
            objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(listEntity, siteId, fullType));
        }

        return new ObjectDataResponse(objectCollection);
    }
}
