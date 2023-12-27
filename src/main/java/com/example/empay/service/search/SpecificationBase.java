package com.example.empay.service.search;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchOperation;
import com.example.empay.controller.search.SearchRequestException;
import com.example.empay.util.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Specification used to create search query for a JPA entity.
 *
 * @param <T> The class of the JPA entity.
 */
public class SpecificationBase<T> implements Specification<T> {
    /**
     * The search criteria input parameters.
     */
    @Getter
    private SearchCriteria searchCriteria;
    /**
     * EntityManager instance.
     */
    @Getter

    private EntityManager entityManager;

    /**
     * Initialize this instance.
     *
     * @param em                The persistance context.
     * @param searchCriteriaArg Search criteria input parameters used to construct the query.
     */
    public void initialize(final EntityManager em, final SearchCriteria searchCriteriaArg) {
        this.entityManager = Objects.requireNonNull(em, "Argument [em] cannot be null.");
        this.searchCriteria = Objects.requireNonNull(searchCriteriaArg,
                "Argument [searchCriteriaArg] cannot be null");
    }

    /**
     * Create a {@code Predicate} instance using the provided arguments.
     *
     * @param root  must not be {@literal null}.
     * @param query must not be {@literal null}.
     * @param cb    must not be {@literal null}.
     * @return
     */
    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(searchCriteria.getOperation());
        Field field = getDeclaredField(root.getModel().getJavaType(), searchCriteria);
        Object parsedValue = parseValue(field, searchCriteria.getValue());
        Object parsedValue2 = parseValue(field, searchCriteria.getValue2());


