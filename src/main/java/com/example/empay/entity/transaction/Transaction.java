package com.example.empay.entity.transaction;

import com.example.empay.entity.AbstractAuditableEntity;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a financial transaction.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(indexes = {@Index(name = Constants.FK_IDX_TRANS_REF_ID, columnList = "REFERENCE_ID", unique = true)})
@ToString(of = {"id", "type", "referenceId", "merchant"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Transaction extends AbstractAuditableEntity {

    /**
     * Entity primary key.
     */
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    /**
     * Type of the transaction.
     */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "TYPE_ID", updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = Constants.FK_TRANSACTION_TYPE_ID))
    private TransactionType type;

    /**
     * Amount of the transaction. Amount is immutable and cannot be changed after created.
     */
    @Min(0)
    @Digits(integer = Constants.MAX_DECIMAL_INTEGER_DIGITS, fraction = Constants.MAX_DECIMAL_FRACTIONAL_DIGITS)
    @Column(precision = Constants.MAX_DECIMAL_INTEGER_DIGITS, scale = Constants.MAX_DECIMAL_FRACTIONAL_DIGITS,
            updatable = false)
    private BigDecimal amount;


    /**
     * Email of the customer who initiated this transaction.
     */
    @Email
    @Size(max = Constants.LENGTH_EMAIL)
    @Column(length = Constants.LENGTH_EMAIL)
    private String customerEmail;

    /**
     * Phone of the customer who initiated this transaction.
     */
    @Size(max = Constants.LENGTH_PHONE)
    @Column(length = Constants.LENGTH_PHONE)
    @Pattern(regexp = "^[\\+]{0,1}[\\d]{1,}$")
    private String customerPhone;

    /**
     * Status of this transaction.
     */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false, foreignKey = @ForeignKey(name =
            Constants.FK_TRANSACTION_STATUS_ID))
    private TransactionStatusType status;

    /**
     * Error message describing the reason the transaction is in status ERROR.
     */
    @Size(max = Constants.LENGTH_ERROR_REASON)
    @Column(length = Constants.LENGTH_ERROR_REASON)
    private String errorReason;

    /**
     * Reference ID of this transaction as provided by a 3rd party. Immutable value that cannot be updated once created.
     */
    @Size(max = Constants.LENGTH_REFERENCE_ID)
    @Column(length = Constants.LENGTH_REFERENCE_ID, updatable = false)
    private String referenceId;

    /**
     * Merchant related to this transaction.
     */
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "MERCHANT_ID", updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = Constants.FK_TRANSACTION_MERCHANT_ID))
    private Merchant merchant;

    /**
     * Parent transaction of this transaction.
     */
    @OneToOne
    private Transaction belongsToTransaction;

    /**
     * Child transaction for this transaction. Used for one-to-one mapping only.
     */
    @OneToOne(mappedBy = "belongsToTransaction")
    private Transaction belongingTransaction;

    /**
     * Version number used for optimistic lock.
     */
    @Version
    @Column(nullable = false)
    private Integer version;
}
