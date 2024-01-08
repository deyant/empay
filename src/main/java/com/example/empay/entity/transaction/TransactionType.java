package com.example.empay.entity.transaction;

import com.example.empay.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Reference (nomenclature) entity for types of a transaction.
 */
@Entity
@Getter
@Setter
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"})
@Accessors(chain = true)
public class TransactionType {

    /**
     * Possible transaction type IDs.
     */
    public enum TYPE {
        /**
         * Authorize transaction type.
         */
        AUTHORIZE,
        /**
         * Charge transaction type.
         */
        CHARGE,
        /**
         * Refund transcation type.
         */
        REFUND,
        /**
         * Reversal transaction type.
         */
        REVERSAL
    }

    /**
     * Entity ID.
     */
    @Id
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NOMENCLATURE_ID)
    @Column(length = Constants.LENGTH_NOMENCLATURE_ID)
    private String id;

    /**
     * Displayable name of this transaction type.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NOMENCLATURE_NAME)
    @Column(nullable = false, length = Constants.LENGTH_NOMENCLATURE_NAME)
    private String name;

    /**
     * Version number used for optimistic lock.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 1;
}
