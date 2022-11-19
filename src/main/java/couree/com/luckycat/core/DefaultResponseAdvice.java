package couree.com.luckycat.core;

import com.sun.istack.NotNull;
import couree.com.luckycat.core.constant.RequestAttributeEnum;
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
public class DefaultResponseAdvice implements ResponseBodyAdvice<Object> {
    private final HandleValueProcessor handleValueProcessor;

    @Autowired
    public DefaultResponseAdvice(HandleValueProcessor handleValueProcessor) {
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
        System.out.println(getClass());
        final ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        final IntegratedRequest integratedRequest =
                (IntegratedRequest) servletRequestAttributes.getRequest()
                        .getAttribute(RequestAttributeEnum.INTEGRATED_REQUEST.toString());

        integratedRequest.setHandleValue(handleValue);
        handleValueProcessor.process(integratedRequest);
        return null;
    }
}
