package couree.com.luckycat.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Integrated Request.
 * @author James Chan
 */
@Component
@RequestScope
public class IntegratedRequest {
    /**
     * Servlet HTTP request.
     */
    private final HttpServletRequest httpServletRequest;

    /**
     * Servlet HTTP response.
     */
    private final HttpServletResponse httpServletResponse;

    /**
     * The handler method found by SpringMVC. The handler method, if found, will be injected in the preHandle method in
     * the DefaultHandlerInterceptor.
     */
    private HandlerMethod handlerMethod;

    /**
     * The return value of the handler method.
     */
    private Object handleValue;

    /**
     * Exception thrown during handling the request.
     */
    private Exception exception;

    @Autowired
    public IntegratedRequest(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    public Object getHandleValue() {
        return handleValue;
    }

    public Exception getException() {
        return exception;
    }

    public void setHandlerMethod(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public void setHandleValue(Object handleValue) {
        this.handleValue = handleValue;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
