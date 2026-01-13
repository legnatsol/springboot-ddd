package io.github.legnatsol.springboot.ddd.event;

import io.github.legnatsol.springboot.ddd.dto.EventObject;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.io.Serializable;
import java.util.UUID;

/**
 * Domain Event
 * @param <T> Domain Event Data
 */
public class DomainEvent<T extends EventObject> extends Event<T> implements ResolvableTypeProvider, Serializable {
    private DomainEvent(String type, T data) {
        super(UUID.randomUUID().toString(), type, data, System.currentTimeMillis());
    }

    /**
     * Create builder
     * @param type event type
     * @return builder
     */
    public static Builder type(String type) {
        return new Builder(type);
    }

    /**
     * Builder
     * @param type event type
     */
    public record Builder(String type) {

        /**
         * Create domain event
         *
         * @param data event data
         * @param <T> event data type
         * @return domain event
         */
        public <T extends EventObject> DomainEvent<T> data(T data) {
            return new DomainEvent<>(type, data);
        }
    }

    /**
     * Resolvable Type Provider
     * @return resolvable type
     */
    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getData()));
    }

    /**
     * Convert to Event
     * @return event
     */
    public Event<T> toEvent() {
        return new Event<>(getId(), getType(), getData(), getTimestamp());
    }
}
