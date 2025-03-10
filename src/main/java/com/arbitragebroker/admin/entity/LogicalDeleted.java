package com.arbitragebroker.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

public interface LogicalDeleted {
    /**
     * entity is deleted or not
     * default value is false
     * @return boolean
     */
    @Column(name = "DELETED", nullable = false)
    boolean isDeleted();

    /**
     * @param deleted
     */
    void setDeleted(boolean deleted);

    /**
     * change deletion status to opposite of current status
     */
    @Transient
    default void toggleDeleted() {
        setDeleted(!isDeleted());
    }
}
