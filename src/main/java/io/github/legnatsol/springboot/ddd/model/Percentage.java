package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents a percentage value between 0.0 and 100.0 (inclusive).
 * Stored as a decimal (e.g., 50% → 50.0).
 */
public final class Percentage implements ValueObject<BigDecimal> {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MAX = HUNDRED;

    /**
     * The percentage value.
     */
    private final BigDecimal value; // e.g., 50.0 for 50%

    /**
     * Creates a new Percentage instance.
     * @param percentage the percentage value.
     */
    public Percentage(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "Percentage must not be null");
        BigDecimal normalized = percentage.stripTrailingZeros();
        if (normalized.compareTo(ZERO) < 0 || normalized.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, inclusive: " + percentage);
        }
        this.value = normalized;
    }

    /**
     * Creates a new Percentage instance.
     * @param percentage the percentage value.
     */
    public Percentage(double percentage) {
        this(BigDecimal.valueOf(percentage));
    }

    /**
     * Parses string like "25.5" or "100".
     * @param value the string representation of the percentage.
     */
    public Percentage(String value) {
        Objects.requireNonNull(value, "Percentage string must not be null");
        try {
            this.value = new BigDecimal(value.trim()).stripTrailingZeros();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid percentage number: " + value, e);
        }
        if (this.value.compareTo(ZERO) < 0 || this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100: " + this.value);
        }
    }

    /**
     * Creates a new Percentage instance.
     * @param percentage the percentage value.
     * @return a new Percentage instance.
     */
    public static Percentage of(BigDecimal percentage) {
        return new Percentage(percentage);
    }

    /**
     * Creates a new Percentage instance.
     * @param percentage the percentage value.
     * @return a new Percentage instance.
     */
    public static Percentage of(double percentage) {
        return new Percentage(percentage);
    }

    /**
     * Parses string like "25.5" or "100".
     * @param value the string representation of the percentage.
     * @return a new Percentage instance.
     */
    public static Percentage parse(String value) {
        return new Percentage(value);
    }

    /**
     * Returns the percentage value.
     * @return the percentage value.
     */
    @Override
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Converts the percentage to a decimal value.
     * @return the decimal value.
     */
    public BigDecimal toDecimal() {
        return value.divide(HUNDRED, 10, RoundingMode.HALF_UP); // e.g., 50% → 0.5
    }

    /**
     * Adds another percentage to this one.
     * @param other the other percentage.
     * @return the sum of the two percentages.
     */
    public Percentage add(Percentage other) {
        BigDecimal sum = this.value.add(other.value);
        if (sum.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("Sum exceeds 100%: " + sum);
        }
        return new Percentage(sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Percentage that = (Percentage) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Percentage{" + value + "%}";
    }
}
