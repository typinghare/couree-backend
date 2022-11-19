package com.couree.luckycat.glacier;

import com.couree.luckycat.glacier.annotation.Initializer;
import com.couree.luckycat.glacier.common.Loads;
import com.couree.luckycat.glacier.constant.StartupPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

@Component(Application.APPLICATION_COMPONENT_NAME)
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@PropertySource("classpath:/" + Application.ENVIRONMENT_PROPERTY_FILENAME)
public class Application {
    public static final String FRAMEWORK_NAME = "Glacier";

    public static final String APPLICATION_COMPONENT_NAME = FRAMEWORK_NAME + "Application";

    public static final String ENVIRONMENT_PROPERTY_FILENAME = ".env.properties";

    public static final String RUNNING_TIME_KEY = "RunningTime";

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final ApplicationContext applicationContext;

    private final GlobalVariableWarehouse globalVariableWarehouse;

    @Autowired
    public Application(
        ApplicationContext applicationContext,
        GlobalVariableWarehouse globalVariableWarehouse,
        ApplicationManager applicationManager,
        ConfigurableEnvironment configurableEnvironment
    ) {
        this.applicationContext = applicationContext;
        this.globalVariableWarehouse = globalVariableWarehouse;

        final long startMillis = System.currentTimeMillis();

        // register environment variables
        registerEnvironmentVariables(configurableEnvironment);

        // bootstrap applications
        applicationManager.bootstrap();

        // add registry entries to the SpringBoot's environment
        final Properties registryEntries = new Properties();
        final Registry registry = applicationContext.getBean(Registry.class);

        registry.metadataSet().forEach((Map.Entry<String, Registry.Metadata> entry)
            -> registryEntries.put(entry.getKey(), entry.getValue().value()));

        final PropertiesPropertySource propertiesPropertySource
            = new PropertiesPropertySource("Registry", registryEntries);
        configurableEnvironment.getPropertySources().addFirst(propertiesPropertySource);

        globalVariableWarehouse.store(RUNNING_TIME_KEY, System.currentTimeMillis() - startMillis);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        logger.info(FRAMEWORK_NAME + " is starting...");
        final long startTimeMillis = System.currentTimeMillis();
        globalVariableWarehouse.store(StartupPhase.GLACIER_STARTING);

        // initializers
        bootstrapInitializers();

        globalVariableWarehouse.store(StartupPhase.COMPLETED);

        final Long usedTime = globalVariableWarehouse.getLong(RUNNING_TIME_KEY);
        logger.info(String.format(
            "Started %s in %.2f seconds (main).",
            FRAMEWORK_NAME,
            (float) (System.currentTimeMillis() - startTimeMillis) / 1000
        ));
        logger.info(String.format(
            "Total starting time of %s is %.2f seconds (including Registry bootstrap).",
            FRAMEWORK_NAME,
            (float) (usedTime + System.currentTimeMillis() - startTimeMillis) / 1000
        ));
    }

    /**
     * Registers application environment variables. Application environment file is ".env.properties". Note
     * that this file is not staged by git, so it need to be created before running this application.
     */
    private void registerEnvironmentVariables(ConfigurableEnvironment configurableEnvironment) {
        final Properties envProperties = Loads.loadProperties(ENVIRONMENT_PROPERTY_FILENAME);
        envProperties.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

        // register to SpringBoot environment property source
        final PropertiesPropertySource propertiesPropertySource
            = new PropertiesPropertySource("EnvVariable", envProperties);
        configurableEnvironment.getPropertySources().addFirst(propertiesPropertySource);
    }

    private void bootstrapInitializers() {
        final InitializerBeanPostProcessor coreBeanPostProcessor = applicationContext.getBean(InitializerBeanPostProcessor.class);
        for (final Object initializerContainer : coreBeanPostProcessor.getInitializerContainerList()) {
            final Class<?> initializerContainerClass = initializerContainer.getClass();
            try {
                for (final Method method : initializerContainerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Initializer.class)) {
                        method.setAccessible(true);
                        method.invoke(initializerContainer);
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.getCause().printStackTrace();
                throw new RuntimeException(String.format(
                    "Fail to actuate initializer in [ %s ].",
                    initializerContainerClass
                ));
            }
        }
    }
}
