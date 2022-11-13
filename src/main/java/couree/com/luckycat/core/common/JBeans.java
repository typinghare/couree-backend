package couree.com.luckycat.core.common;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for Java beans. Java beans are java classes which adhere to certain code convention as follows.
 * <ul>
 * <li>1. They have public default (no argument) constructors.</li>
 * <li>2. They allow access to their properties using accessor (getter or setter) methods.</li>
 * <li>3. They implement java.io.Serializable.</li>
 * </ul>
 * @author James Chan
 * @version 1.0
 * @see Serializable
 */
public class JBeans {
    /**
     * Default getter method prefix.
     */
    private static final String GETTER_PREFIX = "get";

    /**
     * Default setter method prefix.
     */
    public static final String SETTER_PREFIX = "set";

    /**
     * Prints a Java bean.
     * @param bean        Java bean to print
     * @param printStream print stream
     */
    public static void print(Serializable bean, PrintStream printStream) {
        printStream.println(convertToString(bean));
    }

    /**
     * Prints a Java bean. The print stream is System.out.
     * @param bean Java bean to print
     */
    public static void print(Serializable bean) {
        print(bean, System.out);
    }

    /**
     * Converts a Java bean to printing string.
     * @param bean Java bean to convert
     * @return a printing string; null if bean is null
     */
    public static String convertToString(Serializable bean) {
        if (bean == null)
            return null;

        final Class<?> beanClass = bean.getClass();
        final StringBuilder stringBuilder = new StringBuilder();
        final String title = "[" + beanClass.getSimpleName() + "]";

        stringBuilder.append(title).append("\n");

        for (final Method method : beanClass.getMethods()) {
            final String methodName = method.getName();
            if (!methodName.startsWith(GETTER_PREFIX) || methodName.equals("getClass"))
                continue;

            // attribute name
            final char[] chars = methodName.substring(GETTER_PREFIX.length()).toCharArray();
            if (chars.length == 0) continue;
            chars[0] = Character.toLowerCase(chars[0]);
            stringBuilder.append(new String(chars)).append(": ");

            try {
                final Object value = method.invoke(bean);

                if (value == null) {
                    stringBuilder.append("null");
                } else if (value instanceof Integer || value instanceof Long) {
                    stringBuilder.append(value);
                } else if (value instanceof String) {
                    stringBuilder.append('"').append(value).append('"');
                } else {
                    stringBuilder.append(value);
                }

                stringBuilder.append(",\n");
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Converts a source Java Bean to a specified Java Bean class of object.
     * @param srcObject source object to convert
     * @param destClass dest object class
     * @return a dest object; null if source object is null
     */
    public static <D extends Serializable> D convert(Serializable srcObject, Class<? super D> destClass) {
        if (srcObject == null)
            return null;
        final D destObject = create(destClass);

        for (final Method setterMethod : destClass.getMethods()) {
            final String setterName = setterMethod.getName();
            if (!setterName.startsWith(SETTER_PREFIX)) continue;

            final Class<?> srcClass = srcObject.getClass();
            final String getterName = GETTER_PREFIX + setterName.substring(SETTER_PREFIX.length());

            try {
                final Method getterMethod = srcClass.getMethod(getterName);
                setterMethod.invoke(destObject, getterMethod.invoke(srcObject));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        return destObject;
    }

    /**
     * Creates a Java Bean object.
     * @param cls Java Bean class of the destination instance
     * @return a Java Bean object; null if cls is null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T create(Class<? super T> cls) {
        if (cls == null)
            return null;

        try {
            return (T) cls.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Fail to create a Java Bean Object. Check if [%s] complies with Java Bean convention.",
                    cls.getName()
            ));
        }
    }
}
