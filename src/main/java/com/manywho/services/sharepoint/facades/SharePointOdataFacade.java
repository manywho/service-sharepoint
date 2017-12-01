package com.manywho.services.sharepoint.facades;

import com.google.common.base.Strings;
import com.manywho.sdk.entities.draw.elements.type.TypeElement;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.services.DynamicTypesService;
import com.manywho.services.sharepoint.services.ObjectMapperService;
import com.manywho.services.sharepoint.services.file.FileSharePointService;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientValue;
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
        String urlEntity = String.format("groups/%s/sites/root", groupId);
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
    public TypeElementCollection fetchAllListTypes(ServiceConfiguration configuration, String token) {
        TypeElementCollection typeElements = new TypeElementCollection();
        String groupFilter= String.format("groups?$filter=groupTypes/any(c:c+eq+'Unified')");

        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, groupFilter);
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();

        Flowable<ClientEntity> batches = Flowable.fromIterable(listGroups);

        List<TypeElement> typeElements1 = batches.parallel()
                .runOn(Schedulers.computation())
                .map(group -> fetchTypeElementsByGroup(configuration, token, group))
                .flatMap(Flowable::fromIterable)
                .sequential()
                .toList()
                .blockingGet();


        typeElements.addAll(typeElements1);

        return typeElements;
    }

    private TypeElementCollection fetchTypeElementsByGroup(ServiceConfiguration configuration, String token, ClientEntity group){
        TypeElementCollection typeElements = new TypeElementCollection();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        List<ClientEntity> sites = fetchSiteByGroup(configuration, token, group.getProperty("id").getValue().toString());
        listSitesByGroups.addAll(sites);

        for (ClientEntity site : listSitesByGroups) {
            String siteName = site.getProperty("name").getValue().toString();
            String siteId = site.getProperty("id").getValue().toString();
            String urlEntity = String.format("sites/%s/lists", siteId);
            ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);
            List<ClientEntity> lists = entitySetResponse.getBody().getEntities();

            for (ClientEntity list : lists) {
                ClientValue infoList = list.getProperty("list").getValue();
                String displayName = infoList.asComplex().get("template").getValue().asPrimitive().toString();

                if (Objects.equals("genericList", displayName)) {
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

                typeBuilder.addProperty("ID", ContentType.String,  "ID");
            }

        }

        TypeElement typeElement = typeBuilder.build();
        typeElement.setDeveloperSummary(typeDeveloperSummary);

        return typeElement;
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
        fileSharePointService.uploadSmallFile(token, path, bodyPart);
        return new ObjectDataResponse();
    }

    @Override
    public ObjectDataResponse fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName, ObjectDataTypePropertyCollection properties) {
        String entryPoint = String.format("%s/items", developerName);
        URI entitySetURI = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(entryPoint).expand("fields").build();
        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = entitySetRequest.execute();

        return responseDynamicTypes(entitySetResponse.getBody().getEntities(), properties);
    }

    @Override
    public ObjectDataResponse fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName, String itemId, ObjectDataTypePropertyCollection properties) {
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

        return responseDynamicTypes(items, properties);
    }

    @Override
    public ObjectDataResponse saveTypeList(ServiceConfiguration configuration, String token, String developerName, PropertyCollection properties) {
        String itemId = null;

        if (Strings.isNullOrEmpty(properties.getContentValue("ID"))) {
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
        } else {
            itemId = properties.getContentValue("ID");

            URI itemUri = client.newURIBuilder(GRAPH_ENDPOINT).appendEntitySetSegment(developerName)
                    .appendEntitySetSegment("items")
                    .appendEntitySetSegment(itemId)
                    .appendEntitySetSegment("fields")
                    .build();

            DynamicTypesService.patchDynamicType(token, itemUri.toString(), properties);
        }

        ObjectDataTypePropertyCollection propertyCollection = new ObjectDataTypePropertyCollection();
        properties.forEach(p->{
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

    private ObjectDataResponse responseDynamicTypes(List<ClientEntity> items, ObjectDataTypePropertyCollection properties) {
        ObjectCollection objectCollection = new ObjectCollection();

        for (ClientEntity itemEntity : items) {
            objectCollection.add(this.objectMapperService.buildManyWhoDynamicObject(itemEntity.getNavigationLink("fields").asInlineEntity().getEntity().getProperties(), properties));
        }

        return new ObjectDataResponse(objectCollection);
    }
}
