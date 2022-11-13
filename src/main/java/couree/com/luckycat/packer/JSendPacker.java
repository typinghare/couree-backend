package couree.com.luckycat.packer;

import com.google.gson.Gson;
import couree.com.luckycat.constant.JSendStatus;
import couree.com.luckycat.core.IntegratedRequest;
import couree.com.luckycat.core.annotation.SuccessMessage;
import couree.com.luckycat.core.pack.Packer;
import couree.com.luckycat.dto.JSendDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

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
        return null;
    }

//    public Object packError(
//            Throwable throwable,
//            Method method,
//            HttpServletRequest httpServletRequest,
//            HttpServletResponse httpServletResponse
//    ) {
//        setResponseHeader(httpServletResponse);
//
//        JSendStatus status = JSendStatus.FAIL;
//        String message = DEFAULT_ERROR_MESSAGE;
//        String errorCode = DEFAULT_ERROR_CODE;
//        Object data = null;
//
//        if (throwable instanceof RequestException requestException) {
//            RequestExceptionManager requestExceptionManager = App.getBean(RequestExceptionManager.class);
//            message = requestExceptionManager.getMessage(requestException);
//            errorCode = requestExceptionManager.getErrorCode(requestException);
//
//            HttpStatus httpStatus = requestExceptionManager.getHttpStatus(requestException);
//            httpServletResponse.setStatus(httpStatus.value());
//        } else if (throwable instanceof MethodArgumentTypeMismatchException exception) {
//            MethodParameter parameter = exception.getParameter();
//            message = String.format(
//                    "The type of parameter [%s] is incorrect. It should be: [%s].",
//                    parameter.getParameterName(),
//                    parameter.getParameterType().getSimpleName()
//            );
//
//            Map<String, String> map = new HashMap<>();
//            map.put(parameter.getParameterName(), parameter.getParameterType().getSimpleName());
//            data = map;
//        } else if (throwable instanceof MissingServletRequestParameterException exception) {
//            message = String.format("Missing required parameter: [%s]", exception.getParameterName());
//
//            Map<String, String> map = new HashMap<>();
//            map.put(exception.getParameterName(), exception.getParameterType());
//            data = map;
//        } else {
//            status = JSendStatus.ERROR;
//        }
//
//        return gson.toJson(new JSendDto(status, message, errorCode, data));
//    }

    private void setResponseHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Content-Type", "application/json");
    }
}
