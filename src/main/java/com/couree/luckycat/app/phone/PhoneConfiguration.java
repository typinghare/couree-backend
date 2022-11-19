package com.couree.luckycat.app.phone;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import org.springframework.beans.factory.annotation.Value;

@ApplicationConfiguration(name = "Phone", metadata = {
    @Metadata(key = "Twilio.AccountSid"),
    @Metadata(key = "Twilio.AuthToken"),
    @Metadata(key = "Twilio.ServiceSid"),
})
public class PhoneConfiguration {
    @Value("${twilio.account-sid}")
    @RegistryKey("Phone.Twilio.AccountSid")
    public String accountSid;

    @Value("${twilio.auth-token}")
    @RegistryKey("Phone.Twilio.AuthToken")
    public String authToken;

    @Value("${twilio.service-sid}")
    @RegistryKey("Phone.Twilio.ServiceSid")
    public String serviceId;
}
