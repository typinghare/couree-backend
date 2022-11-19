package com.couree.luckycat.glacier;

import com.couree.luckycat.glacier.annotation.ApplicationConfiguration;
import com.couree.luckycat.glacier.exception.NoMetadataDefinedException;
import com.couree.luckycat.glacier.exception.UninitializedRegistryEntryException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Registry is a significant part of glacier. Users can register their application by creating a configuration
 * class, and then annotate it by {@link ApplicationConfiguration}. Users should define the name of the
 * application, the dependencies (if there are), metadata of registry entries. The name of the application
 * should be consistent with the package name, and it should be upper camel case. Note that each application
 * should have only one configuration class.
 * <p>
 * The dependencies are those applications that the application requires. Registry bootstrap program will
 * throw an exception if one or more dependencies is missing.
 * <p>
 * The registry entries are some variables that being loaded at the startup phase. They will be put to the
 * SpringBoot's environment then. Therefore, users can inject the registry entries just like injecting the
 * application properties by {@link org.springframework.beans.factory.annotation.Value}.
 * @author James Chan
 * @version 1.0
 * @see ApplicationConfiguration
 */
@Component
public class Registry {
    /**
     * Mapping: key => Metadata.
     */
    private final Map<String, Metadata> keyMetadataMap = new HashMap<>();

    /**
     * Mapping key => field location list.
     */
    private final Map<String, List<FieldLocation>> fieldLocationListMap = new HashMap<>();

    /**
     * Adds a metadata entry.
     * @param key         key of the metadata entry
     * @param type        type of the metadata entry
     * @param description description of the metadata entry
     * @param updatable   whether the value of the entry can be updated
     */
    public void addMetadataEntry(String key, String type, String description, boolean updatable) {
        if (keyMetadataMap.containsKey(key)) {
            throw new RuntimeException(String.format("Registry key [ %s ] exists.", key));
        }

        keyMetadataMap.put(key, new Metadata(key, type, description, updatable));
    }

    /**
     * Returns a metadata entry.
     * @param key key of the metadata entry
     * @return the metadata entry of the specified key
     */
    public Metadata getMetadata(String key) {
        return keyMetadataMap.get(key);
    }

    /**
     * Adds a field location.
     * @param key   key of the registry entry
     * @param bean  bean of the field
     * @param field field
     */
    public void addFieldLocation(String key, Object bean, Field field, String type) {
        fieldLocationListMap.putIfAbsent(key, new ArrayList<>());
        final List<FieldLocation> fieldLocationList = fieldLocationListMap.get(key);

        fieldLocationList.add(new FieldLocation(bean, field, type));
    }

    /**
     * Adds an entry record to an entry.
     * @param key         key of the entry
     * @param value       value of the entry record
     * @param sourceClass source class of the entry record comes from
     */
    public void addEntryRecord(String key, String value, Class<?> sourceClass) {
        final Metadata metadata = keyMetadataMap.get(key);
        if (metadata == null) {
            throw new NoMetadataDefinedException(key, sourceClass);
        }

        if (!metadata.addEntryRecord(value, sourceClass)) {
            throw new RuntimeException(String.format("Registry entry [ %s ] is not updatable.", key));
        }

        // synchronize all bound fields
        synchronizeFields(key, value, sourceClass);
    }

    /**
     * Adds an entry record to an entry.
     * @param key         key of the entry
     * @param value       value of the entry record
     * @param sourceClass source class of the entry record comes from
     */
    public void addEntryRecord(String key, Object value, Class<?> sourceClass) {
        if (value instanceof Class<?>) {
            addEntryRecord(key, (Class<?>) value, sourceClass);
        } else {
            addEntryRecord(key, value.toString().trim(), sourceClass);
        }
    }

    public void addEntryRecord(String key, Class<?> value, Class<?> sourceClass) {
        addEntryRecord(key, value.getName(), sourceClass);
    }

    public Set<Map.Entry<String, Metadata>> metadataSet() {
        return keyMetadataMap.entrySet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void synchronizeFields(String key, String value, Class<?> sourceClass) {
        for (final FieldLocation fieldLocation : fieldLocationListMap.get(key)) {
            if (sourceClass.equals(fieldLocation.getClass()))
                continue;

            final Field field = fieldLocation.field;
            field.setAccessible(true);

            try {
                switch (fieldLocation.type) {
                    case "Class" -> field.set(fieldLocation.bean, Class.forName(value));
                    case "Enum" -> {
                        final Class<?> enumClass = field.getType();
                        if (!enumClass.isEnum()) {
                            throw new RuntimeException("Cannot convert into enum.");
                        }

                        final Object enumObject = Enum.valueOf((Class<Enum>) field.getType(), value);
                        field.set(fieldLocation.bean, enumObject);
                    }
                    case "String" -> field.set(fieldLocation.bean, value);
                    case "Number" -> {
                        final Class<?> fieldType = field.getType();
                        if (fieldType.equals(Integer.class)) {
                            field.set(fieldLocation.bean, Integer.parseInt(value));
                        } else if (fieldType.equals(Long.class)) {
                            field.set(fieldLocation.bean, Long.parseLong(value));
                        }
                    }
                    case "Boolean" -> field.set(fieldLocation.bean, Boolean.parseBoolean(value));
                }
            } catch (IllegalAccessException ignore) {
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Registry Metadata.
     */
    public static class Metadata {
        /**
         * Entry record list.
         */
        private final List<Entry> entryList = new ArrayList<>();
        private final String key;
        /**
         * Type of this entry defined in metadata.
         */
        private final String type;
        /**
         * Description of this entry defined in metadata.
         */
        private final String description;
        /**
         * Whether the value of this entry can be updated. No more than one entry record is allowed to add.
         */
        private final boolean updatable;

        public Metadata(String key, String type, String description, boolean updatable) {
            this.key = key;
            this.type = type;
            this.description = description;
            this.updatable = updatable;
        }

        /**
         * Adds an entry record.
         * @param value       value of this entry
         * @param sourceClass source class this record comes from
         * @return true if successfully added; false if updatable is true and the size of entry record list is
         * one.
         */
        private boolean addEntryRecord(String value, Class<?> sourceClass) {
            if (!isUpdatable() && entryList.size() == 1)
                return false;

            final String source = String.format("Class[%s]", sourceClass.getName());
            entryList.add(new Entry(value, source));
            return true;
        }

        /**
         * Returns the value of this entry.
         * @return the value of this entry
         */
        public String value() {
            if (entryList.isEmpty()) {
                throw new UninitializedRegistryEntryException(key);
            }

            return entryList.get(entryList.size() - 1).value();
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public boolean isUpdatable() {
            return updatable;
        }

        /**
         * Registry entry record.
         */
        private record Entry(String value, String source) {
        }
    }

    public record FieldLocation(Object bean, Field field, String type) {
    }
}
