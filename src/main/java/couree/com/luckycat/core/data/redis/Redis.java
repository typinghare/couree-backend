package couree.com.luckycat.core.data.redis;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Redis {
    public static Key key(Object... keys) {
        return new Key(keys);
    }

    /**
     * A redis key.
     */
    public static class Key {
        /**
         * The delimiter of keys.
         */
        private final static String DELIMITER = ":";

        private final String key;

        private Key(Object... keys) {
            key = String.join(DELIMITER, Arrays.stream(keys).map(Object::toString).toList());
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
