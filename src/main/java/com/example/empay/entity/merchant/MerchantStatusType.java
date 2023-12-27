package com.example.empay.entity.merchant;

import com.example.empay.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * Reference (nomenclature) entity for status types of a merchant.
 */
@Getter
@Setter
@Entity
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"})
public class MerchantStatusType {

    /**
     * Possible status IDs.
     */
    public enum STATUS {
        /**
         * Active status.
         */
        ACTIVE,
        /**
         * Inactive status.
         */
        INACTIVE
    }

    /**
     * Entity ID.
     */
    @Id
    @NotBlank
    @NotNull
    @Size(min = 2, max = Constants.LENGTH_NOMENCLATURE_ID)
    @Column(length = Constants.LENGTH_NOMENCLATURE_ID)
    private String id;

    /**
     * Displayable name of the status.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NOMENCLATURE_NAME)
    @Column(length = Constants.LENGTH_NOMENCLATURE_NAME)
    private String name;

    /**
     * Merchants in this status.
     */
    @OneToMany(mappedBy = "status")
    private Collection<Merchant> merchants;

    /**
     * Version number used for optimistic lock.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 1;
}
