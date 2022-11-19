package couree.com.luckycat.glacier;

import couree.com.luckycat.core.annotation.Initializer;
import couree.com.luckycat.glacier.annotation.ApplicationConfiguration;
import couree.com.luckycat.glacier.annotation.Metadata;
import couree.com.luckycat.glacier.constant.StartupPhase;
import couree.com.luckycat.glacier.exception.DuplicateApplicationNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class Application {
    public static final String FRAMEWORK_NAME = "Glacier";

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final ApplicationContext applicationContext;

    private final Registry registry;

    private final Set<String> applicationNameSet = new HashSet<>();

    private final GlobalVariableWarehouse globalVariableWarehouse;

    public Application(
            ApplicationContext applicationContext,
            Registry registry,
            GlobalVariableWarehouse globalVariableWarehouse
    ) {
        this.applicationContext = applicationContext;
        this.registry = registry;
        this.globalVariableWarehouse = globalVariableWarehouse;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        logger.info(FRAMEWORK_NAME + " is starting...");
        final long startTimeMillis = System.currentTimeMillis();
        globalVariableWarehouse.store(StartupPhase.GLACIER_STARTING);

        // applications
        final Map<String, Object> applicationConfigurationObjectMap
                = applicationContext.getBeansWithAnnotation(ApplicationConfiguration.class);

        for (final Object applicationConfigurationObject : applicationConfigurationObjectMap.values()) {
            registerApplication(applicationConfigurationObject);
        }

        // initializers
        final CoreBeanPostProcessor coreBeanPostProcessor = applicationContext.getBean(CoreBeanPostProcessor.class);
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


        globalVariableWarehouse.store(StartupPhase.COMPLETED);
        logger.info(String.format(
                "Started %s in %.2f seconds.",
                FRAMEWORK_NAME,
                (float) (System.currentTimeMillis() - startTimeMillis) / 1000
        ));
    }

    public void registry() {

    }

    /**
     * Registers an application.
     * @param configurationObject application configuration object
     */
    private void registerApplication(Object configurationObject) {
        final Class<?> configurationClass = configurationObject.getClass();
        final ApplicationConfiguration applicationConfiguration
                = configurationClass.getAnnotation(ApplicationConfiguration.class);

        // register application's name
        final String applicationName = applicationConfiguration.name();
        if (!applicationNameSet.add(applicationName)) {
            throw new DuplicateApplicationNameException(applicationName);
        }

        // registry
        final Metadata[] metadataArray = applicationConfiguration.metadata();
        Arrays.stream(metadataArray).forEach(metadata -> registerMetadata(applicationName, metadata));
    }

    private void registerMetadata(String applicationName, Metadata metadata) {
        final String key = applicationName + metadata.key();
        registry.addEntry(key, metadata.type(), metadata.description(), metadata.updatable());
    }
}
