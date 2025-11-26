package io.github.legnatsol.springboot.ddd.exception;

/**
 * Domain Exception
 */
public class DomainException extends RuntimeException {
    /**
     * Constructor with message
     *
     * @param message exception message
     */
    public DomainException(String message) {
        super(message);
    }
}
