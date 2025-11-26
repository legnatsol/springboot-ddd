package io.github.legnatsol.springboot.ddd.domain;

import java.io.Serializable;

/**
 * Identifier interface
 * @param <T> identifier value type
 */
public interface Identifier<T> extends Serializable {
    /**
     * get identifier value
     * @return identifier value
     */
    T getValue();
}
