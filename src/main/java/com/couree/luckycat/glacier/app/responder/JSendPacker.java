package com.couree.luckycat.glacier.app.responder;

import com.couree.luckycat.glacier.app.error.stereo.RequestException;
import com.couree.luckycat.glacier.app.pack.IntegratedRequest;
import com.couree.luckycat.glacier.app.pack.Packer;
import com.couree.luckycat.glacier.app.responder.annotation.SuccessMessage;
import com.couree.luckycat.glacier.app.responder.constant.JSendStatus;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class JSendPacker extends Packer {
    private final static Gson gson = new Gson();

    @Value("${Error.DefaultErrorCode}")
    private String defaultSuccessMessage;

    @Value("${Error.DefaultErrorCode}")
    private String defaultErrorCode;

    @Value("${Error.DefaultErrorMessage}")
    private String defaultErrorMessage;

    @Override
    public Object pack(IntegratedRequest request) {
        setResponseHeader(request.getHttpServletResponse());

        if (request.getException() != null)
            return packError(request);

        final SuccessMessage successMessage
            = request.getHandlerMethod().getMethodAnnotation(SuccessMessage.class);
        final String message = successMessage != null ? successMessage.value() : defaultSuccessMessage;

        final JSendDto jSendDto = new JSendDto();
        jSendDto.setStatus(com.couree.luckycat.glacier.app.responder.constant.JSendStatus.SUCCESS);
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

            jSendDto.setErrorCode(defaultErrorCode);
        } else if (exception instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            final String message = String.format(
                "The parameter [%s] is missing. Its type should be: [%s].",
                missingServletRequestParameterException.getParameterName(),
                missingServletRequestParameterException.getParameterType()
            );
            jSendDto.setMessage(message);
            jSendDto.setErrorCode(defaultErrorCode);
            request.getHttpServletResponse().setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            jSendDto.setMessage(defaultErrorMessage);
            jSendDto.setErrorCode(defaultErrorCode);
        }

        return gson.toJson(jSendDto);
    }

    private void setResponseHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Content-Type", "application/json");
    }
}
