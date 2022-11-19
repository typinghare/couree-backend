package com.couree.luckycat.glacier;

import com.couree.luckycat.glacier.exception.ConversionFailException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalVariableWarehouse {
    private final Map<String, Object> globalValueMap = new HashMap<>();

    public void store(String name, Object value) {
        globalValueMap.put(name, value);
    }

    public void store(Object value) {
        globalValueMap.put(value.getClass().getName(), value);
    }

    /**
     * Returns the value of a specified name.
     * @return the value of a specified name
     */
    public Object get(String name) {
        return globalValueMap.get(name);
    }

    /**
     * Returns the value of a specified name.
     * @param requiredClass class of the value
     * @return the value of a specified name
     * @throws ConversionFailException if the value cannot be converted to the required class
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> requiredClass) {
        final Object value = get(name);

        if (value == null)
            return null;

        if (!requiredClass.equals(value.getClass())) {
            throw new ConversionFailException(name, value.getClass());
        }

        return (T) value;
    }

    /**
     * Return the value of a specified class.
     * @param requiredClass class of the value
     * @return the value of a specified class
     */
    public <T> T get(Class<T> requiredClass) {
        return get(requiredClass.getName(), requiredClass);
    }

    /**
     * Returns the value of a specified name. The return value should be an integer.
     * @return the value of a specified name
     */
    public Integer getInteger(String name) {
        return get(name, Integer.class);
    }

    /**
     * Returns the value of a specified name. The return value should be a long.
     * @return the value of a specified name
     */
    public Long getLong(String name) {
        return get(name, Long.class);
    }

    /**
     * Returns the value of a specified name. The return value should be a string.
     * @return the value of a specified name
     */
    public String getString(String name) {
        return get(name, String.class);
    }
}
