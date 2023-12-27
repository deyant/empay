package com.example.empay.service.merchant.search;

import com.example.empay.controller.search.SearchOperation;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantIdentifierType;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.exception.SearchException;
import com.example.empay.service.search.SpecificationBase;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MerchantSpecification extends SpecificationBase<Merchant> {
    /**
     * Constant for the identifier type ID property.
     */
    private static final String PROPERTY_IDENTIFIER_TYPE_ID = "identifierType.id";
    /**
     * Constant for the identifier type property.
     */
    private static final String PROPERTY_IDENTIFIER_TYPE = "identifierType";
    /**
     * Constant for the status type ID property.
     */
    private static final String PROPERTY_STATUS_TYPE_ID = "status.id";
    /**
     * Constant for the status ID property.
     */
    private static final String PROPERTY_STATUS = "status";

    /**
     * Create a {@code Predicate} instance using the provided arguments.
     *
     * @param root  must not be {@literal null}.
     * @param query must not be {@literal null}.
     * @param cb    must not be {@literal null}.
     * @return
     */
    @Override
    public Predicate toPredicate(final Root<Merchant> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(getSearchCriteria().getOperation());
        if (PROPERTY_IDENTIFIER_TYPE_ID.equals(getSearchCriteria().getFilterKey())) {
            switch (searchOperation) {
                case EQUAL:
                    return cb.equal(root.get(PROPERTY_IDENTIFIER_TYPE),
                            getEntityManager().getReference(MerchantIdentifierType.class,
                                    getSearchCriteria().getValue()));

                case NOT_EQUAL:
                    return cb.notEqual(root.get(PROPERTY_IDENTIFIER_TYPE),
                            getEntityManager().getReference(MerchantIdentifierType.class,
                                    getSearchCriteria().getValue()));

                default:
                    throw new SearchException(String.format("Search operation [%s] is not supported for "
                            + "property [%s]", getSearchCriteria().getOperation(), getSearchCriteria().getFilterKey()));

            }
        }

        if (PROPERTY_STATUS_TYPE_ID.equals(getSearchCriteria().getFilterKey())) {
            switch (searchOperation) {
                case EQUAL:
                    return cb.equal(root.get(PROPERTY_STATUS), getEntityManager().getReference(MerchantStatusType.class,
                            getSearchCriteria().getValue()));

                case NOT_EQUAL:
                    return cb.notEqual(root.get(PROPERTY_STATUS),
                            getEntityManager().getReference(MerchantStatusType.class,
                                    getSearchCriteria().getValue()));

                default:
                    throw new SearchException(String.format("Search operation [%s] is not supported for "
                            + "property [%s]", getSearchCriteria().getOperation(), getSearchCriteria().getFilterKey()));

            }
        }

        return super.toPredicate(root, query, cb);
    }
}
