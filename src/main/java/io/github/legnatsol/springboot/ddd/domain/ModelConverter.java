package io.github.legnatsol.springboot.ddd.domain;

/**
 * Model Converter
 * @param <D> domain object
 * @param <P> persist object
 */
public interface ModelConverter<D, P> {
    /**
     * domain object -> persist object
     *
     * @param domainObject domain object
     * @return persist object
     */
    P toPersist(D domainObject);

    /**
     * persist object -> domain object
     *
     * @param persistObject persist object
     * @return domain object
     */
    D toDomain(P persistObject);
}
