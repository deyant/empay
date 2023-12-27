package com.example.empay.service.search;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchOperation;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SpecificationBuilder<T> {

    /**
     * List of {@link SearchCriteria} parameters.
     */
    private final List<SearchCriteria> params;
    /**
     * Persistence context.
     */
    private final EntityManager entityManager;
    /**
     * A supplier function for instances of {@link SpecificationBase<T>}.
     */
    private final Supplier<SpecificationBase<T>> specificationInstanceSupplier;

    /**
     * Sole constructor.
     *
     * @param specificationInstanceSupplier Supplier for specification instances.
     * @param entityManager                 The persistence context.
     */
    public SpecificationBuilder(final Supplier<SpecificationBase<T>> specificationInstanceSupplier,
                                final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.params = new ArrayList<>();
        this.specificationInstanceSupplier = specificationInstanceSupplier;
    }

    /**
     * Add a {@link SearchCriteria} parameter.
     *
     * @param searchCriteria The parameter.
     * @return {@literal this} object.
     */
    public final SpecificationBuilder with(final SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    /**
     * Build the instance.
     *
     * @return A new instance of {@code Specification<T>}.
     */
    public Specification<T> build() {

        if (params.size() == 0) {
            return null;
        }

        Specification<T> result = specificationInstanceSupplier.get();
        ((SpecificationBase<T>) result).initialize(entityManager, params.get(0));

        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            SpecificationBase<T> specification = specificationInstanceSupplier.get();

            specification.initialize(entityManager, criteria);
            result = SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
                    ? Specification.where(result).and(specification)
                    :
                    Specification.where(result).or(specification);
        }
        return result;
    }
}
