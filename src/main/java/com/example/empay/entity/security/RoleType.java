package com.example.empay.entity.security;

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
 * Reference (nomenclature) entity representing user role types.
 */
@Entity
@Getter
@Setter
@ToString(of = {"id", "name"})
@EqualsAndHashCode(of = {"id"})
public class RoleType {

    /**
     * Possible IDs of role types.
     */
    public enum TYPE {
        /**
         * Admin role type.
         */
        ADMIN,
        /**
         * Merchant role type.
         */
        MERCHANT
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
     * Name of the role type.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NOMENCLATURE_NAME)
    @Column(nullable = false, length = Constants.LENGTH_NOMENCLATURE_NAME)
    private String name;

    /**
     * Verison number used for optimistic lock.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 1;

    /**
     * User logins in this role type.
     */
    @OneToMany(mappedBy = "role")
    private Collection<UserLogin> userLogins;
}
