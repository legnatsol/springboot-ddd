package io.github.legnatsol.springboot.ddd.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO
 */
public abstract class DTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * Constructor
     */
    public DTO() {}
}
