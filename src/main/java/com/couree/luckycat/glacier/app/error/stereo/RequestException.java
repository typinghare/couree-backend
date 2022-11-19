package com.couree.luckycat.glacier.app.error.stereo;

import com.couree.luckycat.glacier.app.error.RequestExceptionManager;
import com.couree.luckycat.glacier.app.error.annotation.Entry;
import com.couree.luckycat.glacier.app.error.annotation.ExceptionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Base request exception.
 * @author James Chan
 */
@ExceptionConfiguration(exceptionCode = 1)
public class RequestException extends RuntimeException {
    @Entry(code = 1, message = "No matching handler method found.", status = HttpStatus.BAD_REQUEST)
    public static RequestException NO_MATCHING_HANDLER_METHOD;
    private static RequestExceptionManager requestExceptionManager;

    protected RequestException() {
    }

    @Autowired
    public RequestException(RequestExceptionManager requestExceptionManager) {
        RequestException.requestExceptionManager = requestExceptionManager;
    }

    public String getErrorCode() {
        return requestExceptionManager.getErrorCode(this);
    }

    public HttpStatus getHttpStatus() {
        return requestExceptionManager.getHttpStatus(this);
    }

    public String getDetailedMessage() {
        return String.format(
            "%s: %s",
            requestExceptionManager.getEntryName(this),
            requestExceptionManager.getMessage(this)
        );
    }

    @Override
    public String getMessage() {
        return requestExceptionManager.getMessage(this);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
