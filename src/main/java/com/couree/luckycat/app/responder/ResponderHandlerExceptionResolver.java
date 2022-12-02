package com.couree.luckycat.app.responder;

import com.couree.luckycat.app.pack.IntegratedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author James Chan
 */
@ControllerAdvice
public class ResponderHandlerExceptionResolver implements HandlerExceptionResolver {
    public static final String INTEGRATED_REQUEST_ATTRIBUTE_NAME = ResponderIntegratedRequest.class.getName();

    private final HandleValueProcessor handleValueProcessor;

    @Autowired
    private ResponderHandlerExceptionResolver(HandleValueProcessor handleValueProcessor) {
        this.handleValueProcessor = handleValueProcessor;
    }

    @Override
    @ExceptionHandler
    public ModelAndView resolveException(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception exception
    ) {
        final IntegratedRequest integratedRequest = (IntegratedRequest) request
            .getAttribute(INTEGRATED_REQUEST_ATTRIBUTE_NAME);

        integratedRequest.setException(exception);
        handleValueProcessor.process(integratedRequest);

        return null;
    }
}
