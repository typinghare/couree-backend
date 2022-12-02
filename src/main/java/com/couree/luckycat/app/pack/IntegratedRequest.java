package com.couree.luckycat.app.pack;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Integrated request.
 */
@Component
@RequestScope
public interface IntegratedRequest {
    HttpServletRequest getHttpServletRequest();

    HttpServletResponse getHttpServletResponse();

    HandlerMethod getHandlerMethod();

    void setHandlerMethod(HandlerMethod handlerMethod);

    Object getHandleValue();

    void setHandleValue(Object handleValue);

    Exception getException();

    void setException(Exception exception);
}
