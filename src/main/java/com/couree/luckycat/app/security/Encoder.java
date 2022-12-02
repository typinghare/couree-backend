package com.couree.luckycat.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A general encoder.
 * @author James Chan.
 * @see PasswordEncoder
 */
@Component
public class Encoder {
    private final SecurityConfiguration securityConfiguration;

    /**
     * Encoder maps. Mapping: secret => password encoder of the secret.
     */
    private final Map<String, PasswordEncoder> encoderMap = new HashMap<>();

    @Autowired
    private Encoder(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    /**
     * Encodes the raw password.
     * @return an encoded string
     */
    public final String encode(CharSequence rawPassword, String secret) {
        return getEncoder(secret).encode(rawPassword);
    }

    /**
     * Verifies the encoded password.
     * @return true if the password matches, false otherwise
     */
    public final boolean matches(CharSequence rawPassword, String encodedPassword, String secret) {
        return getEncoder(secret).matches(rawPassword, encodedPassword);
    }

    /**
     * Returns the encoder of a specified secret.
     */
    private PasswordEncoder getEncoder(String secret) {
        PasswordEncoder encoder = encoderMap.get(secret);
        if (encoder == null) {
            try {
                encoder = securityConfiguration.passwordEncoderClass
                    .getConstructor(CharSequence.class)
                    .newInstance(secret);
                encoderMap.put(secret, encoder);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        return encoder;
    }
}
