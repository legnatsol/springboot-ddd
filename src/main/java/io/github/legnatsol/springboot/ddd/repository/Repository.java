package io.github.legnatsol.springboot.ddd.repository;

import io.github.legnatsol.springboot.ddd.domain.AggregateRoot;
import io.github.legnatsol.springboot.ddd.domain.Identifier;

/**
 * Repository
 * @param <T>   AggregateRoot type
 * @param <ID>  Identifier type
 */
public interface Repository<T extends AggregateRoot<ID>, ID extends Identifier<?>> {
    /**
     * find AggregateRoot by ID
     * the found AggregateRoot is automatically traceable.
     * @param id ID
     * @return AggregateRoot
     */
    T find(ID id);

    /**
     * save an AggregateRoot
     * automatically reset tracking conditions after saving
     * @param aggregate AggregateRoot
     */
    void save(T aggregate);
}
