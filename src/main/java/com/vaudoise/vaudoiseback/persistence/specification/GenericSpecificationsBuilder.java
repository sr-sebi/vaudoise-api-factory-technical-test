package com.vaudoise.vaudoiseback.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GenericSpecificationsBuilder<U> {
    private final List<SearchCriteria> params;

    public GenericSpecificationsBuilder() {
        this.params = new ArrayList<>();
    }

    // API
    public final GenericSpecificationsBuilder<U> with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final GenericSpecificationsBuilder<U> with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        final SearchOperation op = getSearchOperation(operation, prefix, suffix);
        if (op != null) {
            params.add(new SearchCriteria(orPredicate, key, op, value));
        }
        return this;
    }

    public Specification<U> build() {
        if (params.isEmpty())
            return null;
        Specification<U> result = new SpecificationImpl<>(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new SpecificationImpl<>(params.get(i)))
                    : Specification.where(result).and(new SpecificationImpl<>(params.get(i)));
        }
        return result;
    }

    public final GenericSpecificationsBuilder<U> with(SpecificationImpl<U> spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public final GenericSpecificationsBuilder<U> with(SearchCriteria criteria) {
        params.add(criteria);
        return this;
    }

    private SearchOperation getSearchOperation(final String operation, final String prefix, final String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op == SearchOperation.EQUALITY) { // the operation may be complex operation
            final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
            final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

            if (startWithAsterisk && endWithAsterisk) {
                op = SearchOperation.CONTAINS;
            } else if (startWithAsterisk) {
                op = SearchOperation.ENDS_WITH;
            } else if (endWithAsterisk) {
                op = SearchOperation.STARTS_WITH;
            }
        }
        return op;
    }
}

