package io.github.legnatsol.springboot.ddd.dto;

import java.io.Serial;

/**
 * PO
 */
public abstract class PersistObject extends DTO {
    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * constructor
     */
    public PersistObject() {}
}
