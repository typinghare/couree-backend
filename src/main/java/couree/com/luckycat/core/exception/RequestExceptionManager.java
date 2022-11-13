package couree.com.luckycat.core.exception;

import com.google.common.base.Strings;
import com.sun.istack.NotNull;
import couree.com.luckycat.core.Config;
import couree.com.luckycat.core.annotation.Entry;
import couree.com.luckycat.core.annotation.Initializer;
import couree.com.luckycat.core.annotation.RequestExceptionCode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestExceptionManager {
    /**
     * The fixed length of request error code.
     */
    private int REQUEST_EXCEPTION_CODE_LENGTH;

    /**
     * Mapping: request exception class => RequestExceptionClass
     */
    private final Map<Class<? extends RequestException>, RequestExceptionClass> requestExceptionClassMap = new HashMap<>();

    @Initializer
    private void init() {
        final Config config = Config.instance();
        REQUEST_EXCEPTION_CODE_LENGTH = Integer.parseInt(config.getRegistryValue("REQUEST_EXCEPTION_CODE_LENGTH"));

        final ApplicationContext applicationContext = Config.getApplicationContext();
        final Map<String, Object> requestExceptionMap = applicationContext.getBeansWithAnnotation(RequestExceptionCode.class);
        for (Object requestException : requestExceptionMap.values()) {
            if (!(requestException instanceof RequestException)) {
                throw new RuntimeException(String.format(
                        "A request exception class should extends request exception. Check [%s].",
                        requestException.getClass()
                ));
            }

            @SuppressWarnings("unchecked") final Class<? extends RequestException> _requestExceptionClass
                    = (Class<? extends RequestException>) requestException.getClass();
            final RequestExceptionCode requestExceptionCode = _requestExceptionClass.getAnnotation(RequestExceptionCode.class);
            final RequestExceptionClass requestExceptionClass = new RequestExceptionClass(requestExceptionCode.value());
            requestExceptionClassMap.put(_requestExceptionClass, requestExceptionClass);

            Field[] fields = _requestExceptionClass.getDeclaredFields();
            for (Field field : fields) {
                final Entry entry = field.getAnnotation(Entry.class);
                if (entry == null) continue;

                try {
                    final RequestException requestExceptionInstance = _requestExceptionClass.getConstructor().newInstance();

                    // inject
                    field.set(null, requestExceptionInstance);

                    // register
                    requestExceptionClass.register(
                            requestExceptionInstance,
                            entry.code(),
                            entry.status(),
                            entry.message(),
                            field.getName()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(String.format(
                            "Fail to create a new instance of request exception class: [%s].",
                            _requestExceptionClass.getName()
                    ));
                }
            }
        }
    }

    @NotNull
    public String getEntryName(RequestException requestException) {
        return getRequestExceptionClass(requestException).getRequestExceptionEntry(requestException).entryName;
    }

    @NotNull
    public String getErrorCode(RequestException requestException) {
        RequestExceptionClass requestExceptionClass = getRequestExceptionClass(requestException);
        int entryCode = requestExceptionClass.getRequestExceptionEntry(requestException).entryCode;

        return requestExceptionClass.requestExceptionCode +
                Strings.padStart(String.valueOf(entryCode), REQUEST_EXCEPTION_CODE_LENGTH, '0');
    }

    @NotNull
    public String getMessage(RequestException requestException) {
        return getRequestExceptionClass(requestException).getRequestExceptionEntry(requestException).message;
    }

    @NotNull
    public HttpStatus getHttpStatus(RequestException requestException) {
        return getRequestExceptionClass(requestException).getRequestExceptionEntry(requestException).httpStatus;
    }

    @NotNull
    private RequestExceptionClass getRequestExceptionClass(RequestException requestException) {
        RequestExceptionClass requestExceptionClass = requestExceptionClassMap.get(requestException.getClass());
        if (requestExceptionClass == null) {
            throw new RuntimeException(String.format(
                    "Request Exception not registered: [%s].", requestException.getClass().getName()
            ));
        }

        return requestExceptionClass;
    }

    private static class RequestExceptionClass {
        /**
         * Request exception code.
         */
        private final int requestExceptionCode;

        /**
         * Mapping: request exception instance => request exception entry
         */
        private final Map<RequestException, RequestExceptionEntry> entryNameEntryMap = new HashMap<>();

        private RequestExceptionClass(int requestExceptionCode) {
            this.requestExceptionCode = requestExceptionCode;
        }

        /**
         * Register an request exception entry.
         */
        private void register(
                RequestException instance,
                int entryCode,
                HttpStatus httpStatus,
                String message,
                String entryName
        ) {
            entryNameEntryMap.put(instance, new RequestExceptionEntry(entryCode, httpStatus, message, entryName));
        }

        @NotNull
        private RequestExceptionEntry getRequestExceptionEntry(RequestException requestException) {
            final RequestExceptionEntry requestExceptionEntry = entryNameEntryMap.get(requestException);
            if (requestExceptionEntry == null) {
                throw new RuntimeException("Request exception entry not found. " +
                        "This exception is not expected to be thrown. " +
                        "Check the source code on registering request exception entries.");
            }

            return requestExceptionEntry;
        }
    }

    private record RequestExceptionEntry(
            int entryCode,
            HttpStatus httpStatus,
            String message,
            String entryName
    ) {
    }
}
