package com.example.empay.service.transaction.search;

import com.example.empay.controller.search.SearchOperation;
import com.example.empay.controller.search.SearchRequestException;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.entity.transaction.TransactionType;
import com.example.empay.service.search.SpecificationBase;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TransactionSpecification extends SpecificationBase<Transaction> {
    /**
     * Constant for the merchant ID property.
     */
    private static final String PROPERTY_MERCHANT_ID = "merchantId";
    /**
     * Constant for the merchant property.
     */
    private static final String PROPERTY_MERCHANT = "merchant";
    /**
     * Constant for the status ID property.
     */
    private static final String PROPERTY_STATUS_TYPE_ID = "status.id";
    /**
     * Constant for the status property.
     */
    private static final String PROPERTY_STATUS = "status";
    /**
     * Constant for the transaction type ID property.
     */
    private static final String PROPERTY_TYPE_ID = "type.id";
    /**
     * Constant for the transaction type property.
     */
    private static final String PROPERTY_TYPE = "type";

    /**
     * Create a {@code Predicate} instance using the provided arguments.
     *
     * @param root  must not be {@literal null}.
     * @param query must not be {@literal null}.
     * @param cb    must not be {@literal null}.
     * @return
     */
    @Override
    public Predicate toPredicate(final Root<Transaction> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(getSearchCriteria().getOperation());
        if (PROPERTY_MERCHANT_ID.equals(getSearchCriteria().getFilterKey())) {
            switch (searchOperation) {
                case EQUAL:
                    return cb.equal(root.get(PROPERTY_MERCHANT),
                            getEntityManager().getReference(Merchant.class, getSearchCriteria().getValue()));

                case NOT_EQUAL:
                    return cb.notEqual(root.get(PROPERTY_MERCHANT),
                            getEntityManager().getReference(Merchant.class, getSearchCriteria().getValue()));

                default:
                    throw new SearchRequestException(
                            String.format("Search operation [%s] on property [%s] is not supported.",
                                    getSearchCriteria().getOperation(), getSearchCriteria().getFilterKey()),
                            getSearchCriteria());


            }
        }

        if (PROPERTY_STATUS_TYPE_ID.equals(getSearchCriteria().getFilterKey())) {
            switch (searchOperation) {
                case EQUAL:
                    return cb.equal(root.get(PROPERTY_STATUS),
                            getEntityManager().getReference(TransactionStatusType.class,
                                    getSearchCriteria().getValue()));

                case NOT_EQUAL:
                    return cb.notEqual(root.get(PROPERTY_STATUS),
                            getEntityManager().getReference(TransactionStatusType.class,
                                    getSearchCriteria().getValue()));

                default:
                    throw new SearchRequestException(
                            String.format("Search operation [%s] on property [%s] is not supported.",
                                    getSearchCriteria().getOperation(), getSearchCriteria().getFilterKey()),
                            getSearchCriteria());

            }
        }

        if (PROPERTY_TYPE_ID.equals(getSearchCriteria().getFilterKey())) {
            switch (searchOperation) {
                case EQUAL:
                    return cb.equal(root.get(PROPERTY_TYPE),
                            getEntityManager().getReference(TransactionType.class, getSearchCriteria().getValue()));

                case NOT_EQUAL:
                    return cb.notEqual(root.get(PROPERTY_TYPE),
                            getEntityManager().getReference(TransactionType.class, getSearchCriteria().getValue()));

                default:
                    throw new SearchRequestException(
                            String.format("Search operation [%s] on property [%s] is not supported.",
                                    getSearchCriteria().getOperation(), getSearchCriteria().getFilterKey()),
                            getSearchCriteria());


            }
        }

        return super.toPredicate(root, query, cb);
    }
}
