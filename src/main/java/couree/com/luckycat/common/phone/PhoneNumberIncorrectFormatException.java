package couree.com.luckycat.common.phone;

public class PhoneNumberIncorrectFormatException extends RuntimeException {
    PhoneNumberIncorrectFormatException(String phoneNumberString) {
        super(String.format("Incorrect phone number format: [%s].", phoneNumberString));
    }
}
