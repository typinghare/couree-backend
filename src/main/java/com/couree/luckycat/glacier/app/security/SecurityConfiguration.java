package com.couree.luckycat.glacier.app.security;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.constant.RegistryType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@ApplicationConfiguration(name = "Security", metadata = {
    @Metadata(key = "PasswordEncoderClass", type = RegistryType.CLASS)
})
public class SecurityConfiguration {
    @RegistryKey("Security.PasswordEncoderClass")
    public Class<? extends PasswordEncoder> passwordEncoderClass = Pbkdf2PasswordEncoder.class;

    private SecurityConfiguration() {
    }
}
