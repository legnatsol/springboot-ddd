package io.github.legnatsol.springboot.ddd.model;

/**
 * Represents a validated WebSocket URL.
 * <p>
 * Only allows schemes: {@code ws} and {@code wss}.
 */
public final class WebsocketUrl extends BaseUrl<WebsocketUrl> {

    private static final long serialVersionUID = 1L;

    private WebsocketUrl(String url) {
        super(url);
    }

    /**
     * Creates a {@link WebsocketUrl} from the given string.
     *
     * @param url the URL string (e.g., "wss://chat.example.com/socket")
     * @return a new {@code WebsocketUrl} instance
     * @throws NullPointerException if {@code url} is null
     * @throws IllegalArgumentException if the URL is invalid, blank, lacks a host,
     *                                  or uses a scheme other than "ws" or "wss"
     */
    public static WebsocketUrl of(String url) {
        return new WebsocketUrl(url);
    }

    @Override
    protected void validateScheme(String scheme) {
        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException(
                    "Unsupported scheme for WebsocketUrl: '" + scheme + "'. Only 'ws' and 'wss' are allowed.");
        }
    }
}
