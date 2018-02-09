package com.manywho.services.sharepoint.facades;

import com.google.common.base.Strings;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ServiceConfiguration;
import com.manywho.services.sharepoint.constants.ApiConstants;
import com.manywho.services.sharepoint.mapper.ObjectMapperService;
import com.manywho.services.sharepoint.types.*;
import com.manywho.services.sharepoint.utilities.IdExtractorForDynamicTypes;
import com.manywho.services.sharepoint.utilities.IdExtractorForLists;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharePointOdataFacade implements SharePointFacadeInterface {

    private ObjectMapperService objectMapperService;
    private final ODataClient client;
    private final RetrieveRequestFactory retrieveRequestFactory;
    private final CUDRequestFactory cudRequestFactory;
    private final OdataPaginator odataPaginator;

    @Inject
    public SharePointOdataFacade(ObjectMapperService objectMapperService, OdataPaginator odataPaginator) {
        this.objectMapperService = objectMapperService;
        client = ODataClientFactory.getClient();
        cudRequestFactory = client.getCUDRequestFactory();
        retrieveRequestFactory = client.getRetrieveRequestFactory();
        this.odataPaginator = odataPaginator;
    }

    @Override
    public List<Group> fetchGroups(ServiceConfiguration configuration, String token, ListFilter listFilter) {
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "groups?$filter=groupTypes/any(c:c+eq+'Unified')");
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();

        List<Group> objectCollection = new ArrayList<>();

        for (ClientEntity groupEntity : listGroups) {
            objectCollection.add(this.objectMapperService.buildManyWhoGroupObject(groupEntity));
        }

        return objectCollection;
    }

    @Override
    public List<User> fetchUsers(ServiceConfiguration configuration, String token, ListFilter listFilter) {
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "users");
        List<ClientEntity> listUsers = sitesEntitySetResponse.getBody().getEntities();

        List<User> objectCollection = new ArrayList<>();

        for (ClientEntity userEntity : listUsers) {
            objectCollection.add(this.objectMapperService.buildManyWhoUserObject(userEntity));
        }

        return objectCollection;
    }

    @Override
    public List<Site> fetchSites(ServiceConfiguration configuration, String token, String groupId) {
        //this line should work but it doesn't
        //ODataRetrieveResponse<ODataEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(getToken, "sites/root/sites");
        // we fetch groups
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, "groups?$filter=groupTypes/any(c:c+eq+'Unified')");
        List<ClientEntity> listGroups = sitesEntitySetResponse.getBody().getEntities();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        // we get the sites for each group
        for (ClientEntity group : listGroups) {
            if (Strings.isNullOrEmpty(groupId) || groupId.equals(group.getProperty("id").getValue().toString())) {
                List<ClientEntity> sites = fetchSiteByGroup(configuration, token, group.getProperty("id").getValue().toString());
                listSitesByGroups.addAll(sites);
            }
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
    public List<Site> fetchSubsites(ServiceConfiguration configuration, String token, String parentId) {
        String url = String.format("sites/%s/sites", parentId);
        ODataRetrieveResponse<ClientEntitySet> sitesEntitySetResponse = getEntitiesSetResponse(token, url);

        return responseSites(sitesEntitySetResponse.getBody().getEntities(), parentId);
    }

    @Override
    public Site fetchSite(ServiceConfiguration configuration, String token, String id) {
        String urlEntity = String.format("sites/%s", id);
        List<ClientEntity> sites = new ArrayList<>();
        sites.add(0, getEntitySetResponse(token, urlEntity).getBody());

        return responseSites(sites, "").get(0);
    }

    @Override
    public List<SharePointList> fetchLists(ServiceConfiguration configuration, String token, String idSite, boolean fullType) {
        String urlEntity = String.format("sites/%s/lists", idSite);
        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseLists(entitySetResponse.getBody().getEntities(), idSite, fullType);
    }

    @Override
    public List<TypeElement> fetchAllListTypes(ServiceConfiguration configuration, String token) {
        List<TypeElement>  typeElements = new ArrayList<>();
        String groupFilter= "groups?$filter=groupTypes/any(c:c+eq+'Unified')";

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

    private List<TypeElement> fetchTypeElementsByGroup(ServiceConfiguration configuration, String token, ClientEntity group){
        List<TypeElement> typeElements = new ArrayList<>();
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
        ODataRetrieveResponse<ClientEntitySet> oDataRetrieveResponse = getEntitiesSetResponse(token, list.getNavigationLink("columns")
                .getLink().toString().replace(ApiConstants.GRAPH_ENDPOINT_BETA, ""));
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
    public SharePointList fetchList(ServiceConfiguration configuration, String token, String idSite, String idList) {
        String entryPoint = String.format("sites/%s/lists/%s", idSite, idList);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);
        List<ClientEntity> lists = new ArrayList<>();
        lists.add(0, entitySetResponse.getBody());

        return responseLists(lists, idSite, false).get(0);
    }

    @Override
    public List<SharePointList> fetchListsRoot(ServiceConfiguration configuration, String token) {
        return responseLists(getEntitiesSetResponse(token, "sites/root/lists").getBody().getEntities(), "", false);
    }

    @Override
    public SharePointListItem fetchItem(ServiceConfiguration configuration, String token, String siteId, String listId, String itemId) {
        String entryPoint = String.format("sites/%s/lists/%s/items/%s", siteId, listId, itemId);
        ODataRetrieveResponse<ClientEntity> entitySetResponse = getEntitySetResponse(token, entryPoint);

        List<ClientEntity> items = new ArrayList<>();
        items.add(0, entitySetResponse.getBody());

        return responseItems(items, siteId, listId).get(0);
    }

    @Override
    public List<SharePointListItem> fetchItems(ServiceConfiguration configuration, String token, String listIdUnique) {
        String siteId = IdExtractorForLists.extractSiteId(listIdUnique);
        String listId = IdExtractorForLists.extractListId(listIdUnique);
        String urlEntity = String.format("sites/%s/lists/%s/items", siteId, listId);

        ODataRetrieveResponse<ClientEntitySet> entitySetResponse = getEntitiesSetResponse(token, urlEntity);

        return responseItems(entitySetResponse.getBody().getEntities(), siteId, listId);
    }

    @Override
    public List<MObject> fetchTypesFromLists(ServiceConfiguration configuration, String token, String developerName,
                                             List<ObjectDataTypeProperty> properties, ListFilter listFilter) {

        String entryPoint = String.format("%s/items", developerName);
        URIBuilder uriBuilder = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA)
                .appendEntitySetSegment(entryPoint)
                .expand("fields");

        List<ClientEntity> clientEntities = odataPaginator.getEntities(token, uriBuilder, listFilter, retrieveRequestFactory);

        return responseDynamicTypes(clientEntities, properties);
    }


    @Override
    public MObject fetchTypeFromList(ServiceConfiguration configuration, String token, String developerName,
                                     String itemId, List<ObjectDataTypeProperty> properties) {

        URI entitySetURI = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA)
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
    public MObject updateTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties, String id) {

        URI itemUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA).appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(id)
                .appendEntitySetSegment("fields")
                .build();

        ClientEntity clientEntity = getClientEntityModification(properties);
        ODataEntityUpdateRequest<ClientEntity> request = cudRequestFactory.getEntityUpdateRequest(itemUri, UpdateType.PATCH, clientEntity);

        request.addCustomHeader("Authorization", String.format("Bearer %s", token));
        request.addCustomHeader("Content-Type", "application/json");

        ODataEntityUpdateResponse<ClientEntity> response = request.execute();

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            List<ObjectDataTypeProperty> propertyCollection = new ArrayList<>();
            properties.forEach(p -> {
                ObjectDataTypeProperty prop = new ObjectDataTypeProperty();
                prop.setDeveloperName(p.getDeveloperName());
                propertyCollection.add(prop);
            });
            return fetchTypeFromList(configuration, token, developerName, id, propertyCollection);
        } else {
            throw new RuntimeException(String.format("Error updating type :%s", response.getStatusCode()));
        }
    }

    @Override
    public MObject createTypeList(ServiceConfiguration configuration, String token, String developerName, List<Property> properties) {
        URI itemUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA).appendEntitySetSegment(developerName)
                .appendEntitySetSegment("items")
                .build();

        ClientEntity clientEntity = getClientEntityCreation(properties);

        ODataEntityCreateRequest<ClientEntity> req = cudRequestFactory.getEntityCreateRequest(itemUri, clientEntity);

        req.addCustomHeader("Authorization", String.format("Bearer %s", token));
        req.addCustomHeader("Content-Type", "application/json");

        ODataEntityCreateResponse<ClientEntity> response = req.execute();

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            List<ObjectDataTypeProperty> propertyCollection = new ArrayList<>();
            properties.forEach(p -> {
                ObjectDataTypeProperty prop = new ObjectDataTypeProperty();
                prop.setDeveloperName(p.getDeveloperName());
                propertyCollection.add(prop);
            });
            String itemId = IdExtractorForDynamicTypes.extractItemId(response.getBody().getId().toString());

            return fetchTypeFromList(configuration, token, developerName, itemId, propertyCollection);
        } else {
            throw new RuntimeException(String.format("Error updating type :%s", response.getStatusCode()));
        }
    }

    private ClientEntity getClientEntityCreation(List<Property> properties){
        ClientEntity clientEntity = client.getObjectFactory().newEntity(null);

        ClientComplexValue complexValue = client.getObjectFactory().newComplexValue(null);

        for (Property property: properties) {
            if (Objects.equals(property.getDeveloperName(), "ID")) {
                continue;
            }
            switch(property.getContentType()){
                case Boolean:
                    boolean propertyValue = !Strings.isNullOrEmpty(property.getContentValue()) && Objects.equals(property.getContentValue().toLowerCase(), "true");
                    complexValue
                            .add(client.getObjectFactory().newPrimitiveProperty(property.getDeveloperName(),
                                    client.getObjectFactory().newPrimitiveValueBuilder().buildBoolean(propertyValue)));
                    break;
                default:
                    complexValue
                            .add(client.getObjectFactory().newPrimitiveProperty(property.getDeveloperName(),
                                    client.getObjectFactory().newPrimitiveValueBuilder().buildString(property.getContentValue())));
                    break;
            }
        }

        clientEntity.getProperties().add(client.getObjectFactory().newComplexProperty("fields", complexValue));

        return clientEntity;
    }

    private ClientEntity getClientEntityModification(List<Property> properties){
        ClientEntity clientEntity = client.getObjectFactory().newEntity(new FullQualifiedName("microsoft.graph", "fieldValueSet"));

        for (Property property: properties) {
            if (Objects.equals(property.getDeveloperName(), "ID")) {
                continue;
            }
            switch(property.getContentType()){
                case Boolean:
                    boolean propertyValue = !Strings.isNullOrEmpty(property.getContentValue()) && Objects.equals(property.getContentValue().toLowerCase(), "true");
                    clientEntity.getProperties()
                            .add(client.getObjectFactory().newPrimitiveProperty(property.getDeveloperName(),
                                    client.getObjectFactory().newPrimitiveValueBuilder().buildBoolean(propertyValue)));
                    break;
                default:
                    clientEntity.getProperties()
                            .add(client.getObjectFactory().newPrimitiveProperty(property.getDeveloperName(),
                                    client.getObjectFactory().newPrimitiveValueBuilder().buildString(property.getContentValue())));
                    break;
            }
        }

        return clientEntity;
    }

    @Override
    public String getUserId(ServiceConfiguration configuration, String token) {
        URI uri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA).appendEntitySetSegment("me").build();

        ODataEntityRequest<ClientEntity> entitySetRequest = retrieveRequestFactory.getEntityRequest(uri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        return entitySetRequest.execute().getBody().getProperty("id").getValue().toString();
    }

    private ODataRetrieveResponse<ClientEntitySet> getEntitiesSetResponse(String token, String urlEntity) {
        URI entitySetURI = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA).appendEntitySetSegment(urlEntity).build();

        ODataEntitySetRequest<ClientEntitySet> entitySetRequest = retrieveRequestFactory.getEntitySetRequest(entitySetURI);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));

        return entitySetRequest.execute();
    }

    private ODataRetrieveResponse<ClientEntity> getEntitySetResponse(String token, String entryPoint) {
        URI entityUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_BETA).appendEntitySetSegment(entryPoint).build();
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

    private List<SharePointListItem> responseItems(List<ClientEntity> sites, String siteId, String listId) {
        List<SharePointListItem> objectCollection = new ArrayList<>();

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
            objectCollection.add(objectMapperService.buildManyWhoDynamicObject(itemEntity.getId().toString(), itemEntity.getNavigationLink("fields")
                    .asInlineEntity().getEntity().getProperties(), properties));
        }

        return objectCollection;
    }
}
