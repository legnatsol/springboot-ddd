package io.github.legnatsol.springboot.ddd.model;
import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.text.Normalizer;
import java.util.Objects;
import java.net.IDN;

/**
 * A value object representing an internationalized email address (RFC 6530).
 * <p>
 * This class supports Unicode characters in both the local-part and domain,
 * such as {@code "josé@café.fr"}.
 * </p>
 * Validation is lenient but robust:
 * <ul>
 *   <li>Exactly one '{@code @}' symbol</li>
 *   <li>Non-empty local-part and domain</li>
 *   <li>Domain contains at least one dot, not at start or end</li>
 *   <li>Domain is a valid-internationalized domain name (IDN)</li>
 * </ul>
 * It does <em>not</em> verify deliverability—only syntactic plausibility.
 * <p>
 * Instances are immutable, thread-safe, and suitable for use as map keys.
 * </p>
 */
public final class EmailAddress implements ValueObject<String> {

    /**
     * The normalized email address in lowercase Unicode NFC form.
     */
    private final String email;

    /**
     * Constructs an {@code EmailAddress} instance after validating and normalizing the input.
     *
     * @param rawEmail the raw email string provided by the user (must not be null)
     * @throws IllegalArgumentException if the email is null, empty, or structurally invalid
     */
    private EmailAddress(String rawEmail) {
        Objects.requireNonNull(rawEmail, "Email must not be null");

        // Trim and reject empty input early
        String trimmed = rawEmail.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        // Normalize to Unicode NFC to ensure consistent representation
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFC);

        // Parse and validate basic structure; extract domain part
        String domainPart = parseDomainPart(normalized, rawEmail);

        // Validate domain as a proper internationalized domain name
        validateDomain(domainPart, rawEmail);

        // Store in lowercase for case-insensitive equality (domains are case-insensitive;
        // local-part is typically treated as such in practice)
        this.email = normalized.toLowerCase();
    }

    /**
     * Parses the domain part from a normalized email string and performs basic structural checks.
     * <p>
     * Ensures:
     * <ul>
     *   <li>Exactly one '{@code @}' exists</li>
     *   <li>'{@code @}' is not at the start or end</li>
     *   <li>Domain part is non-empty</li>
     * </ul>
     *
     * @param normalizedEmail the email after Unicode normalization
     * @param originalEmail   the original input (used for error messages)
     * @return the extracted domain part
     * @throws IllegalArgumentException if the format is invalid
     */
    private static String parseDomainPart(String normalizedEmail, String originalEmail) {
        int atIndex = normalizedEmail.indexOf('@');
        // atIndex <= 0 covers: no '@' (atIndex = -1) or '@' at start (atIndex = 0)
        if (atIndex <= 0 || atIndex == normalizedEmail.length() - 1) {
            throw new IllegalArgumentException(
                    "Invalid email: missing or misplaced '@' in '" + originalEmail + "'");
        }
        // Check for multiple '@' symbols
        if (normalizedEmail.indexOf('@', atIndex + 1) != -1) {
            throw new IllegalArgumentException(
                    "Invalid email: multiple '@' symbols in '" + originalEmail + "'");
        }

        // Extract domain part (local-part is guaranteed non-empty due to atIndex > 0)
        String domainPart = normalizedEmail.substring(atIndex + 1);
        if (domainPart.isEmpty()) {
            throw new IllegalArgumentException(
                    "Email domain part cannot be empty: '" + originalEmail + "'");
        }
        return domainPart;
    }

    /**
     * Validates that the domain part is a syntactically valid internationalized domain name.
     * <p>
     * Checks:
     * <ul>
     *   <li>Contains at least one dot</li>
     *   <li>Does not start or end with a dot</li>
     *   <li>Is encodable via IDN (Punycode) without errors</li>
     * </ul>
     *
     * @param domain        the domain string (Unicode form)
     * @param originalEmail original input for error context
     * @throws IllegalArgumentException if the domain is malformed
     */
    private static void validateDomain(String domain, String originalEmail) {
        if (!domain.contains(".")) {
            throw new IllegalArgumentException(
                    "Domain must contain at least one dot: '" + originalEmail + "'");
        }
        if (domain.startsWith(".") || domain.endsWith(".")) {
            throw new IllegalArgumentException(
                    "Domain must not start or end with a dot: '" + originalEmail + "'");
        }
        try {
            // This validates IDN syntax (label length, allowed chars, etc.)
            IDN.toASCII(domain, IDN.ALLOW_UNASSIGNED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid internationalized domain in email: '" + originalEmail + "'", e);
        }
    }

    /**
     * Creates a validated {@link EmailAddress} instance from a string.
     * <p>
     * Supports international characters and performs lenient structural validation.
     *
     * @param email the email address
     * @return a new {@code EmailAddress} instance
     * @throws IllegalArgumentException if the input is null or invalid
     */
    public static EmailAddress of(String email) {
        return new EmailAddress(email);
    }

    /**
     * Returns the normalized email address in lowercase Unicode form.
     * <p>
     * This is the canonical, user-friendly representation.
     *
     * @return the validated email string
     */
    @Override
    public String getValue() {
        return email;
    }

    /**
     * Returns the ASCII-compatible encoding (ACE) of this email, suitable for SMTP or DNS.
     * <p>
     * Converts only the domain part to Punycode (per IDN standards).
     * The local-part remains in original Unicode (requires SMTPUTF8 support for delivery).
     * </p>
     *
     * @return the ASCII-encoded email address
     */
    public String toAscii() {
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);
        String asciiDomain = IDN.toASCII(domainPart, IDN.ALLOW_UNASSIGNED);
        return localPart + "@" + asciiDomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Email{" + email + '}';
    }
}
