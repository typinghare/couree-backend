package couree.com.luckycat.core.common;

import java.security.SecureRandom;

public class Verifications {
    /**
     * Returns a code with a specified number of figures.
     * @param length a specified number of figures of the code
     * @return a code with a specified number of figures
     * @author James Chan
     */
    public static String generateCode(int length) {
        final SecureRandom secureRandom = new SecureRandom();

        if (length < 10) {
            final int nextInt = secureRandom.nextInt((int) Math.pow(10, 9), Integer.MAX_VALUE);
            final String str = String.valueOf(nextInt);
            return str.substring(10 - length);
        } else {
            final StringBuilder builder = new StringBuilder();
            for (int n = length / 9; n > 0; n--)
                builder.append(generateCode(9));
            builder.append(generateCode(length % 9));

            return builder.toString();
        }
    }
}
