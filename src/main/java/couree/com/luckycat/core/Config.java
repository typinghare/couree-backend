package couree.com.luckycat.core;

import couree.com.luckycat.core.annotation.Initializer;
import couree.com.luckycat.core.annotation.InitializerContainer;
import couree.com.luckycat.core.annotation.RegistryEntries;
import couree.com.luckycat.core.annotation.RegistryEntry;
import couree.com.luckycat.core.common.Loads;
import couree.com.luckycat.core.constant.StartupPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The application config.
 * @author James Chan
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class Config {
    /**
     * The Spring annotated application context.
     */
    private static ApplicationContext applicationContext;

    /**
     * Singleton instance.
     */
    private static Config _instance;

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    /**
     * The registry for this application.
     */
    private final Registry registry;

    /**
     * The startup phase of this application.
     */
    private StartupPhase startupPhase = StartupPhase.CORE_STARTING;

    /**
     * The constructor is private so that instantiating this class is prevented.
     * @param applicationContext the Spring annotated application context
     */
    @Autowired
    private Config(
            ApplicationContext applicationContext,
            ConfigurableEnvironment configurableEnvironment,
            Registry registry
    ) {
        Config.applicationContext = applicationContext;
        this.registry = registry;

        registerEnvironmentVariables(configurableEnvironment);
        initializeRegistry(configurableEnvironment);
    }

    /**
     * Registers application environment variables. Application environment file is ".env.properties". Note that this
     * file is not staged by git, so it need to be created before running this application.
     */
    private void registerEnvironmentVariables(ConfigurableEnvironment configurableEnvironment) {
        final Properties envProperties = Loads.loadProperties(".env.properties");
        envProperties.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

        // register to SpringBoot environment property source
        final PropertiesPropertySource propertiesPropertySource
                = new PropertiesPropertySource("EnvVariable", envProperties);
        configurableEnvironment.getPropertySources().addFirst(propertiesPropertySource);
    }

    /**
     * Initialize registry.
     */
    private void initializeRegistry(ConfigurableEnvironment configurableEnvironment) {
        final Properties properties = new Properties();
        final Map<String, Object> registryClassMap
                = applicationContext.getBeansWithAnnotation(couree.com.luckycat.core.annotation.Registry.class);

        final List<Object[]> priorityList = new ArrayList<>();
        for (Object registryClass : registryClassMap.values()) {
            final couree.com.luckycat.core.annotation.Registry registryAnnotation
                    = registryClass.getClass().getAnnotation(couree.com.luckycat.core.annotation.Registry.class);
            priorityList.add(new Object[]{registryAnnotation.value(), registryClass});
        }
        priorityList.sort(Comparator.comparingInt(o -> ((Integer) o[0])));

        for (Object registryInstance : priorityList.stream().map(o -> o[1]).toList()) {
            final Class<?> registryClass = registryInstance.getClass();
            final RegistryEntries registryEntries = registryClass.getAnnotation(RegistryEntries.class);

            if (registryEntries != null) {
                for (RegistryEntry registryEntry : registryEntries.value()) {
                    final String key = registryEntry.key();
                    final String value = Pattern
                            .compile("^\\$\\{[A-Za-z.-_]*}$")
                            .matcher(registryEntry.value())
                            .replaceAll(matchResult -> {
                                final String group = matchResult.group();
                                final String val
                                        = configurableEnvironment.getProperty(group.substring(2, group.length() - 1));
                                return val == null ? "" : val;
                            });
                    properties.put(key, value);
                    registry.addEntry(key, value, registryClass);
                }
            }

            // register fields
            for (Field field : registryClass.getDeclaredFields()) {
                final RegistryEntry registryEntry = field.getAnnotation(RegistryEntry.class);
                if (registryEntry == null)
                    continue;

                final String key = registryEntry.key();
                field.setAccessible(true);
                try {
                    final Object value = field.get(registryInstance);
                    properties.put(key, value.toString());
                    registry.addEntry(key, value.toString(), registryClass);
                } catch (Exception ignore) {
                }
            }
        }

        final PropertiesPropertySource propertiesPropertySource
                = new PropertiesPropertySource("CoreRegistry", properties);
        configurableEnvironment.getPropertySources().addFirst(propertiesPropertySource);
    }

    /**
     * Returns the Spring annotated application context. This method should be the only way for developers to obtain the
     * application context object.
     * @return the Spring annotated application context.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Returns config instance.
     * @return config instance
     */
    public static Config instance() {
        if (_instance == null) {
            _instance = applicationContext.getBean(Config.class);
        }
        return _instance;
    }

    /**
     * Initialization. Start the Luckycat core. Starting time will be recorded and logged.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        startupPhase = StartupPhase.CORE_STARTING;
        logger.info("Luckycat core is starting...");
        final long startTimeMillis = System.currentTimeMillis();

        actuate();

        startupPhase = StartupPhase.COMPLETED;
        logger.info(String.format("Started Luckycat core in %.2f seconds.",
                (float) (System.currentTimeMillis() - startTimeMillis) / 1000)
        );
    }

    /**
     * Actuate core.
     */
    private void actuate() {
        // actuate initializers
        final Map<String, Object> initializerContainerMap = applicationContext.getBeansWithAnnotation(InitializerContainer.class);
        for (final Object initializerContainer : initializerContainerMap.values()) {
            try {
                for (final Method method : initializerContainer.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Initializer.class)) {
                        method.setAccessible(true);
                        method.invoke(initializerContainer);
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(String.format("Fail to actuate initializer in [%s].", initializerContainer.getClass()));
            }
        }
    }

    /**
     * Returns the  corresponding registry value of the given key.
     * @param key ket to find.
     * @return corresponding registry value of the given key
     */
    public String getRegistryValue(String key) {
        final String value = registry.getValue(key);
        if (value == null) {
            throw new RuntimeException(String.format(
                    "Key [%s] is not found in the registry", key
            ));
        }
        return value;
    }

    public StartupPhase getStartupPhase() {
        return startupPhase;
    }
}
