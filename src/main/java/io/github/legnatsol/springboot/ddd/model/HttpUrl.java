package io.github.legnatsol.springboot.ddd.model;

/**
 * Represents a validated HTTP or HTTPS URL.
 * <p>
 * Only allows schemes: {@code http} and {@code https}.
 */
public final class HttpUrl extends BaseUrl<HttpUrl> {

    private static final long serialVersionUID = 1L;

    private HttpUrl(String url) {
        super(url);
    }

    /**
     * Creates an {@link HttpUrl} from the given string.
     *
     * @param url the URL string (e.g., "https://example.com/path")
     * @return a new {@code HttpUrl} instance
     * @throws NullPointerException if {@code url} is null
     * @throws IllegalArgumentException if the URL is invalid, blank, lacks a host,
     *                                  or uses a scheme other than "http" or "https"
     */
    public static HttpUrl of(String url) {
        return new HttpUrl(url);
    }

    @Override
    protected void validateScheme(String scheme) {
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException(
                    "Unsupported scheme for HttpUrl: '" + scheme + "'. Only 'http' and 'https' are allowed.");
        }
    }
}
