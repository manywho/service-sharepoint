package com.manywho.services.sharepoint.facades;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ApplicationConfiguration;
import com.manywho.services.sharepoint.services.DynamicTypesService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import com.manywho.services.sharepoint.types.SharePointList;
import com.manywho.services.sharepoint.types.Site;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
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
    public List<Site> fetchSites(ApplicationConfiguration configuration, String token) {
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

    private List<ClientEntity> fetchSiteByGroup(ApplicationConfiguration configuration, String token, String groupId) {
        String urlEntity = String.format("groups/%s/sites/root", groupId);
        List<ClientEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return sites;
    }

    @Override
    public List<Site> fetchSites(ApplicationConfiguration configuration, String token, String parentId) {
        String url = String.format("sites/%s/sites", parentId);
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, url);

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), parentId);
    }

    @Override
    public Site fetchSite(ApplicationConfiguration configuration, String token, String id) {
        String urlEntity = String.format("sites/%s", id);
        List<ClientEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return responseSites(sites, "").get(0);
    }

    @Override
    public List<SharePointList> fetchLists(ApplicationConfiguration configuration, String token, String idSite, boolean fullType) {
        String urlEntity = String.format("sites/%s/lists", idSite);
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseLists(entitySetResponse.getBody().getEntities(), idSite, fullType);
    }

    @Override
    public List<TypeElement> fetchAllListTypes(ApplicationConfiguration configuration, String token) {
        List<TypeElement> typeElements = new ArrayList<>();
        String groupFilter = "groups?$filter=groupTypes/any(c:c+eq+'Unified')";

        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, groupFilter);
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        for (ClientEntity group : listGroups) {
            List<ClientEntity> sites = fetchSiteByGroup(configuration, token, group.getProperty("id").getValue().toString());
            listSitesByGroups.addAll(sites);
        }

        for (ClientEntity site : listSitesByGroups) {
            String siteName = site.getProperty("name").getValue().toString();
            String siteId = site.getProperty("id").getValue().toString();
            String urlEntity = String.format("sites/%s/lists", siteId);
            ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);
            List<ClientEntity> lists = entitySetResponse.getBody().getEntities();

            for (ClientEntity list : lists) {
                ClientValue infoList = list.getProperty("list").getValue();
                String displayName = infoList.asComplex().get("template").getValue().asPrimitive().toString();

                if (!configuration.getIncludeDefaultLists()) {
                    if (Objects.equals("genericList", displayName)) {
                        TypeElement typeElement = getTypeElement(token, siteId, siteName, list);
                        if (typeElement != null) {
                            typeElements.add(typeElement);
                        }
                    }
                } else {
                    TypeElement typeElement = getTypeElement(token, siteId, siteName, list);
                    if (typeElement != null) {
                        typeElements.add(typeElement);
                    }
                }
            }
        }

        return typeElements;
    }




    private TypeElement getTypeElement(String token, String siteId, String siteName, ClientEntity list) {
        String listName = list.getProperty("name").getValue().toString();
        String typeDeveloperName = String.format("%s Type", listName);
        String typeDeveloperSummary = String.format("Type for list \"%s\" in site \"%s\"", listName, siteName);

        TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                .setDeveloperName(typeDeveloperName)
                .setTableName(String.format("sites/%s/lists/%s", siteId, list.getProperty("id").getValue().asPrimitive().toString()));

        // should be possible extends=columns in list but for some reason I can not access to those properties with olingo
        ODataRetrieveResponse<ClientEntitySet> oDataRetrieveResponse = getEntitiesSetResponse(token, list.getNavigationLink("columns").getLink().toString().replace(GRAPH_ENDPOINT, ""));
        List<ClientEntity> properties = oDataRetrieveResponse.getBody().getEntities();
        for (ClientEntity property : properties) {
            ContentType contentType = null;

            if (property.getProperty("text") != null) {
                contentType = ContentType.String;
            } else if (property.getProperty("dateTime") != null) {
                contentType = ContentType.DateTime;
            } else if (property.getProperty("boolean") != null) {
                contentType = ContentType.Boolean;
            } else if (property.getProperty("number") != null) {
                contentType = ContentType.Number;
            } else {
                contentType = null;
            }

            if (!(boolean) property.getProperty("readOnly").getValue().asPrimitive().toValue() && contentType != null) {
                String propertyDisplayName = property.getProperty("name").getValue().toString();
                String propertyName = property.getProperty("displayName").getValue().toString();

                typeBuilder.addProperty(propertyName, contentType, propertyDisplayName);
            }

            if (property.getProperty("name") != null && Objects.equals(property.getProperty("name").getValue().asPrimitive().toString(), "ID")) {
                //

                typeBuilder.addProperty("ID", ContentType.String, "ID");
            }

        }

        TypeElement typeElement = typeBuilder.build();
        typeElement.setDeveloperSummary(typeDeveloperSummary);

        return typeElement;
    }

    @Override
    public SharePointList fetchList(ApplicationConfiguration configuration, String token, String idSite, String idList) {
        String entryPoint = String.format("sites/%s/lists/%s", idSite, idList);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);
        List<ClientEntity> lists = new ArrayList<>();
        lists.add(0, entitySetResponse.getBody());

        return responseLists(lists, idSite, false).get(0);
    }

    @Override
    public List<SharePointList> fetchListsRoot(ApplicationConfiguration configuration, String token) {
        return responseLists(getEntitiesSetResponse(token, "sites/root/lists").getBody().getEntities(), "", false);
    }

    @Override
    public MObject fetchItem(ApplicationConfiguration configuration, String token, String siteId, String listId, String itemId) {
        String entryPoint = String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);

        List<ClientEntity> items = new ArrayList<>();
        items.add(0, entitySetResponse.getBody());

        return responseItems(items, siteId, listId).get(0);
    }

    @Override
    public List<MObject> fetchItems(ApplicationConfiguration configuration, String token, String siteId, String listId) {
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseItems(entitySetResponse.getBody().getEntities(), siteId, listId);
    }
