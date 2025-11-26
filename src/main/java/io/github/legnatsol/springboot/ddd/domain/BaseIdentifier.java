package io.github.legnatsol.springboot.ddd.domain;

import java.util.Objects;

/**
 * Base Identifier
 * @param <T> identifier value type
 */
public abstract class BaseIdentifier<T> implements Identifier<T> {

    /**
     * identifier value
     */
    protected T value;

    /**
     * Default constructor
     */
    private BaseIdentifier() {
    }

    /**
     * Constructor with value
     *
     * @param value identifier value
     */
    public BaseIdentifier(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BaseIdentifier<?> that)) {
            return false;
        }
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
