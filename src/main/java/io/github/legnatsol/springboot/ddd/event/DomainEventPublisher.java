package io.github.legnatsol.springboot.ddd.event;

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
    <T extends DomainEventData> void publishEvent(DomainEvent<T> event);
}
