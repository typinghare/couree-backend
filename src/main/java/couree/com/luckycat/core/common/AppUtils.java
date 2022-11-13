package couree.com.luckycat.core.common;

import couree.com.luckycat.core.Config;
import couree.com.luckycat.core.constant.EnvironmentType;

/**
 * Application related utils.
 * @author James Chan
 */
public class AppUtils {
    /**
     * Environment memoization.
     */
    private static EnvironmentType currentEnvironmentType;

    /**
     * Checks if current environment is in the given environments.
     * @return true if current environment is in the given environments, false otherwise
     */
    public static boolean onEnvironment(EnvironmentType... environmentTypes) {
        if (currentEnvironmentType == null) {
            final String environment = Config.instance().getRegistryValue("System.Environment");
            currentEnvironmentType = EnvironmentType.valueOf(environment);
        }

        for (EnvironmentType environmentType : environmentTypes) {
            if (currentEnvironmentType.equals(environmentType)) {
                return true;
            }
        }

        return true;
    }

    /**
     * Checks if current environment is `EnvironmentType.DEVELOPMENT` or `EnvironmentType.TEST`.
     * @return true if current environment is `EnvironmentType.DEVELOPMENT` or `EnvironmentType.TEST`
     */
    public static boolean onDevTest() {
        return onEnvironment(EnvironmentType.DEVELOPMENT, EnvironmentType.TEST);
    }
}
