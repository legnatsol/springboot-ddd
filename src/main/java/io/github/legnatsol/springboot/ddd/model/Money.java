package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a monetary amount with currency.
 * Uses BigDecimal to avoid floating-point errors.
 */
public final class Money implements ValueObject<String>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Amount of money.
     */
    private final BigDecimal amount;

    /**
     * Currency of money.
     */
    private final Currency currency;

    /**
     * Constructor for frameworks like MapStruct.
     * @param amount amount
     * @param currency currency
     */
    public Money(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException(
                    "Amount scale exceeds currency's fraction digits: " + amount + " for " + currency.getCurrencyCode());
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative: " + amount);
        }
        this.amount = amount.stripTrailingZeros();
        this.currency = currency;
    }

    /**
     * Constructor for frameworks like MapStruct.
     * Expects string in format: "CNY 100.00" or "USD 50"
     * @param value string representation of money
     */
    public Money(String value) {
        Objects.requireNonNull(value, "Money string must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Money string must not be empty");
        }

        String[] parts = trimmed.split("\\s+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected format 'CURRENCY AMOUNT', got: '" + value + "'");
        }

        String currencyCode = parts[0];
        String amountStr = parts[1];

        Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode, e);
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: " + amountStr, e);
        }

        // Reuse main constructor for validation
        this.amount = amount.stripTrailingZeros();
        this.currency = currency;

        // Validate after assignment to avoid double-check
        if (this.amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException(
                    "Amount scale exceeds currency's fraction digits: " + this.amount + " for " + currencyCode);
        }
        if (this.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative: " + this.amount);
        }
    }

    /**
     * Factory method for creating Money instances.
     * @param amount amount
     * @param currency currency
     * @return new Money instance
     */
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    /**
     * Factory method for creating Money instances.
     * @param amount amount
     * @param currency currency
     * @return new Money instance
     */
    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    /**
     * Parses a string representation of a money amount.
     * @param value string representation of a money amount
     * @return new Money instance
     */
    public static Money parse(String value) {
        return new Money(value);
    }

    /**
     * Returns the amount of this money.
     * @return amount of this money
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the currency of this money.
     * @return currency of this money
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Returns the string representation of this money amount.
     * @return string representation of this money amount
     */
    @Override
    public String getValue() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }

    /**
     * Adds another money amount to this one.
     * @param other other money amount
     * @return new Money instance
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts another money amount from this one.
     * @param other other money amount
     * @return new Money instance
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting amount cannot be negative");
        }
        return new Money(result, this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Money{" + getValue() + '}';
    }
}
