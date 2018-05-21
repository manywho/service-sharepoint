package com.manywho.services.sharepoint.types;

import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.uri.FilterFactoryImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class ODataFilter {

    URIFilter createUriFilter(ListFilter listFilter, String childPropertyPath) {
        FilterFactory filterFactory = new FilterFactoryImpl();

        List<URIFilter> uriFilters = listFilter.getWhere().stream()
                .map( where -> filterFromWhere(where, childPropertyPath, filterFactory))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (uriFilters.size() >= 1) {
            URIFilter uriFilter = uriFilters.get(0);
            for (int i = 1; i< uriFilters.size(); i++) {
                if (listFilter.getComparisonType() == ComparisonType.And) {
                    uriFilter = filterFactory.and(uriFilter, uriFilters.get(i));
                } else {
                    uriFilter = filterFactory.or(uriFilter, uriFilters.get(i));
                }
            }

            return uriFilter;
        }

        return null;
    }

    private URIFilter filterFromWhere(ListFilterWhere where, String propertyPath, FilterFactory filterFactory) {

        String childPropertyPath = String.format("%s/%s", propertyPath, where.getColumnName());
        // this is the only property that seems to work for SharePoint list items using odata
        // boolean fields and the id seems to be ignored

        switch (where.getCriteriaType()) {
            case Equal:
                return filterFactory.eq(childPropertyPath, where.getContentValue());
            default:
        }

        return null;
    }

}
