package couree.com.luckycat.glacier;

import couree.com.luckycat.core.annotation.Initializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoreBeanPostProcessor implements BeanPostProcessor {
    private final List<Object> initializerContainerList = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (final Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Initializer.class)) {
                initializerContainerList.add(bean);
            }
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    public List<Object> getInitializerContainerList() {
        return initializerContainerList;
    }
}
