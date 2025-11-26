package io.github.legnatsol.springboot.ddd.domain;

/**
 * Identifiable interface
 * @param <ID> Identifier type
 */
public interface Identifiable<ID extends Identifier<?>> {

    /**
     * Get Identifier
     * @return Identifier
     */
    ID getId();
}
