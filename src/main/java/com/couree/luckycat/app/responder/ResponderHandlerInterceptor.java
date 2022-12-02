package com.couree.luckycat.app.responder;

import com.couree.luckycat.glacier.GlobalVariableWarehouse;
import com.couree.luckycat.app.pack.IntegratedRequest;
import com.couree.luckycat.glacier.constant.StartupPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ResponderHandlerInterceptor implements HandlerInterceptor {
    private final ApplicationContext applicationContext;

    private String applicationName;

    @Autowired
    public ResponderHandlerInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        applicationName = applicationContext
            .getEnvironment().getProperty("application.name");
        if (applicationName == null)
            applicationName = "Unnamed";

        applicationName = applicationName.replace("\\n", "").trim();
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws IOException {
        final GlobalVariableWarehouse globalVariableWarehouse
            = applicationContext.getBean(GlobalVariableWarehouse.class);
        final StartupPhase startupPhase = globalVariableWarehouse.get(StartupPhase.class);

        if (startupPhase == StartupPhase.SPRINGBOOT_STARTING) {
            response.getWriter().print(String.format(
                "Application [ %s ] (SpringBoot startup phase) is still starting...",
                applicationName
            ));
            return false;
        } else if (startupPhase == StartupPhase.GLACIER_STARTING) {
            response.getWriter().print(String.format(
                "Application [ %s ] (Glacier startup phase) is still starting...",
                applicationName
            ));
            return false;
        }

        final IntegratedRequest integratedRequest = applicationContext.getBean(IntegratedRequest.class);
        integratedRequest.setHandlerMethod((HandlerMethod) handler);
        request.setAttribute(ResponderHandlerExceptionResolver.INTEGRATED_REQUEST_ATTRIBUTE_NAME, integratedRequest);

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
        Exception exception
    ) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, exception);
    }
}
