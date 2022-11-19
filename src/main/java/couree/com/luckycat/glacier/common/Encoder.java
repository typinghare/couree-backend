package couree.com.luckycat.glacier.common;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A general encoder.
 * @author James Chan.
 */
@Component
public class Encoder {
    /**
     * A specified PasswordEncoder implementation class.
     */
    private static final Class<? extends PasswordEncoder> passwordEncoderClass = Pbkdf2PasswordEncoder.class;

    /**
     * Encoder maps. Mapping: secret => password encoder of the secret.
     */
    private final Map<String, PasswordEncoder> encoderMap = new HashMap<>();

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
                encoder = passwordEncoderClass.getConstructor(CharSequence.class).newInstance(secret);
                encoderMap.put(secret, encoder);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        return encoder;
    }
}
