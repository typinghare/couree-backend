package com.couree.luckycat.glacier.app.pack;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Packer Factory.
 * @author James Chan
 */
@Component
public class PackerFactory {
    private final ApplicationContext applicationContext;

    public PackerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Returns a packer instance.
     * @param packerClass a packer class (template)
     * @return a packer instance
     */
    @NotNull
    public Packer getPacker(@NotNull Class<? extends Packer> packerClass) {
        try {
            return applicationContext.getBean(packerClass);
        } catch (NoSuchBeanDefinitionException e) {
            throw new RuntimeException(String.format(
                "Packer not found: [ %s ].", packerClass.getName()
            ), e);
        }
    }
}
