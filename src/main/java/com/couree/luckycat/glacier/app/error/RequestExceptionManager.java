package com.couree.luckycat.glacier.app.error;

import com.couree.luckycat.glacier.annotation.Initializer;
import com.couree.luckycat.glacier.app.error.annotation.Entry;
import com.couree.luckycat.glacier.app.error.annotation.ExceptionConfiguration;
import com.couree.luckycat.glacier.app.error.stereo.RequestException;
import com.google.common.base.Strings;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestExceptionManager {
    /**
     * The fixed length of request error code.
     */
    @Value("${Error.RequestExceptionCodeLength}")
    private int REQUEST_EXCEPTION_CODE_LENGTH;

    private final ApplicationContext applicationContext;

    /**
     * Mapping: request exception class => RequestExceptionClass
     */
    private final Map<Class<? extends RequestException>, RequestExceptionClass> requestExceptionClassMap
        = new HashMap<>();

    @Autowired
    public RequestExceptionManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Initializer
    @SuppressWarnings("unchecked")
    private void init() {
        final Map<String, Object> exceptionConfigurationBeanMap
            = applicationContext.getBeansWithAnnotation(ExceptionConfiguration.class);
        for (final Object exceptionConfigurationBean : exceptionConfigurationBeanMap.values()) {
            if (!(exceptionConfigurationBean instanceof RequestException)) {
                throw new RuntimeException(String.format(
                    "Request exception classes should extend RequestException. Check [%s].",
                    exceptionConfigurationBean.getClass()
                ));
            }

            final ExceptionConfiguration exceptionConfiguration =
                exceptionConfigurationBean.getClass().getAnnotation(ExceptionConfiguration.class);
            final Class<? extends RequestException> _requestExceptionClass
                = (Class<? extends RequestException>) exceptionConfigurationBean.getClass();
            final RequestExceptionClass requestExceptionClass
                = new RequestExceptionClass(exceptionConfiguration.exceptionCode());
            requestExceptionClassMap.put(_requestExceptionClass, requestExceptionClass);

            final Field[] fields = _requestExceptionClass.getDeclaredFields();
            for (final Field field : fields) {
                final Entry entry = field.getAnnotation(Entry.class);
                if (entry == null)
                    continue;

                try {
                    final Constructor<RequestException> constructor
                        = (Constructor<RequestException>) _requestExceptionClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    final RequestException requestExceptionInstance
                        = constructor.newInstance();

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
                        "Fail to create a new instance of request exception class: [ %s ].",
                        _requestExceptionClass.getName()
                    ), e);
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
