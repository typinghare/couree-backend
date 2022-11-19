package com.couree.luckycat.glacier;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.annotation.Metadata;
import com.couree.luckycat.glacier.annotation.RegistryKey;
import com.couree.luckycat.glacier.exception.DuplicateApplicationNameException;
import com.couree.luckycat.glacier.exception.MissingApplicationException;
import com.couree.luckycat.glacier.exception.NoMetadataDefinedException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
public class ApplicationManager {
    private final ApplicationContext applicationContext;

    private final Registry registry;

    /**
     * Mapping: application name => application configuration class.
     */
    private final Map<String, Class<?>> applicationMap = new HashMap<>();

    private final Set<String> loadedApplication = new HashSet<>();

    public ApplicationManager(ApplicationContext applicationContext, Registry registry) {
        this.applicationContext = applicationContext;
        this.registry = registry;
    }

    private void register(String applicationName, Class<?> configurationClass) {
        if (configurationClass == null) {
            return;
        }

        if (applicationMap.containsKey(applicationName)) {
            throw new DuplicateApplicationNameException(applicationName);
        }

        applicationMap.put(applicationName, configurationClass);
    }

    public void bootstrap() {
        // register all applications
        final Map<String, Object> applicationConfigurationObjectMap
            = applicationContext.getBeansWithAnnotation(ApplicationConfiguration.class);

        for (final Object applicationConfigurationObject : applicationConfigurationObjectMap.values()) {
            final Class<?> applicationConfigurationClass = applicationConfigurationObject.getClass();
            final ApplicationConfiguration applicationConfiguration
                = applicationConfigurationClass.getAnnotation(ApplicationConfiguration.class);

            if (applicationConfiguration != null) {
                register(applicationConfiguration.name(), applicationConfigurationClass);
            }
        }

        // load applications
        for (final String applicationName : applicationMap.keySet()) {
            load(applicationName);
        }
    }

    private void load(String applicationName) {
        if (loadedApplication.contains(applicationName)) {
            return;
        }

        final Class<?> configurationClass = applicationMap.get(applicationName);
        if (configurationClass == null)
            throw new MissingApplicationException(applicationName);

        final ApplicationConfiguration applicationConfiguration
            = configurationClass.getAnnotation(ApplicationConfiguration.class);

        // load dependencies
        for (final String dependency : applicationConfiguration.dependency()) {
            load(dependency);
        }

        // load registry metadata entries
        final Metadata[] metadataArray = applicationConfiguration.metadata();
        Arrays.stream(metadataArray).forEach(metadata -> registerMetadata(applicationName, metadata));

        // registry entries
        final Object configurationObject = applicationContext.getBean(configurationClass);
        for (final Field field : configurationClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(RegistryKey.class))
                continue;

            final String key = field.getAnnotation(RegistryKey.class).value();
            final Registry.Metadata metadata = registry.getMetadata(key);
            if (metadata == null) {
                throw new NoMetadataDefinedException(key, configurationClass);
            }

            registry.addFieldLocation(key, configurationObject, field, metadata.getType());

            try {
                field.setAccessible(true);
                registry.addEntryRecord(key, field.get(configurationObject), configurationClass);
            } catch (IllegalAccessException ignore) {
            }
        }

        loadedApplication.add(applicationName);
    }

    private void registerMetadata(String applicationName, Metadata metadata) {
        final String key = applicationName + "." + metadata.key();
        registry.addMetadataEntry(key, metadata.type(), metadata.description(), metadata.updatable());
    }
}
