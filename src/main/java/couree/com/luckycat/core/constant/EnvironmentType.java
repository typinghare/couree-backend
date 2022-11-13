package couree.com.luckycat.core.constant;

/**
 * Development test Environment types.
 * @author James Chan
 * @see <a href="https://www.unitrends.com/blog/development-test-environments">Development Test Envrionment</a>
 */
public enum EnvironmentType {
    /**
     * Environment where application development tasks, such as designing, programming debugging, etc. take place
     */
    DEVELOPMENT,

    /**
     * Environment where application testing is conducted to find and fix errors.
     */
    TEST,

    /**
     * Environment where application is merged into the built system.
     */
    STAGING,

    /**
     * Environment where new builds/updates are moved into production for end users
     */
    PRODUCTION
}
