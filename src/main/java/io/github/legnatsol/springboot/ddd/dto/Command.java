package io.github.legnatsol.springboot.ddd.dto;

import java.io.Serial;

/**
 * Command
 */
public abstract class Command extends DTO {
    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * Constructor
     */
    public Command() {
    }
}
