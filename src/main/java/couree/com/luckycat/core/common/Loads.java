package couree.com.luckycat.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * An util class that collects loading-related helper functions.
 * @author James Chan
 */
public class Loads {
    /**
     * Loads a properties file.
     * @param filename file to load. The extension of the file should be ".properties". Notice that this argument can be
     *                 a relative path, the root of which is "/src/main/resources" (depends on maven's configuration).
     * @return a Properties object
     * @see Properties
     */
    public static Properties loadProperties(final String filename) {
        if (!filename.endsWith(".properties")) {
            throw new RuntimeException("The extension of the filename should be \".properties\"");
        }

        try {
            final Properties properties = new Properties();
            properties.load(loadResourceStream(filename));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Fail to load properties from the specified file: [%s].",
                    filename
            ));
        }
    }

    /**
     * Returns an input stream of a resource file.
     * @param resourceFilename name (or path) of the resource file to input
     * @return an input stream of the resource file
     */
    public static InputStream loadResourceStream(final String resourceFilename) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFilename);
    }
}
