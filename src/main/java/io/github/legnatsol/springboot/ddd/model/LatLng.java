package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.util.Objects;

/**
 * An immutable value object representing a geographic coordinate in decimal degrees (WGS84).
 * <p>
 * The canonical string representation is {@code "latitude,longitude"} (e.g., {@code "39.9042,116.4074"}),
 * which aligns with human-readable conventions (Google Maps URLs, GPS devices, etc.).
 * </p>
 * <p>
 * This class is designed to be:
 * <ul>
 *   <li>Immutable and thread-safe</li>
 *   <li>Serializable (for caching, RPC, etc.)</li>
 *   <li>Compatible with MapStruct, Jackson, and other reflection-based frameworks
 *       via the public {@code LatLng(String)} constructor</li>
 *   <li>A valid {@link ValueObject} with reversible {@link #getValue()}</li>
 * </ul>
 */
public final class LatLng implements ValueObject<String> {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    /**
     * The latitude in decimal degrees.
     */
    private final double latitude;

    /**
     * The longitude in decimal degrees.
     */
    private final double longitude;

    /**
     * Constructs a {@code LatLng} from raw latitude and longitude values.
     *
     * @param latitude  the latitude in decimal degrees [-90.0, 90.0]
     * @param longitude the longitude in decimal degrees [-180.0, 180.0]
     * @throws IllegalArgumentException if either coordinate is out of valid range
     */
    public LatLng(double latitude, double longitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException("Latitude must be between -90.0 and 90.0, but was: " + latitude);
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException("Longitude must be between -180.0 and 180.0, but was: " + longitude);
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Public constructor for use by mapping frameworks such as MapStruct, Jackson, or Spring Data.
     * <p>
     * Parses a string in the format {@code "latitude,longitude"} (e.g., {@code "39.9042,116.4074"}).
     * No whitespace is allowed around the comma in typical usage, but leading/trailing whitespace
     * in each part is trimmed during parsing.
     *
     * @param value the string representation; must not be null or empty
     * @throws IllegalArgumentException if the string is malformed or coordinates are out of range
     */
    public LatLng(String value) {
        Objects.requireNonNull(value, "Input string must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Input string must not be empty");
        }

        String[] parts = trimmed.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Expected exactly one comma separating latitude and longitude, got: '" + value + "'");
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(parts[0].trim());
            lng = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse number in: '" + value + "'", e);
        }

        if (lat < MIN_LATITUDE || lat > MAX_LATITUDE) {
            throw new IllegalArgumentException("Latitude out of range: " + lat);
        }
        if (lng < MIN_LONGITUDE || lng > MAX_LONGITUDE) {
            throw new IllegalArgumentException("Longitude out of range: " + lng);
        }

        this.latitude = lat;
        this.longitude = lng;
    }

    /**
     * Creates a {@code LatLng} from raw coordinates.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return a new {@code LatLng} instance
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public static LatLng of(double latitude, double longitude) {
        return new LatLng(latitude, longitude);
    }

    /**
     * Parses a string in "lat,lng" format into a {@code LatLng}.
     * <p>
     * This method is functionally equivalent to {@code new LatLng(value)},
     * but provides clearer intent in application code.
     *
     * @param value the string representation (e.g., "39.9042,116.4074")
     * @return a new {@code LatLng} instance
     * @throws IllegalArgumentException if the input is invalid
     */
    public static LatLng parse(String value) {
        return new LatLng(value);
    }

    /**
     * Returns the latitude in decimal degrees.
     *
     * @return the latitude value in [-90.0, 90.0]
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude in decimal degrees.
     *
     * @return the longitude value in [-180.0, 180.0]
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the canonical string representation: {@code "latitude,longitude"}.
     * <p>
     * Example: {@code "39.9042,116.4074"}
     * <p>
     * This format is reversible via {@link #parse(String)} or the {@code LatLng(String)} constructor.
     *
     * @return a string in "lat,lng" format
     */
    @Override
    public String getValue() {
        return latitude + "," + longitude;
    }

    /**
     * Returns a new {@code LatLng} with coordinates rounded to the specified number of decimal places.
     * <p>
     * Common precision guide:
     * <ul>
     *   <li>0 decimals ≈ 111 km</li>
     *   <li>3 decimals ≈ 111 m</li>
     *   <li>5 decimals ≈ 1 m</li>
     *   <li>6 decimals ≈ 0.1 m</li>
     * </ul>
     *
     * @param decimalPlaces number of decimal places (must be ≥ 0)
     * @return a new {@code LatLng} with rounded coordinates
     * @throws IllegalArgumentException if {@code decimalPlaces} is negative
     */
    public LatLng round(int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative");
        }
        if (decimalPlaces == 0 && latitude == (long) latitude && longitude == (long) longitude) {
            return this; // optimization for whole numbers
        }
        double scale = Math.pow(10, decimalPlaces);
        double roundedLat = Math.round(latitude * scale) / scale;
        double roundedLng = Math.round(longitude * scale) / scale;

        // Re-clamp in rare cases where rounding pushes value slightly beyond bounds (e.g., 90.0000001)
        roundedLat = Math.max(MIN_LATITUDE, Math.min(MAX_LATITUDE, roundedLat));
        roundedLng = Math.max(MIN_LONGITUDE, Math.min(MAX_LONGITUDE, roundedLng));

        return new LatLng(roundedLat, roundedLng);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLng that = (LatLng) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "lat=" + latitude +
                ", lng=" + longitude +
                '}';
    }
}
