package io.github.legnatsol.springboot.ddd.domain;

/**
 * Aggregate Root
 * @param <ID> Identifier
 */
public interface AggregateRoot<ID extends Identifier<?>> extends Entity<ID> {
    /**
     * save mode
     *
     * @return mode
     */
    SaveMode getSaveMode();
}
