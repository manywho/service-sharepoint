package com.manywho.services.sharepoint.types;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sharepoint.configuration.ApiConstants;
import com.manywho.services.sharepoint.client.GraphClient;
import com.manywho.services.sharepoint.client.OdataPaginator;
import com.manywho.services.sharepoint.sites.SiteClient;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.manywho.services.sharepoint.configuration.ApiConstants.GRAPH_ENDPOINT_V1;

public class DynamicTypesOdataClient {

    private final ODataClient client;
    private GraphClient graphClient;
    private SiteClient siteClient;
    private ODataFilter oDataFilter;

    @Inject
    public DynamicTypesOdataClient(ODataClient client, SiteClient siteClient, ODataFilter oDataFilter) {
        this.client = client;
        this.graphClient = new GraphClient(client);
        this.siteClient = siteClient;
        this.oDataFilter = oDataFilter;
    }

    public List<TypeElement> fetchAllListTypes(String token) {
        URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment("groups?$filter=groupTypes/any(c:c+eq+'Unified')")
                .build();

        List<ClientEntity> listGroups = graphClient.queryList(token, uri);
        Flowable<ClientEntity> batches = Flowable.fromIterable(listGroups);

        List<TypeElement> elementsByGroup = batches.parallel()
                .runOn(Schedulers.computation())
                .map(group -> fetchTypeElementsByGroup(token, group))
                .flatMap(Flowable::fromIterable)
                .sequential()
                .toList()
                .blockingGet();

        return new ArrayList<>(elementsByGroup);
    }

    private List<TypeElement> fetchTypeElementsByGroup(String token, ClientEntity group){
        List<TypeElement> typeElements = new ArrayList<>();
        List<ClientEntity> listSitesByGroups = new ArrayList<>();

        ClientEntity siteGroup = siteClient.fetchSiteByGroup(token, group.getProperty("id").getValue().toString());
        listSitesByGroups.add(siteGroup);

        for (ClientEntity site : listSitesByGroups) {
            String siteName = site.getProperty("name").getValue().toString();
            String siteId = site.getProperty("id").getValue().toString();
            String urlEntity = String.format("sites/%s/lists", siteId);
            URI uri = client.newURIBuilder(GRAPH_ENDPOINT_V1).appendEntitySetSegment(urlEntity).build();
            List<ClientEntity> lists = graphClient.queryList(token, uri);

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
        ResourceMetadata resourceMetadata = new ResourceMetadata(list, siteId, listName, siteName);

        TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                .setDeveloperName(typeDeveloperName)
                .setTableName(resourceMetadata.getMetadata());

        URI uri = client.newURIBuilder(list.getNavigationLink("columns").getLink().toString()).build();
        List<ClientEntity> properties = graphClient.queryList(token, uri);

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
                typeBuilder.addProperty("ID", ContentType.String, "ID");
            }

        }

        TypeElement typeElement = typeBuilder.build();
        typeElement.setDeveloperSummary(typeDeveloperSummary);

        return typeElement;
    }

    public List<MObject> fetchTypesFromLists(String token, ResourceMetadata resourceMetadata,
                                             List<ObjectDataTypeProperty> properties, ListFilter listFilter) {

        OdataPaginator odataPaginator = new OdataPaginator();

        String entryPoint = String.format("%s/items", resourceMetadata.getResource());

        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        URIBuilder uriBuilder = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(entryPoint);
        if (listFilter.hasSearch()) {
                uriBuilder.search(listFilter.getSearch());
        }

        URIFilter uriFilter = oDataFilter.createUriFilter(listFilter,"fields");
        if (uriFilter != null) {
            uriBuilder.filter(uriFilter);
        }

        uriBuilder.expand("fields");
        //order by was not supported when the OData filter was added

        List<ClientEntity> clientEntities = odataPaginator.getEntities(token, uriBuilder, listFilter, client.getRetrieveRequestFactory());

        return responseDynamicTypes(clientEntities, properties, resourceMetadata);
    }

    public MObject fetchTypeFromList(String token, ResourceMetadata resourceMetadata,
                                     String itemId, List<ObjectDataTypeProperty> properties) {

        URI entitySetURI = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(resourceMetadata.getResource())
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(itemId)
                .expand("fields")
                .build();

        ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory().getEntityRequest(entitySetURI);
        entityRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

        List<ClientEntity> items = new ArrayList<>();
        items.add(0, entityResponse.getBody());

        return responseDynamicTypes(items, properties, resourceMetadata).get(0);
    }

    public MObject updateTypeList(String token, ResourceMetadata resourceMetadata, List<Property> properties, String id) {

        URI itemUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(resourceMetadata.getResource())
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(id)
                .appendEntitySetSegment("fields")
                .build();

        ClientEntity clientEntity = getClientEntityModification(properties);

        ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory()
                .getEntityUpdateRequest(itemUri, UpdateType.PATCH, clientEntity);

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
            return fetchTypeFromList(token, resourceMetadata, id, propertyCollection);
        } else {
            throw new RuntimeException(String.format("Error updating type :%s", response.getStatusCode()));
        }
    }

    public void deleteTypeList(String token, ResourceMetadata resourceMetadata, String id) {
        URI deleteItemUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1)
                .appendEntitySetSegment(resourceMetadata.getResource())
                .appendEntitySetSegment("items")
                .appendEntitySetSegment(id)
                .build();

        graphClient.executeDelete(token, deleteItemUri);
    }

    public MObject createTypeList(String token, MObject object) {
        ResourceMetadata resourceMetadata = new ResourceMetadata(object.getDeveloperName());
        URI itemUri = client.newURIBuilder(ApiConstants.GRAPH_ENDPOINT_V1).appendEntitySetSegment(resourceMetadata.getResource())
                .appendEntitySetSegment("items")
                .build();

        ClientEntity clientEntity = getClientEntityCreation(object.getProperties());

        ODataEntityCreateRequest<ClientEntity> req = client.getCUDRequestFactory().getEntityCreateRequest(itemUri, clientEntity);

        req.addCustomHeader("Authorization", String.format("Bearer %s", token));
        req.addCustomHeader("Content-Type", "application/json");

        ODataEntityCreateResponse<ClientEntity> response = req.execute();

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {

            List<ObjectDataTypeProperty> propertyCollection  = PropertiesUtils.mapToObjectProperty(object.getProperties());
            String itemId = response.getBody().getProperty("id").getValue().toString();

            return fetchTypeFromList(token, resourceMetadata, itemId, propertyCollection);
        } else {
            throw new RuntimeException(String.format("Error inserting type %s (%s)", object.getDeveloperName(), response.getStatusCode()));
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

    private List<MObject> responseDynamicTypes(List<ClientEntity> items, List<ObjectDataTypeProperty> properties, ResourceMetadata resourceMetadata) {
        List<MObject> objectCollection = new ArrayList<>();

        DynamicTypesMapper dynamicTypesMapper = new DynamicTypesMapper();

        for (ClientEntity itemEntity : items) {
            String uniqueItemId = String.format("%s/items/%s", resourceMetadata.getResource(),
                    itemEntity.getProperty("id").getValue().toString());

            objectCollection.add(
                    dynamicTypesMapper.buildManyWhoDynamicObject(uniqueItemId, itemEntity.getNavigationLink("fields")
                    .asInlineEntity().getEntity().getProperties(), properties));
        }

        return objectCollection;
    }
}
