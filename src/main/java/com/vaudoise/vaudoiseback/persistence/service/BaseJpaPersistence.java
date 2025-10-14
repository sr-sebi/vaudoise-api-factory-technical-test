package com.vaudoise.vaudoiseback.persistence.service;

import com.vaudoise.vaudoiseback.config.logging.LogServiceMethod;
import com.vaudoise.vaudoiseback.persistence.repositories.BaseJpaRepository;
import com.vaudoise.vaudoiseback.persistence.specification.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Abstract class ready to query and write values from a JPA repository
 *
 * @param <R>  JPA Repository
 * @param <E>  JPA entity
 * @param <ID> Entity's identifier
 */
@SuppressWarnings("unchecked")
public abstract class BaseJpaPersistence<R extends BaseJpaRepository<E, ID>, E, ID> {
    /**
     * JPA repository instance
     */
    private final R repository;

    protected BaseJpaPersistence(R repository) {
        this.repository = repository;
    }

    /**
     * Returns a pagination ready page of entities which match with given query.
     *
     * @return Page of entities
     */
    @LogServiceMethod
    public Page<E> baseFindAll(String query, Pageable page) {
        if (!StringUtils.hasText(query)) return baseFindAll(page);
        Specification<E> spec = SpecificationUtils.buildSpecificationFromQuery(query, getJpaEntityClass());
        return repository.findAll(spec, page);
    }

    /**
     * Returns a pagination ready page of entities.
     *
     * @return Page of entities
     */
    @LogServiceMethod
    public Page<E> baseFindAll(Pageable page) {
        return repository.findAll(page);
    }

    /**
     * Returns used JPA entity class.
     *
     * @return Class<E>
     */
    public Class<E> getJpaEntityClass() {
        Type genericInterface = repository.getClass().getInterfaces()[0];
        return (Class<E>) ((ParameterizedType) ((Class<?>) genericInterface).getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}