        if (searchOperation == null) {
            throw new SearchRequestException(String.format("Search operation [%s] does not exist.",
                    searchCriteria.getOperation()), searchCriteria);
        }
        switch (searchOperation) {
            case EQUAL:
                return cb.equal(root.get(searchCriteria.getFilterKey()), parsedValue);
            case NOT_EQUAL:
                return cb.notEqual(root.get(searchCriteria.getFilterKey()), parsedValue);
            case CONTAINS:
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())),
                        "%" + searchCriteria.getValue() + "%");
            case DOES_NOT_CONTAIN:
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())),
                        "%" + searchCriteria.getValue() + "%");
            case BEGINS_WITH:
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())), searchCriteria.getValue() + "%");
            case DOES_NOT_BEGIN_WITH:
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())), searchCriteria.getValue() + "%");
            case ENDS_WITH:
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + searchCriteria.getValue());
            case DOES_NOT_END_WITH:
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + searchCriteria.getValue());
            case NUL:
                return cb.isNull(root.get(searchCriteria.getFilterKey()));
            case NOT_NULL:
                return cb.isNotNull(root.get(searchCriteria.getFilterKey()));
            case BETWEEN:
                return getBetweenPredicate(field, cb, root, parsedValue, parsedValue2, searchCriteria);
            case GREATER_THAN:
                return getGreaterThanPredicate(field, cb, root, parsedValue, searchCriteria);
            case GREATER_THAN_EQUAL:
                return getGreaterThanOrEqualToPredicate(field, cb, root, parsedValue, searchCriteria);

            case LESS_THAN:
                return getLessThanPredicate(field, cb, root, parsedValue, searchCriteria);

            case LESS_THAN_EQUAL:
                return getLessThanOrEqualToPredicate(field, cb, root, parsedValue, searchCriteria);
            default:
                throw new SearchRequestException(
                        String.format("Unsupported search operation [%s].", searchCriteria.getOperation()),
                        searchCriteria);
        }
    }

    protected static Object parseValue(final Field field, final Object value) {
        if (value == null) {
            return null;
        }

        String valueString = value.toString();
        Class fieldType = field.getType();
        if (ZonedDateTime.class.isAssignableFrom(fieldType)) {
            return ZonedDateTime.parse(valueString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .withZoneSameInstant(Constants.ZONE_ID_UTC);
        }
        if (Integer.class.isAssignableFrom(fieldType)) {
            return Integer.valueOf(valueString);
        }

        if (Long.class.isAssignableFrom(fieldType)) {
            return Long.valueOf(valueString);
        }

        if (BigDecimal.class.isAssignableFrom(fieldType)) {
            return new BigDecimal(valueString);
        }

        if (Boolean.class.isAssignableFrom(fieldType)) {
            return Boolean.valueOf(valueString);
        }

        if (UUID.class.isAssignableFrom(fieldType)) {
            return UUID.fromString(valueString);
        }

        return value;
    }

    protected static Field getDeclaredField(final Class entityClass, final SearchCriteria searchCriteria) {
        try {
            return entityClass.getDeclaredField(searchCriteria.getFilterKey());
        } catch (NoSuchFieldException e) {
            if (entityClass.getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
                try {
                    return entityClass.getSuperclass().getDeclaredField(searchCriteria.getFilterKey());
                } catch (NoSuchFieldException e2) {
                    throw new SearchRequestException(
                            String.format("Entity class [%s] does not have a declared field [%s]",
                                    entityClass.getSimpleName(), searchCriteria.getFilterKey()),
                            searchCriteria);
                }
            } else {
                throw new SearchRequestException(
                        String.format("Entity class [%s] does not have a declared field [%s]",
                                entityClass.getSimpleName(), searchCriteria.getFilterKey()),
                        searchCriteria);
            }
        }
    }

    protected static Predicate getBetweenPredicate(final Field field, final CriteriaBuilder cb, final Root root,
                                                   final Object parsedValue,
                                                   final Object parsedValue2, final SearchCriteria searchCriteria) {

        if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
            return cb.between(root.get(searchCriteria.getFilterKey()), (ZonedDateTime) parsedValue,
                    (ZonedDateTime) parsedValue2);
        }

        if (Integer.class.isAssignableFrom(field.getType())) {
            return cb.between(root.get(searchCriteria.getFilterKey()), (Integer) parsedValue,
                    (Integer) parsedValue2);
        }

        if (Long.class.isAssignableFrom(field.getType())) {
            return cb.between(root.get(searchCriteria.getFilterKey()), (Long) parsedValue,
                    (Long) parsedValue2);
        }

        if (Short.class.isAssignableFrom(field.getType())) {
            return cb.between(root.get(searchCriteria.getFilterKey()), (Short) parsedValue,
                    (Short) parsedValue2);
        }

        if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return cb.between(root.get(searchCriteria.getFilterKey()), (BigDecimal) parsedValue,
                    (BigDecimal) parsedValue2);
        }

        throw new SearchRequestException(
                String.format("Search operation [%s] on property [%s] of type [%s]"
                                + " is not supported.", searchCriteria.getOperation(),
                        field.getName(),
                        field.getType().getName()),
                searchCriteria);

    }

    protected static Predicate getGreaterThanPredicate(final Field field, final CriteriaBuilder cb, final Root root,
                                                       final Object parsedValue,
                                                       final SearchCriteria searchCriteria) {
        if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
            return cb.greaterThan(root.get(searchCriteria.getFilterKey()), (ZonedDateTime) parsedValue);
        }

        if (Integer.class.isAssignableFrom(field.getType())) {
            return cb.greaterThan(root.get(searchCriteria.getFilterKey()), (Integer) parsedValue);
        }

        if (Long.class.isAssignableFrom(field.getType())) {
            return cb.greaterThan(root.get(searchCriteria.getFilterKey()), (Long) parsedValue);
        }

        if (Short.class.isAssignableFrom(field.getType())) {
            return cb.greaterThan(root.get(searchCriteria.getFilterKey()), (Short) parsedValue);
        }

        if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return cb.greaterThan(root.get(searchCriteria.getFilterKey()), (BigDecimal) parsedValue);
        }

        throw new SearchRequestException(
                String.format("Search operation [%s] on property [%s] of type [%s]"
                                + " is not supported.", searchCriteria.getOperation(),
                        field.getName(),
                        field.getType().getName()),
                searchCriteria);
    }

    protected static Predicate getGreaterThanOrEqualToPredicate(final Field field, final CriteriaBuilder cb,
                                                                final Root root,
                                                                final Object parsedValue,
                                                                final SearchCriteria searchCriteria) {
        if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
            return cb.greaterThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (ZonedDateTime) parsedValue);
        }

        if (Integer.class.isAssignableFrom(field.getType())) {
            return cb.greaterThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Integer) parsedValue);
        }

        if (Long.class.isAssignableFrom(field.getType())) {
            return cb.greaterThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Long) parsedValue);
        }

        if (Short.class.isAssignableFrom(field.getType())) {
            return cb.greaterThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Short) parsedValue);
        }

        if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return cb.greaterThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (BigDecimal) parsedValue);
        }

        throw new SearchRequestException(
                String.format("Search operation [%s] on property [%s] of type [%s]"
                                + " is not supported.", searchCriteria.getOperation(),
                        field.getName(),
                        field.getType().getName()),
                searchCriteria);
    }

    protected static Predicate getLessThanPredicate(final Field field, final CriteriaBuilder cb, final Root root,
                                                    final Object parsedValue,
                                                    final SearchCriteria searchCriteria) {
        if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
            return cb.lessThan(root.get(searchCriteria.getFilterKey()), (ZonedDateTime) parsedValue);
        }

        if (Integer.class.isAssignableFrom(field.getType())) {
            return cb.lessThan(root.get(searchCriteria.getFilterKey()), (Integer) parsedValue);
        }

        if (Long.class.isAssignableFrom(field.getType())) {
            return cb.lessThan(root.get(searchCriteria.getFilterKey()), (Long) parsedValue);
        }

        if (Short.class.isAssignableFrom(field.getType())) {
            return cb.lessThan(root.get(searchCriteria.getFilterKey()), (Short) parsedValue);
        }

        if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return cb.lessThan(root.get(searchCriteria.getFilterKey()), (BigDecimal) parsedValue);
        }

        throw new SearchRequestException(
                String.format("Search operation [%s] on property [%s] of type [%s]"
                                + " is not supported.", searchCriteria.getOperation(),
                        field.getName(),
                        field.getType().getName()),
                searchCriteria);
    }

    protected static Predicate getLessThanOrEqualToPredicate(final Field field, final CriteriaBuilder cb,
                                                             final Root root,
                                                             final Object parsedValue,
                                                             final SearchCriteria searchCriteria) {
        if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
            return cb.lessThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (ZonedDateTime) parsedValue);
        }

        if (Integer.class.isAssignableFrom(field.getType())) {
            return cb.lessThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Integer) parsedValue);
        }

        if (Long.class.isAssignableFrom(field.getType())) {
            return cb.lessThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Long) parsedValue);
        }

        if (Short.class.isAssignableFrom(field.getType())) {
            return cb.lessThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (Short) parsedValue);
        }

        if (BigDecimal.class.isAssignableFrom(field.getType())) {
            return cb.lessThanOrEqualTo(root.get(searchCriteria.getFilterKey()), (BigDecimal) parsedValue);
        }

        throw new SearchRequestException(
                String.format("Search operation [%s] on property [%s] of type [%s]"
                                + " is not supported.", searchCriteria.getOperation(),
                        field.getName(),
                        field.getType().getName()),
                searchCriteria);
    }
}
