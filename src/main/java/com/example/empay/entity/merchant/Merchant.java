package com.example.empay.entity.merchant;

import com.example.empay.entity.AbstractAuditableEntity;
import com.example.empay.entity.security.UserLogin;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.util.Constants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(name = "IDX_MERCHANT_NAME", columnList = "NAME"),
        @Index(name = Constants.IDX_MERCHANT_EMAIL, columnList = "EMAIL", unique = true),
        @Index(name = Constants.IDX_MERCHANT_IDENT, columnList = "IDENTIFIER_TYPE_ID,IDENTIFIER_VALUE", unique = true)})
@EntityListeners(AuditingEntityListener.class)
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Merchant extends AbstractAuditableEntity {

    /**
     * Auto-generated ID of the merchant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchantSeq")
    @SequenceGenerator(name = "merchantSeq", sequenceName = "MERCHANT_SEQ", allocationSize = 1, initialValue =
            Constants.ENTITY_SEQUENCE_INITIAL_VALUE)
    private Long id;

    /**
     * Merchant name.
     */
    @NotNull
    @NotBlank
    @Size(min = 1, max = Constants.LENGTH_NAME)
    @Column(nullable = false, length = Constants.LENGTH_NAME)
    private String name;

    /**
     * Contact email of the merchant.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_EMAIL)
    @Column(nullable = false, length = Constants.LENGTH_EMAIL)
    @Email
    private String email;

    /**
     * Current status.
     */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_ID", nullable = false, foreignKey = @ForeignKey(name = Constants.FK_MERCHANT_STATUS_ID))
    private MerchantStatusType status;

    /**
     * Total sum of transactions of type CHARGE minus REFUND for this merchant.
     */
    @NotNull
    @Min(0)
    @Digits(integer = Constants.MAX_DECIMAL_INTEGER_DIGITS, fraction = Constants.MAX_DECIMAL_FRACTIONAL_DIGITS)
    @Column(precision = Constants.MAX_DECIMAL_INTEGER_DIGITS, scale = Constants.MAX_DECIMAL_FRACTIONAL_DIGITS,
            nullable = false)
    private BigDecimal totalTransactionSum = BigDecimal.ZERO;

    /**
     * Business identifier type of this merchant.
     */
    @ManyToOne
    @JoinColumn(name = "IDENTIFIER_TYPE_ID", foreignKey = @ForeignKey(name = Constants.FK_MERCHANT_IDENT_TYPE_ID))
    private MerchantIdentifierType identifierType;

    /**
     * Business identifier value.
     */
    @Size(max = Constants.LENGTH_BUSINESS_IDENTIFIER)
    @Column(length = Constants.LENGTH_BUSINESS_IDENTIFIER)
    private String identifierValue;

    /**
     * Version number used for optimistic locking.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 0;

    /**
     * User logins related to this merchant.
     */
    @OneToMany(mappedBy = "merchant", cascade = {CascadeType.REMOVE})
    private Collection<UserLogin> userLogins;

    /**
     * Transactions related to this merchant.
     */
    @OneToMany(mappedBy = "merchant")
    private Collection<Transaction> transactions;
}
