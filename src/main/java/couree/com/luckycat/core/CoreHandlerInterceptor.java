package couree.com.luckycat.core;

import couree.com.luckycat.core.constant.RequestAttributeEnum;
import couree.com.luckycat.core.constant.StartupPhase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CoreHandlerInterceptor implements HandlerInterceptor {
    @Value("${application.name}")
    private String applicationName;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        try {
            final StartupPhase startupPhase = Config.instance().getStartupPhase();
            if (startupPhase == StartupPhase.CORE_STARTING) {
                response.getWriter()
                        .print(String.format("Application [%s] (Core) is still starting...", applicationName));
            } else if (startupPhase == StartupPhase.SPRINGBOOT_STARTING) {
                response.getWriter()
                        .print(String.format("Application [%s] (SpringBoot) is still starting...", applicationName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final IntegratedRequest integratedRequest = Config.getApplicationContext().getBean(IntegratedRequest.class);
        integratedRequest.setHandlerMethod((HandlerMethod) handler);
        request.setAttribute(RequestAttributeEnum.INTEGRATED_REQUEST.toString(), integratedRequest);

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) {
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
