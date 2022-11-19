package couree.com.luckycat.packer;

import com.google.gson.Gson;
import couree.com.luckycat.constant.JSendStatus;
import couree.com.luckycat.core.IntegratedRequest;
import couree.com.luckycat.core.annotation.SuccessMessage;
import couree.com.luckycat.core.exception.RequestException;
import couree.com.luckycat.core.pack.Packer;
import couree.com.luckycat.dto.JSendDto;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * JSendPacker packs the given objects and exceptions complying with JSend policy.
 * @author James Chan
 */
@Component
public class JSendPacker extends Packer {
    private final static Gson gson = new Gson();

    private final static String DEFAULT_SUCCESS_MESSAGE = "Success.";
    private final static String DEFAULT_ERROR_MESSAGE = "Unknown Server Internal Error. Please contact maintainers.";
    private final static String DEFAULT_ERROR_CODE = "1000";

    @Override
    public Object pack(IntegratedRequest request) {
        setResponseHeader(request.getHttpServletResponse());

        if (request.getException() != null)
            return packError(request);

        final SuccessMessage successMessage
                = request.getHandlerMethod().getMethodAnnotation(SuccessMessage.class);
        final String message = successMessage != null ? successMessage.value() : DEFAULT_SUCCESS_MESSAGE;

        final JSendDto jSendDto = new JSendDto();
        jSendDto.setStatus(JSendStatus.SUCCESS);
        jSendDto.setMessage(message);
        jSendDto.setData(request.getHandleValue());

        return gson.toJson(jSendDto);
    }

    private Object packError(IntegratedRequest request) {
        final JSendDto jSendDto = new JSendDto();
        final Exception exception = request.getException();

        jSendDto.setStatus(JSendStatus.FAIL);

        if (exception instanceof final RequestException requestException) {
            jSendDto.setMessage(requestException.getMessage());
            jSendDto.setErrorCode(requestException.getErrorCode());
            request.getHttpServletResponse().setStatus(requestException.getHttpStatus().value());
        } else if (exception instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            MethodParameter parameter = methodArgumentTypeMismatchException.getParameter();
            final String message = String.format(
                    "The type of parameter [%s] is incorrect. It should be: [%s].",
                    parameter.getParameterName(),
                    parameter.getParameterType().getSimpleName()
            );
            jSendDto.setMessage(message);

            final Map<String, String> data = new HashMap<>();
            data.put(parameter.getParameterName(), parameter.getParameterType().getSimpleName());
            jSendDto.setData(data);

            jSendDto.setErrorCode(DEFAULT_ERROR_CODE);
        } else if (exception instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            final String message = String.format(
                    "The parameter [%s] is missing. Its type should be: [%s].",
                    missingServletRequestParameterException.getParameterName(),
                    missingServletRequestParameterException.getParameterType()
            );
            jSendDto.setMessage(message);
            jSendDto.setErrorCode(DEFAULT_ERROR_CODE);
            request.getHttpServletResponse().setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            jSendDto.setMessage(DEFAULT_ERROR_MESSAGE);
            jSendDto.setErrorCode(DEFAULT_ERROR_CODE);
        }

        return gson.toJson(jSendDto);
    }

    private void setResponseHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Content-Type", "application/json");
    }
}
