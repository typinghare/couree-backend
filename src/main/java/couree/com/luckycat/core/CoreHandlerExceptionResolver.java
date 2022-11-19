package couree.com.luckycat.core;

import couree.com.luckycat.core.constant.RequestAttributeEnum;
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
public class CoreHandlerExceptionResolver implements HandlerExceptionResolver {
    private final HandleValueProcessor handleValueProcessor;

    @Autowired
    private CoreHandlerExceptionResolver(HandleValueProcessor handleValueProcessor) {
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
                .getAttribute(RequestAttributeEnum.INTEGRATED_REQUEST.toString());

        integratedRequest.setException(exception);
        handleValueProcessor.process(integratedRequest);

        return null;
    }
}
