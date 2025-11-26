package io.github.legnatsol.springboot.ddd.domain;

import java.io.Serializable;

/**
 * Value Object
 * @param <T> value type
 */
public interface ValueObject<T> extends Serializable {
    /**
     * get value
     * @return  value
     */
    T getValue();
}
