package couree.com.luckycat.core.common;

/**
 * Helper functions of throwing.
 * @author James Chan
 */
public class Throws {
    /**
     * Throws a runtime exception if the given condition is true.
     * @param condition        a specified condition
     * @param runtimeException a runtime exception to throw if the given condition is true
     * @throws RuntimeException if the given condition is true.
     */
    public static void ifTrue(Boolean condition, RuntimeException runtimeException) {
        if (condition != null && condition) {
            throw runtimeException;
        }
    }

    /**
     * Throws a runtime exception if the given condition is false.
     * @param condition        a specified condition
     * @param runtimeException a runtime exception to throw if the given condition is false
     * @throws RuntimeException if the given condition is false.
     */
    public static void ifFalse(Boolean condition, RuntimeException runtimeException) {
        if (!condition) {
            throw runtimeException;
        }
    }

    /**
     * Throws a runtime exception if the given object is null.
     * @param object           a specified object to test
     * @param runtimeException a runtime exception to throw if the given object is null
     * @throws RuntimeException if the given object is null
     */
    public static void ifNull(Object object, RuntimeException runtimeException) {
        if (object == null) {
            throw runtimeException;
        }
    }

    /**
     * Throws a runtime exception if the given object is null.
     * @param object           a specified object to test
     * @param runtimeException a runtime exception to throw if the given object is null
     * @throws RuntimeException if the given object is null
     */
    public static void ifNotNull(Object object, RuntimeException runtimeException) {
        if (object != null) {
            throw runtimeException;
        }
    }
}
