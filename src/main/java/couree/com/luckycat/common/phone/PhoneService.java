package couree.com.luckycat.common.phone;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import couree.com.luckycat.core.annotation.Initializer;
import couree.com.luckycat.core.annotation.InitializerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author James Chan
 */
@Service
@InitializerContainer
public class PhoneService {
    private final PhoneServiceRegistry phoneServiceRegistry;

    @Autowired
    private PhoneService(PhoneServiceRegistry phoneServiceRegistry) {
        this.phoneServiceRegistry = phoneServiceRegistry;
    }

    @Initializer
    private void init() {
        Twilio.init(phoneServiceRegistry.accountSid, phoneServiceRegistry.authToken);
    }

    /**
     * Returns the message with prefix.
     * @param message message to send
     * @return the message with prefix
     */
    private String getMessage(String message) {
        return "[Couree]" + message;
    }

    /**
     * Sends a message to the specified phone number.
     * @param phoneNumber phone number to sent
     * @param message     message to send
     * @return true if successfully send the message
     */
    public boolean sendMessage(PhoneNumber phoneNumber, String message) {
        final String messageToSend = getMessage(message);
        final var to = new com.twilio.type.PhoneNumber(phoneNumber.toFormalString());
        final Message twilioMessage
                = Message.creator(to, phoneServiceRegistry.serviceId, messageToSend).create();

        return twilioMessage.getStatus() == Message.Status.ACCEPTED;
    }

    /**
     * Sends a verification code message to a specified phone number.
     * @param phoneNumber      phone number to send to
     * @param verificationCode verification code to send
     * @param expiration       the expiration of the verification code
     * @return true if successfully send the message
     */
    public boolean sendVerificationCode(
            PhoneNumber phoneNumber,
            String verificationCode,
            int expiration
    ) {
        final int minute = expiration / 60;
        final int second = expiration % 60;
        final String expirationString = second == 0 ?
                String.format("%s minutes.", minute) :
                String.format("%s minutes %s seconds.", minute, second);

        return sendMessage(phoneNumber, String.format("Your verification code is: %s. " +
                "This code will be expired in %s.", verificationCode, expirationString));
    }
}
