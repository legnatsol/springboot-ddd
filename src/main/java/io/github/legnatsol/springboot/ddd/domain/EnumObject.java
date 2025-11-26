package io.github.legnatsol.springboot.ddd.domain;

import java.io.Serializable;

/**
 * Enum Object
 * @param <T> enum value
 */
public interface EnumObject<T> extends Serializable {

    /**
     * get value
     * @return value
     */
    T getValue();
}
