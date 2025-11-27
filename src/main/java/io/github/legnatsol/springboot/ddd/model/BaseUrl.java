package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.io.Serializable;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for validated URL value objects with full URI parsing,
 * query parameter extraction, JavaScript-compatible encoding/decoding,
 * and Internationalized Domain Name (IDN) support.
 * <p>
 * Subclasses must implement {@link #validateScheme(String)} to restrict allowed schemes.
 * The host is automatically normalized using IDN (Punycode ↔ Unicode).
 * <p>
 * This class is immutable and thread-safe.
 *
 * @param <T> the concrete subclass type (e.g., HttpUrl, WebsocketUrl)
 */
public abstract class BaseUrl<T extends BaseUrl<T>> implements ValueObject<String> {

    /**
     * The original URL string as provided during construction.
     */
    private final String originalUrl;

    /**
     * The parsed URI, stored as an ASCII version for internal use.
     */
    private transient URI parsedUri;

    /**
     * Constructs a validated URL instance.
     *
     * @param url the raw URL string; must not be null or blank
     * @throws NullPointerException if {@code url} is null
     * @throws IllegalArgumentException if {@code url} is blank, malformed, lacks a host,
     *                                  or uses an unsupported scheme
     */
    protected BaseUrl(String url) {
        Objects.requireNonNull(url, "url must not be null");
        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("url must not be blank");
        }

        try {
            // Parse original URI to extract components
            URI originalUri = new URI(trimmed);
            if (originalUri.getHost() == null || originalUri.getHost().isEmpty()) {
                throw new IllegalArgumentException("URL must contain a valid host: " + trimmed);
            }

            // Validate scheme before IDN conversion
            validateScheme(originalUri.getScheme());

            // Convert host to ASCII (Punycode) for safe URI construction
            String asciiHost = IDN.toASCII(originalUri.getHost());
            URI asciiUri = new URI(
                    originalUri.getScheme(),
                    originalUri.getUserInfo(),
                    asciiHost,
                    originalUri.getPort(),
                    originalUri.getPath(),
                    originalUri.getQuery(),
                    originalUri.getFragment()
            );

            this.originalUrl = trimmed;
            this.parsedUri = asciiUri; // store ASCII version for internal use
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL syntax: " + trimmed, e);
        }
    }

    /**
     * Validates whether the given scheme is allowed for this URL type.
     * <p>
     * Called during construction. Must throw {@link IllegalArgumentException} if the scheme is not supported.
     *
     * @param scheme the URI scheme (e.g., "http", "ws"), may be null
     * @throws IllegalArgumentException if the scheme is not allowed
     */
    protected abstract void validateScheme(String scheme);

    /**
     * Returns the original URL string as provided during construction.
     *
     * @return the non-null, trimmed URL string
     */
    @Override
    public final String getValue() {
        return originalUrl;
    }

    /**
     * Returns the URI scheme in lowercase (e.g., "https", "wss").
     *
     * @return the scheme
     */
    public final String getScheme() {
        return getAsciiUri().getScheme().toLowerCase(java.util.Locale.ROOT);
    }

    /**
     * Returns the host part of the URL in Unicode form (e.g., "例子.测试").
     * <p>
     * If the original URL used Punycode (e.g., "xn--fsq.xn--0zwm56d"),
     * this method returns the human-readable Unicode equivalent.
     *
     * @return the decoded Unicode host name
     */
    public final String getHost() {
        String asciiHost = getAsciiUri().getHost();
        return asciiHost != null ? IDN.toUnicode(asciiHost) : "";
    }

    /**
     * Returns the port number, or -1 if not explicitly specified.
     *
     * @return the port number
     */
    public final int getPort() {
        return getAsciiUri().getPort();
    }

    /**
     * Returns the path component of the URL.
     * <p>
     * Returns {@code ""} if no path is present.
     *
     * @return the path (e.g., "/api/v1/users")
     */
    public final String getPath() {
        String path = getAsciiUri().getPath();
        return path != null ? path : "";
    }

    /**
     * Returns the raw query string (without the leading '?'), or {@code null} if none.
     *
     * @return the query string
     */
    public final String getQuery() {
        return getAsciiUri().getQuery();
    }

    /**
     * Returns the fragment (without the leading '#'), or {@code null} if none.
     *
     * @return the fragment
     */
    public final String getFragment() {
        return getAsciiUri().getFragment();
    }

    /**
     * Returns an unmodifiable list of decoded parameter values for the given query parameter key.
     * <p>
     * Keys are matched case-sensitively. Values are UTF-8 URL-decoded.
     * <p>
     * Example:
     * <pre>
     *   HttpUrl url = HttpUrl.of("https://ex.com/?name=张三&amp;name=李四");
     *   url.getQueryParams("name"); // ["张三", "李四"]
     * </pre>
     *
     * @param key the query parameter key (must not be null or blank)
     * @return an unmodifiable list of decoded values; never null
     * @throws NullPointerException if {@code key} is null
     * @throws IllegalArgumentException if {@code key} is blank
     */
    public final List<String> getQueryParams(String key) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be blank");
        }

        String query = getQuery();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> values = new ArrayList<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            int idx = pair.indexOf('=');
            String namePart = idx > 0 ? pair.substring(0, idx) : pair;
            String valuePart = idx > 0 && idx < pair.length() - 1 ? pair.substring(idx + 1) : "";

            try {
                String decodedName = URLDecoder.decode(namePart, StandardCharsets.UTF_8);
                if (key.equals(decodedName)) {
                    String decodedValue = URLDecoder.decode(valuePart, StandardCharsets.UTF_8);
                    values.add(decodedValue);
                }
            } catch (IllegalArgumentException e) {
                // Skip malformed percent-encoding
                continue;
            }
        }

        return Collections.unmodifiableList(values);
    }

    // ====== Static Utility Methods (JavaScript compatible) ======

    /**
     * Decodes a Uniform Resource Identifier (URI) previously created by {@link #encodeURI()}
     * or by another routine, using UTF-8 encoding.
     * <p>
     * Does NOT decode escape sequences that represent reserved URI characters:
     * {@code ; / ? : @ & = + $ , # [ ]}.
     * <p>
     * Mirrors JavaScript's {@code decodeURI()}.
     *
     * @param uri the encoded URI string
     * @return the decoded URI string
     * @throws IllegalArgumentException if the input contains invalid percent-encoding
     * @throws NullPointerException if {@code uri} is null
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">MDN: decodeURI</a>
     */
    public static String decodeURI(String uri) {
        Objects.requireNonNull(uri, "uri must not be null");
        return decode(uri, URI_RESERVED);
    }

    /**
     * Decodes a URI component previously created by {@link #encodeURIComponent()}
     * or by another routine, using UTF-8 encoding.
     * <p>
     * Decodes ALL percent-encoded sequences.
     * <p>
     * Mirrors JavaScript's {@code decodeURIComponent()}.
     *
     * @param component the encoded URI component
     * @return the decoded string
     * @throws IllegalArgumentException if the input contains invalid percent-encoding
     * @throws NullPointerException if {@code component} is null
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">MDN: decodeURIComponent</a>
     */
    public static String decodeURIComponent(String component) {
        Objects.requireNonNull(component, "component must not be null");
        return URLDecoder.decode(component, StandardCharsets.UTF_8);
    }

    // ====== Instance Encoding Methods ======

    /**
     * Encodes the entire URL string, preserving URI-reserved characters.
     * <p>
     * Equivalent to JavaScript's {@code encodeURI(this.getValue())}.
     *
     * @return the encoded URI string
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">MDN: encodeURI</a>
     */
    public final String encodeURI() {
        return encode(getValue(), URI_RESERVED);
    }

    /**
     * Encodes this URL as a single URI component.
     * <p>
     * Equivalent to JavaScript's {@code encodeURIComponent(this.getValue())}.
     *
     * @return the encoded URI component
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">MDN: encodeURIComponent</a>
     */
    public final String encodeURIComponent() {
        return encode(getValue(), URIC_COMPONENT_UNRESERVED);
    }

    // ====== Helper Fields and Methods ======

    private static final BitSet URI_RESERVED = new BitSet(256);
    private static final BitSet URIC_COMPONENT_UNRESERVED = new BitSet(256);

    static {
        // Unreserved: ALPHA / DIGIT / - _ . ~
        for (int i = 'a'; i <= 'z'; i++) URI_RESERVED.set(i);
        for (int i = 'A'; i <= 'Z'; i++) URI_RESERVED.set(i);
        for (int i = '0'; i <= '9'; i++) URI_RESERVED.set(i);
        for (char c : "-_.~".toCharArray()) URI_RESERVED.set(c);

        // Reserved but NOT encoded by encodeURI: ; / ? : @ & = + $ , # [ ]
        for (char c : ";/?:@&=+$,#[]".toCharArray()) URI_RESERVED.set(c);

        // encodeURIComponent only preserves unreserved
        URIC_COMPONENT_UNRESERVED.or(URI_RESERVED);
        // Remove reserved chars from component set
        for (char c : ";/?:@&=+$,#[]".toCharArray()) {
            URIC_COMPONENT_UNRESERVED.clear(c);
        }
    }

    private static String encode(String input, BitSet allowed) {
        StringBuilder out = new StringBuilder();
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            int u = b & 0xFF;
            if (allowed.get(u)) {
                out.append((char) u);
            } else {
                out.append(String.format("%%%02X", u));
            }
        }
        return out.toString();
    }

    private static String decode(String s, BitSet skipDecoding) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%' && i + 2 < s.length()) {
                String hex = s.substring(i + 1, i + 3);
                try {
                    int code = Integer.parseInt(hex, 16);
                    if (skipDecoding.get(code)) {
                        result.append('%').append(hex);
                    } else {
                        result.append((char) code);
                    }
                    i += 3;
                } catch (NumberFormatException e) {
                    result.append(c);
                    i++;
                }
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    private URI getAsciiUri() {
        if (parsedUri == null) {
            // Should not happen due to constructor logic, but safe fallback
            try {
                URI orig = new URI(originalUrl);
                String asciiHost = orig.getHost() != null ? IDN.toASCII(orig.getHost()) : null;
                parsedUri = new URI(
                        orig.getScheme(),
                        orig.getUserInfo(),
                        asciiHost,
                        orig.getPort(),
                        orig.getPath(),
                        orig.getQuery(),
                        orig.getFragment()
                );
            } catch (URISyntaxException e) {
                throw new IllegalStateException("URL should be valid", e);
            }
        }
        return parsedUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUrl<?> that = (BaseUrl<?>) o;
        return Objects.equals(originalUrl, that.originalUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + originalUrl + '}';
    }
}
