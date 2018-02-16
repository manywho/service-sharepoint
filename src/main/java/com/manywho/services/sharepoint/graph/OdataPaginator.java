package com.manywho.services.sharepoint.graph;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.uri.URIBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class OdataPaginator {

    public List<ClientEntity> getEntities(String token, URIBuilder uriBuilder, ListFilter listFilter, RetrieveRequestFactory retrieveRequestFactory) {
        int limit = 11;

        if (listFilter.hasOffset()) {
            uriBuilder.top(listFilter.getOffset());

        } else if (listFilter.hasLimit()){
            uriBuilder.top(listFilter.getLimit());
            // there is not offset, so return the first page with "limit" elements
            return fetchEntitiesOfAPage(fetchPage(token, uriBuilder.build(), retrieveRequestFactory));
        }

        if (listFilter.hasLimit()) {
            limit = listFilter.getLimit();
        }

        // grab second page

        ClientEntitySetIterator<ClientEntitySet, ClientEntity> body = fetchPage(token, uriBuilder.build(), retrieveRequestFactory);
        // I need to read all entities of the first page before grab the next page url with getNext()
        fetchEntitiesOfAPage(body);

        ArrayList<ClientEntity> entities = new ArrayList<>();
        URI nextUri = body.getNext();

        if (nextUri == null) {
            return entities;
        }

        body = fetchPage(token, body.getNext(), retrieveRequestFactory);

        int count_entities = 0;
        while (body.hasNext() && count_entities < limit) {
            count_entities++;
            entities.add(body.next());
        }

        count_entities = entities.size();

        while (count_entities < limit) {
            nextUri = body.getNext();

            if (nextUri == null) {
                return entities;
            }
            // add nex entities from next pages until get limit
            body = fetchPage(token, body.getNext(), retrieveRequestFactory);
            while (body.hasNext() && count_entities < limit) {
                count_entities++;
                entities.add(body.next());
            }
        }

        return entities;
    }

    private ClientEntitySetIterator<ClientEntitySet, ClientEntity> fetchPage(String token, URI uri, RetrieveRequestFactory retrieveRequestFactory) {
        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> entitySetRequest = retrieveRequestFactory.getEntitySetIteratorRequest(uri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> execution = entitySetRequest.execute();
        return execution.getBody();
    }

    private ArrayList<ClientEntity> fetchEntitiesOfAPage(ClientEntitySetIterator<ClientEntitySet, ClientEntity> body) {
        ArrayList<ClientEntity> entities = new ArrayList<>();

        while (body.hasNext()) {
            entities.add(body.next());
        }

        return entities;
    }
}
