package com.log.autoconfigure;

import com.log.core.aspect.SystemLogAspect;
import com.log.core.properties.QueueArgumentsProperties;
import com.log.core.properties.SystemLogProperties;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @date 2024/9/10 15:05
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SystemLogProperties.class)
public class SystemLogAutoConfiguration {

    @Bean
    public SystemLogAspect systemLogAspect() {
        return new SystemLogAspect();
    }


    /**
     * 创建交换机
     *
     * @param systemLogProperties
     * @return
     */
    @Bean
    FanoutExchange systemLogFanoutExchange(SystemLogProperties systemLogProperties) {
        return new FanoutExchange(systemLogProperties.getExchangesName(), true, true);
    }

    /**
     * 创建(系统日志)队列
     *
     * @param systemLogProperties
     * @return
     */
    @Bean
    Queue systemLogQueue(SystemLogProperties systemLogProperties) {
        Map<String, Object> args = new HashMap<>(2);
        args.put(QueueArgumentsProperties.DEAD_LETTER_QUEUE_KEY, systemLogProperties.getDeadExchangesName());
        args.put(QueueArgumentsProperties.DEAD_LETTER_ROUTING_KEY, systemLogProperties.getDeadRoutingKey());
        if (systemLogProperties.getLazyQueueModeFlag()) {
            args.put(QueueArgumentsProperties.QUEUE_MODE_KEY, "lazy");
        }
        return new Queue(systemLogProperties.getQueueName(), true, false, false, args);
    }

    /**
     * (系统日志)交换机与队列绑定
     *
     * @param systemLogQueue
     * @param systemLogFanoutExchange
     * @return
     */
    @Bean
    Binding bindingQueueSystemLog(Queue systemLogQueue, FanoutExchange systemLogFanoutExchange) {
        return BindingBuilder.bind(systemLogQueue).to(systemLogFanoutExchange);
    }

    /**
     * 创建系统日志死信交换机
     *
     * @param systemLogProperties
     * @return
     */
    @Bean
    DirectExchange systemLogDeadDirectExchange(SystemLogProperties systemLogProperties) {
        return new DirectExchange(systemLogProperties.getDeadExchangesName(), true, true);
    }


    /**
     * 系统日志邮件死信队列
     *
     * @param systemLogProperties
     * @return
     */
    @Bean
    Queue systemLogDeadQueue(SystemLogProperties systemLogProperties) {
        return new Queue(systemLogProperties.getDeadQueueName(), true);
    }


    /**
     * 系统日志死信交换机与死信队列绑定
     *
     * @param systemLogDeadQueue
     * @param systemLogDeadDirectExchange
     * @param systemLogProperties
     * @return
     */
    @Bean
    Binding bindingDeadQueueSystemLog(Queue systemLogDeadQueue, DirectExchange systemLogDeadDirectExchange, SystemLogProperties systemLogProperties) {
        return BindingBuilder.bind(systemLogDeadQueue).to(systemLogDeadDirectExchange).with(systemLogProperties.getDeadRoutingKey());
    }
}
