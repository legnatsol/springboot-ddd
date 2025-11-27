package io.github.legnatsol.springboot.ddd.model;

import io.github.legnatsol.springboot.ddd.domain.ValueObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A value object representing a mobile phone number in international format.
 * <p>
 * The expected format is: {@code +<countryCode>-<number>}, for example: {@code +86-13812345678}.
 * This class ensures the input string conforms to this structure and parses it into
 * a country code (integer) and subscriber number (string).
 * </p>
 * <p>
 * Instances are immutable and suitable for use as map keys or in collections.
 * </p>
 */
public final class MobileNumber implements ValueObject<String> {

    private static final String MOBILE_NUMBER_REGEX = "^\\+([1-9][0-9]*)-([0-9]{1,15})$";
    private static final Pattern PATTERN = Pattern.compile(MOBILE_NUMBER_REGEX);

    /**
     * The country calling code
     */
    private final int countryCode;

    /**
     * The national (subscriber) mobile number without leading zeros or country code.
     * Length is typically between 8 and 15 digits depending on the country.
     */
    private final String number;

    /**
     * Private constructor to enforce validation via static factory method.
     *
     * @param rawInput the raw mobile number string in "+CC-NUMBER" format.
     * @throws IllegalArgumentException if the format is invalid or components are out of range.
     */
    private MobileNumber(String rawInput) {
        Objects.requireNonNull(rawInput, "Mobile number must not be null");

        final Matcher matcher = PATTERN.matcher(rawInput);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid mobile number format: '" + rawInput + "'. Expected format: +<countryCode>-<number>"
            );
        }

        String countryCodeStr = matcher.group(1);
        String numberStr = matcher.group(2);

        // Validate country code range (ITU-T E.164 allows 1 to 999)
        try {
            int cc = Integer.parseInt(countryCodeStr);
            if (cc < 1 || cc > 999) {
                throw new IllegalArgumentException("Country code must be between 1 and 999: " + cc);
            }
            this.countryCode = cc;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid country code: " + countryCodeStr, e);
        }

        // Optional: validate number length per ITU-T E.164 (max 15 digits total, minus CC digits)
        // Here we assume the number part is already within reasonable bounds (1–15 digits)
        this.number = numberStr;
    }

    /**
     * Creates a {@link MobileNumber} from a string in international format.
     *
     * @param mobileNumber the mobile number string, e.g., "+86-13812345678".
     * @return a new {@code MobileNumber} instance.
     * @throws IllegalArgumentException if the input is null or does not match the required format.
     */
    public static MobileNumber of(String mobileNumber) {
        return new MobileNumber(mobileNumber);
    }

    /**
     * Returns the mobile number in its canonical string representation: "+{countryCode}-{number}".
     *
     * @return the formatted mobile number string.
     */
    @Override
    public String getValue() {
        return "+" + countryCode + "-" + number;
    }

    /**
     * Returns the country calling code.
     *
     * @return the numeric country code
     */
    public int getCountryCode() {
        return countryCode;
    }

    /**
     * Returns the subscriber (national) mobile number part.
     *
     * @return the number string without country code or '+' prefix.
     */
    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileNumber that = (MobileNumber) o;
        return countryCode == that.countryCode && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }

    @Override
    public String toString() {
        return "MobileNumber{" + getValue() + '}';
    }
}
