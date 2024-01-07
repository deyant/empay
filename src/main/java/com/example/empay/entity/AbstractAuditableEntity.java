package com.example.empay.entity;

import com.example.empay.util.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

/**
 * Base class of entities providing common properties.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractAuditableEntity {

    /**
     * The date and time this entity was persisted.
     */
    @CreatedDate
    @Column(precision = Constants.TIMESTAMP_PRECISION)
    private ZonedDateTime createdDate;

    /**
     * The date and time this entity was last modified.
     */
    @LastModifiedDate
    @Column(precision = Constants.TIMESTAMP_PRECISION)
    private ZonedDateTime lastModifiedDate;
}
