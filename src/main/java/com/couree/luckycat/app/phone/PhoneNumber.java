package com.couree.luckycat.app.phone;

import com.couree.luckycat.app.phone.exception.PhoneNumberIncorrectFormatException;

/**
 * @author James Chan
 */
public class PhoneNumber {
    /**
     * The separator that separates the country code and real phone number.
     */
    private final static String SEPARATOR = "&";

    /**
     * Country calling code, or simply country code.
     * @see <a href="https://countrycode.org">countrycode.org</a>
     */
    private final int countryCode;

    /**
     * The real phone number after the country code.
     */
    private final String phoneNumber;

    /**
     * Creates a phone number
     */
    private PhoneNumber(int countryCode, String phoneNumber) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets a phone number instance.
     * @param phoneNumberString the raw phone number string
     * @return a phone number instance
     */
    public static PhoneNumber of(String phoneNumberString) {
        final String[] sp = phoneNumberString.split(SEPARATOR);
        if (sp.length != 2) {
            throw new PhoneNumberIncorrectFormatException(phoneNumberString);
        }

        try {
            return new PhoneNumber(Integer.parseInt(sp[0]), sp[1]);
        } catch (NumberFormatException e) {
            throw new PhoneNumberIncorrectFormatException(phoneNumberString);
        }
    }

    /**
     * Returns the country code of this phone number.
     * @return the country code of this phone number
     */
    public int getCountryCode() {
        return countryCode;
    }

    /**
     * Returns the real phone number of this phone number.
     * @return the real phone number of this phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Converts this phone number to a formal string.
     * @return the formal string of this phone number.
     */
    public String toFormalString() {
        return String.format("+%d%s", countryCode, phoneNumber);
    }

    /**
     * Returns the phone number string.
     * @return the phone number string
     */
    @Override
    public String toString() {
        return String.format("%s%s%s", countryCode, SEPARATOR, phoneNumber);
    }
}
