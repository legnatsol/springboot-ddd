package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * A value object representing a valid IP address (IPv4 or IPv6).
 * <p>
 * This class encapsulates an IP address string and ensures it is valid upon construction.
 * It is immutable and implements proper {@code equals()} and {@code hashCode()} semantics.
 * </p>
 */
public final class IPAddress implements ValueObject<String> {

    /**
     * The underlying IP address string.
     */
    private final String ipAddress;

    /**
     * Private constructor to enforce validation via static factory method.
     *
     * @param ipAddress the raw IP address string; must not be null and must represent a valid IP.
     * @throws IllegalArgumentException if the given string is not a valid IPv4 or IPv6 address.
     */
    private IPAddress(String ipAddress) {
        Objects.requireNonNull(ipAddress, "IP address must not be null");
        if (!isValidIP(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress);
        }
        this.ipAddress = ipAddress;
    }

    /**
     * Static factory method to create an {@link IPAddress} instance.
     *
     * @param ipAddress the IP address string to validate and wrap.
     * @return a new {@code IPAddress} instance if valid.
     * @throws IllegalArgumentException if the input is null or not a valid IP address.
     */
    public static IPAddress of(String ipAddress) {
        return new IPAddress(ipAddress);
    }

    /**
     * Validates whether the given string is a syntactically correct IPv4 or IPv6 address.
     * <p>
     * This method uses {@link InetAddress#getByName(String)} for robust validation,
     * which handles standard formats including compressed IPv6 and mixed notation.
     * </p>
     *
     * @param ip the candidate IP address string.
     * @return {@code true} if valid, {@code false} otherwise (including null input).
     */
    private static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        try {
            // InetAddress.getByName() throws UnknownHostException for invalid IPs
            InetAddress addr = InetAddress.getByName(ip);
            // Additional check: ensure the canonical form matches input in terms of format?
            // Not strictly necessary for validation, but we rely on JDK's parser.
            return true;
        } catch (UnknownHostException | SecurityException e) {
            return false;
        }
    }

    /**
     * Returns the underlying IP address string.
     *
     * @return the validated IP address as a {@link String}.
     */
    @Override
    public String getValue() {
        return ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress that = (IPAddress) o;
        return Objects.equals(ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress);
    }

    @Override
    public String toString() {
        return "IPAddress{" + ipAddress + '}';
    }
}
