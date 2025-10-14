package com.vaudoise.vaudoiseback.persistence.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This utils is usable by all persistence services.
 */
public class SpecificationUtils {

    // non-instantiable class
    private SpecificationUtils() {
    }

    /**
     * Method ready to build a generic specification from a query and a given class for all his String fields.
     *
     * @param query  Query to be performed
     * @param tClass Class used to infer filterable fields
     * @param <T>    Class which specification belongs to
     * @return Search criteria as a Specification
     */
    public static <T> Specification<T> buildSpecificationFromQuery(final String query, final Class<T> tClass, String[] fields) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        String operationSetExper = String.join("|", SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(query + ",");

        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
        }

        Specification<T> spec = builder.build();

        if (spec == null) {
            List<String> fieldList;

            // Lista de campos de auditoría a excluir
            List<String> excludeFields = Arrays.asList("createdAt", "modifiedAt", "createdBy", "modifiedBy");

            if (fields == null || fields.length == 0) {
                fieldList = getAllFields(tClass)
                        .stream()
                        .filter(field -> !excludeFields.contains(field.getName()) &&
                                (Objects.equals(field.getType(), String.class) || Objects.equals(field.getType(), UUID.class)))
                        .map(Field::getName)
                        .collect(Collectors.toList());
            } else {
                fieldList = Arrays.stream(fields)
                        .filter(field -> !excludeFields.contains(field))
                        .collect(Collectors.toList());
            }

            fieldList.forEach(field ->
                    builder.with(SearchOperation.OR_PREDICATE_FLAG, field, ":", query, "*", "*")
            );
        }

        return builder.build();
    }


    public static <T> Specification<T> buildSpecificationFromQuery(final String query, final Class<T> tClass) {
        return buildSpecificationFromQuery(query, tClass, null);
    }


    /**
     * Method ready to build a generic specification to match entity id whith a list of ids
     *
     * @param ids ids to filter
     * @return Search criteria as a Specification
     */
    public static <T> Specification<T> buildSpecificationEqualsToAnyId(final String field, List<Long> ids) {
        return (root, query, builder) -> builder.or(builder.in(root.get(field)).value(ids));
    }

    /**
     * Method ready to build a generic specification from a query given a specific field (like operation)
     *
     * @param field field to be filtered
     * @param text  Query to be performed
     * @param <T>   Class which specification belongs to
     * @return Search criteria as a Specification
     */
    public static <T> Specification<T> buildSpecificationFromQueryLike(final String field, final String text) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // like
            predicates.add(builder.like(root.get(field).as(String.class), "%" + text + "%"));
            // AND all predicates
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Returns a list of all fields retrieving recursively fields from superclasses.
     *
     * @param fields List to start adding new fields
     * @param type   Class where fields are declared
     * @return List of all fields (own and inherited fields)
     */
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    /**
     * Overloaded method to add an empty list by default.
     *
     * @param type Class where fields are declared
     * @return List of all fields (own and inherited fields)
     */
    private static List<Field> getAllFields(Class<?> type) {
        return getAllFields(new LinkedList<>(), type);
    }
}
