package com.wxmblog;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Local-only switch for starting the application without payment credentials.
 * Payment remains enabled unless wxmfast.config.pay.enabled=false is supplied.
 */
@Configuration
public class LocalPaymentDisabledConfiguration {

    @Bean
    public static BeanFactoryPostProcessor removePaymentBeans(Environment environment) {
        return beanFactory -> {
            if (!"false".equalsIgnoreCase(environment.getProperty("wxmfast.config.pay.enabled"))) {
                return;
            }
            if (!(beanFactory instanceof BeanDefinitionRegistry)) {
                return;
            }

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            List<String> toRemove = new ArrayList<>();
            for (String beanName : registry.getBeanDefinitionNames()) {
                BeanDefinition definition = registry.getBeanDefinition(beanName);
                String className = definition.getBeanClassName();
                if (className == null) {
                    className = "";
                }
                String factoryBeanName = definition.getFactoryBeanName();
                String lowerBeanName = beanName.toLowerCase();
                if ("com.wxmblog.base.pay.config.WechatPayConfig".equals(className)
                        || className.startsWith("com.wxmblog.base.pay.controller.")
                        || className.startsWith("com.wxmblog.yanjian.service.impl.pay.")
                        || "wechatPayConfig".equalsIgnoreCase(factoryBeanName)
                        || lowerBeanName.contains("wechatpayconfig")
                        || lowerBeanName.contains("basepaycontroller")
                        || lowerBeanName.contains("transferbillscontroller")
                        || lowerBeanName.contains("wxpaycontroller")) {
                    toRemove.add(beanName);
                }
            }
            for (String beanName : toRemove) {
                if (registry.containsBeanDefinition(beanName)) {
                    registry.removeBeanDefinition(beanName);
                }
            }
        };
    }
}
