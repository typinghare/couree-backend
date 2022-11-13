package couree.com.luckycat.common.phone;

import couree.com.luckycat.core.annotation.Registry;
import couree.com.luckycat.core.annotation.RegistryEntry;
import org.springframework.beans.factory.annotation.Value;

@Registry
public class PhoneServiceRegistry {
    @Value("${twilio.account-sid}")
    @RegistryEntry(key = "Twilio.AccountSid")
    public String accountSid;

    @Value("${twilio.auth-token}")
    @RegistryEntry(key = "Twilio.AuthToken")
    public String authToken;

    @Value("${twilio.service-sid}")
    @RegistryEntry(key = "Twilio.ServiceSid")
    public String serviceId;
}
