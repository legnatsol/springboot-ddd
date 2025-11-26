package io.github.legnatsol.springboot.ddd.domain;

import java.io.Serializable;

/**
 * Save Modes
 */
public enum SaveMode implements Serializable {
    /**
     * Insert
     */
    INSERT,

    /**
     * Update
     */
    UPDATE,

    /**
     * Delete
     */
    DELETE,
}
