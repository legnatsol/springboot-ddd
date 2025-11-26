package io.github.legnatsol.springboot.ddd.domain;

/**
 * Entity interface
 * @param <ID>  identifier
 */
public interface Entity<ID extends Identifier<?>> extends Identifiable<ID> {
}
