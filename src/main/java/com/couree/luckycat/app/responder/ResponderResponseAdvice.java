package com.couree.luckycat.app.responder;

import com.couree.luckycat.app.pack.IntegratedRequest;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.Servlet;

/**
 * @author James Chan
 */
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@RestControllerAdvice(annotations = RestController.class)
public class ResponderResponseAdvice implements ResponseBodyAdvice<Object> {
    private final HandleValueProcessor handleValueProcessor;

    @Autowired
    public ResponderResponseAdvice(HandleValueProcessor handleValueProcessor) {
        this.handleValueProcessor = handleValueProcessor;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
        Object handleValue,
        @NotNull MethodParameter methodParameter,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request,
        ServerHttpResponse response
    ) {
        final ServletRequestAttributes servletRequestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        final IntegratedRequest integratedRequest = (IntegratedRequest) servletRequestAttributes.getRequest()
            .getAttribute(ResponderHandlerExceptionResolver.INTEGRATED_REQUEST_ATTRIBUTE_NAME);

        integratedRequest.setHandleValue(handleValue);
        handleValueProcessor.process(integratedRequest);

        // write nothing to the body then
        return null;
    }
}