//
//    public MObject uploadFileToSharePoint(String token, String path, BodyPart bodyPart) {
//        fileSharePointService.uploadSmallFile(token, path, bodyPart);
//        return new ObjectDataResponse();
//    }

    @Override
    public List<MObject> fetchTypesFromLists(ApplicationConfiguration configuration, String token, String developerName,
                                             List<ObjectDataTypeProperty> properties) {

        String entryPoint = String.format("%s/items", developerName);
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(entryPoint).expand("fields").build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();

        return responseDynamicTypes(entitySetResponse.getBody().getEntities(), properties);
    }

    @Override
    public MObject fetchTypeFromList(ApplicationConfiguration configuration, String token, String developerName,
                                     String itemId, List<ObjectDataTypeProperty> properties) {

        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT)
                .appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(itemId)
                .expand("fields").build();

        ODataEntityRequest<ClientEntity> entityRequest = retrieveRequestFactory.getEntityRequest(entitySetURI);
        entityRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

        List<ClientEntity> items = new ArrayList<>();
        items.add(0, entityResponse.getBody());

        return responseDynamicTypes(items, properties).get(0);
    }

    @Override
    public MObject updateTypeList(ApplicationConfiguration configuration, String token, String developerName, List<Property> properties, String id) {

        URI itemUri = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(id)
                .appendEntitySetSegment("fields")
                .build();

        DynamicTypesService.patchDynamicType(token, itemUri.toString(), properties);

        List<ObjectDataTypeProperty> propertyCollection = new ArrayList<>();
        properties.forEach(p -> {
            ObjectDataTypeProperty prop = new ObjectDataTypeProperty();
            prop.setDeveloperName(p.getDeveloperName());
            propertyCollection.add(prop);
        });

        return fetchTypeFromList(configuration, token, developerName, id, propertyCollection);
    }


    @Override
    public MObject createTypeList(ApplicationConfiguration configuration, String token, String developerName, List<Property> properties) {
        String itemId = null;

        URI itemUri = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .build();

        itemId = DynamicTypesService.insertDynamicType(token, itemUri.toString());

        URI itemUriUpdate = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(itemId)
                .appendEntitySetSegment("fields")
                .build();

        DynamicTypesService.patchDynamicType(token, itemUriUpdate.toString(), properties);

        List<ObjectDataTypeProperty> propertyCollection = new ArrayList<>();
        properties.forEach(p -> {
            ObjectDataTypeProperty prop = new ObjectDataTypeProperty();
            prop.setDeveloperName(p.getDeveloperName());
            propertyCollection.add(prop);
        });

        return fetchTypeFromList(configuration, token, developerName, itemId, propertyCollection);
    }

    private ODataRetrieveResponse<ClientEntitySet> getEntitiesSetResponse(String token, String urlEntity) {
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

    private List<Site> responseSites(List<ClientEntity> sites, String parentId) {
        List<Site> objectCollection = new ArrayList<>();

        for (ClientEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoSiteObject(siteEntity, parentId));
        }

        return objectCollection;
    }

    private List<MObject> responseItems(List<ClientEntity> sites, String siteId, String listId) {
        List<MObject> objectCollection = new ArrayList<>();

        for (ClientEntity siteEntity : sites) {
            objectCollection.add(this.objectMapperService.buildManyWhoItemObject(siteEntity, siteId, listId));
        }

        return objectCollection;
    }

    private List<SharePointList> responseLists(List<ClientEntity> lists, String siteId, boolean fullType) {
        List<SharePointList> objectCollection = new ArrayList<>();

        for (ClientEntity listEntity : lists) {
            objectCollection.add(this.objectMapperService.buildManyWhoSharePointListObject(listEntity, siteId));
        }

        return objectCollection;
    }

    private List<MObject> responseDynamicTypes(List<ClientEntity> items, List<ObjectDataTypeProperty> properties) {
        List<MObject> objectCollection = new ArrayList<>();

        for (ClientEntity itemEntity : items) {
            objectCollection.add(objectMapperService.buildManyWhoDynamicObject(itemEntity.getNavigationLink("fields")
                    .asInlineEntity().getEntity().getProperties(), properties));
        }

        return objectCollection;
    }
}
