package com.example.empay.entity.security;

import com.example.empay.entity.AbstractAuditableEntity;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.util.ApplicationContextProvider;
import com.example.empay.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Entity representing a single user that is capable to login.
 */
@Entity
@Table(indexes = {@Index(name = Constants.IDX_USERLOGIN_USERNAME, columnList = "USERNAME", unique = true)})
@Getter
@Setter
@ToString(of = {"id", "username", "merchant", "role"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class UserLogin extends AbstractAuditableEntity {

    /**
     * Auto-generated entity ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userLoginSequence")
    @SequenceGenerator(name = "userLoginSequence", sequenceName = "USER_LOGIN_SEQ", allocationSize = 1, initialValue =
            Constants.ENTITY_SEQUENCE_INITIAL_VALUE)
    private Long id;

    /**
     * Username of the user login used as credential.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NAME)
    @Column(nullable = false, length = Constants.LENGTH_NAME)
    private String username;

    /**
     * Hashed password.
     */
    @Size(max = Constants.LENGTH_HASHED_PASSWORD)
    @Column(length = Constants.LENGTH_HASHED_PASSWORD)
    private String currentPassword;

    /**
     * Merchant related to this user login.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Merchant merchant;

    /**
     * Role of this user.
     */
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private RoleType role;

    /**
     * Indicates if this user is enabled and can log in.
     */
    private Boolean enabled = Boolean.TRUE;

    /**
     * Indicates if this user has logged out.
     */
    private Boolean hasLoggedOut = Boolean.FALSE;

    /**
     * Indicates if this user is expired and cannot log in.
     */
    private Boolean expired = Boolean.FALSE;

    /**
     * Indicates if this user is locked after a number of unsuccessful login attempts.
     */
    private Boolean locked = Boolean.FALSE;

    /**
     * Indicates if this user must change his/her password upon next log in.
     */
    private Boolean requirePasswordChange = Boolean.FALSE;

    /**
     * Version number used for optimistic lock.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 1;

    /**
     * Set the current password to a new value.
     *
     * @param newPassword The new value of the password.
     */
    public void setCurrentPassword(final String newPassword) {
        if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals(currentPassword)) {
            PasswordEncoder encoder = ApplicationContextProvider.getApplicationContext().getBean(PasswordEncoder.class);
            currentPassword = encoder.encode(newPassword);
        }
    }

}
