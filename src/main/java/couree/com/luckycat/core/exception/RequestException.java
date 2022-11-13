package couree.com.luckycat.core.exception;

import couree.com.luckycat.core.Config;
import couree.com.luckycat.core.annotation.Entry;
import couree.com.luckycat.core.annotation.RequestExceptionCode;
import org.springframework.http.HttpStatus;

/**
 * Base request exception.
 * @author James Chan
 */
@RequestExceptionCode(1)
public class RequestException extends RuntimeException {
    private static RequestExceptionManager requestExceptionManager;

    @Entry(code = 1, message = "No matching handler method found.", status = HttpStatus.BAD_REQUEST)
    public static RequestException NO_MATCHING_HANDLER_METHOD;

    @Override
    public String getMessage() {
        if (requestExceptionManager == null) {
            requestExceptionManager = Config.getApplicationContext().getBean(RequestExceptionManager.class);
        }

        return String.format(
                "%s: %s",
                requestExceptionManager.getEntryName(this),
                requestExceptionManager.getMessage(this)
        );
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
