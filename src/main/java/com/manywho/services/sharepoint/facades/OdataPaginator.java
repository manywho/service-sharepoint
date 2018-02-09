package com.manywho.services.sharepoint.facades;

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
        int top = 0;
        int limit = 10;

        if (listFilter.hasOffset()) {
            top = listFilter.getOffset();
            uriBuilder.top(top);

        } else if (listFilter.hasLimit()){
            uriBuilder.top(listFilter.getLimit());
            // there is not offset, so return the first page with "limit" elements
            return pageEntities(getResponsePage(token, uriBuilder.build(), retrieveRequestFactory));
        }

        if (listFilter.hasLimit() == false) {
            limit = 10;
        } else {
            limit = listFilter.getLimit();
        }

        // grab second page

        ClientEntitySetIterator<ClientEntitySet, ClientEntity> body = getResponsePage(token, uriBuilder.build(), retrieveRequestFactory);
        // I need to read all entities of the first page before grab the next page url with getNext()
        pageEntities(body);

        ArrayList<ClientEntity> entities = new ArrayList<>();
        body = getResponsePage(token, body.getNext(), retrieveRequestFactory);

        int count_entities = 0;
        while (body.hasNext() && count_entities < listFilter.getLimit()) {
            count_entities++;
            entities.add(body.next());
        }

        // in this case I show all elements that are in second page
        if (top >= listFilter.getLimit()) {
            return entities;
        }

        // return limit elements that are in second page plus complete with next pages

        while (count_entities < limit) {
            // add nex entities from next pages until get limit
            body = getResponsePage(token, body.getNext(), retrieveRequestFactory);
            while (body.hasNext() && count_entities < limit) {
                count_entities++;
                entities.add(body.next());
            }
        }

        return entities;
    }

    private ClientEntitySetIterator<ClientEntitySet, ClientEntity> getResponsePage(String token, URI uri, RetrieveRequestFactory retrieveRequestFactory) {
        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> entitySetRequest = retrieveRequestFactory.getEntitySetIteratorRequest(uri);
        entitySetRequest.addCustomHeader("Authorization", String.format("Bearer %s", token));
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> execution = entitySetRequest.execute();
        return execution.getBody();
    }

    private ArrayList<ClientEntity> pageEntities(ClientEntitySetIterator<ClientEntitySet, ClientEntity> body) {
        ArrayList<ClientEntity> entities = new ArrayList<>();
        while (body.hasNext()) {
            entities.add(body.next());
        }

        return entities;
    }
}
