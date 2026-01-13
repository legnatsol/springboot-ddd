package io.github.legnatsol.springboot.ddd.repository;

import io.github.legnatsol.springboot.ddd.domain.BaseAggregateRoot;
import io.github.legnatsol.springboot.ddd.domain.Identifier;
import io.github.legnatsol.springboot.ddd.domain.SaveMode;
import io.github.legnatsol.springboot.ddd.event.DomainEvent;
import io.github.legnatsol.springboot.ddd.util.SpringUtils;

import java.util.List;

/**
 * Base Repository
 * @param <T>   AggregateRoot type
 * @param <ID>  Identifier type
 */
public abstract class BaseRepository<T extends BaseAggregateRoot<ID>, ID extends Identifier<?>>
        implements Repository<T, ID> {

    /**
     * Constructor
     */
    public BaseRepository() {
    }

    /**
     * publish events
     *
     * @param aggregateRoot Aggregate Root
     */
    protected void publishEvent(T aggregateRoot) {
        List<DomainEvent<?>> events = aggregateRoot.getEvents();
        if (events != null && !events.isEmpty()) {
            events.forEach(SpringUtils::publishEvent);
            aggregateRoot.clearEvents();
        }
    }

    @Override
    public T find(ID id) {
        return findById(id);
    }

    @Override
    public void save(T aggregate) {
        if (SaveMode.INSERT == aggregate.getSaveMode()) {
            this.saveForInsert(aggregate);
        } else if (SaveMode.UPDATE == aggregate.getSaveMode()) {
            this.saveForUpdate(aggregate);
        } else if (SaveMode.DELETE == aggregate.getSaveMode()) {
            this.saveForDelete(aggregate);
        }
        publishEvent(aggregate);
    }

    /**
     * Save aggregate for insert operation
     *
     * @param aggregate aggregate root
     */
    protected abstract void saveForInsert(T aggregate);

    /**
     * Find aggregate by id
     *
     * @param id identifier
     * @return aggregate root
     */
    protected abstract T findById(ID id);

    /**
     * Save aggregate for update operation
     *
     * @param aggregate aggregate root
     */
    protected abstract void saveForUpdate(T aggregate);

    /**
     * Save aggregate for delete operation
     *
     * @param aggregate aggregate root
     */
    protected abstract void saveForDelete(T aggregate);
}
