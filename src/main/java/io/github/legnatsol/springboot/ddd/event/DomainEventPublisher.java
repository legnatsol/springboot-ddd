package io.github.legnatsol.springboot.ddd.event;

import io.github.legnatsol.springboot.ddd.dto.EventObject;

/**
 * Domain Event Publisher
 */
public interface DomainEventPublisher {
    /**
     * publish domain event
     *
     * @param event domain event
     * @param <T> domain event data
     */
    <T extends EventObject> void publishEvent(DomainEvent<T> event);
}
