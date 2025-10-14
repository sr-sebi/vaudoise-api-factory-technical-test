package com.vaudoise.vaudoiseback.persistence.specification;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SearchCriteria implements Serializable {

    private String key;
    private SearchOperation operation;
    private Object value; // NOSONAR
    private boolean orPredicate;

    public SearchCriteria(final String orPredicate, final String key, final SearchOperation operation, final Object value) {
        super();
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

}

