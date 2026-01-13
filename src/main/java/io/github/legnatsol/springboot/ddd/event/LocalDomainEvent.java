package io.github.legnatsol.springboot.ddd.event;

import io.github.legnatsol.springboot.ddd.dto.EventObject;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.io.Serializable;

/**
 * Local Domain Event
 * @param <T> event data
 */
public class LocalDomainEvent<T extends EventObject> extends Event<T> implements ResolvableTypeProvider, Serializable {

    /**
     * Create from event
     * @param event event
     */
    public LocalDomainEvent(Event<T> event) {
        super(event.getId(), event.getType(), event.getData(), event.getTimestamp());
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getData()));
    }
}
