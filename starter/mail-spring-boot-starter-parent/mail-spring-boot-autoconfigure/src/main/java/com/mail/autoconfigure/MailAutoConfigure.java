package com.mail.autoconfigure;

import com.mail.core.factory.MailFactory;
import com.mail.core.properties.DeadLetterKeyProperties;
import com.mail.core.properties.MailProperties;
import com.mail.core.receiver.MailReceiver;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/6/25 16:48 星期二
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfigure {


    /**
     * 邮件工厂上下文对象
     *
     * @return
     */
    @Bean
    MailFactory mailFactory() {
        return new MailFactory();
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    FanoutExchange createMailFanoutConfig(MailProperties mailProperties) {
        return new FanoutExchange(mailProperties.getExchangesName(), true, true);
    }

    /**
     * 创建(邮件)队列
     *
     * @return
     */
    @Bean
    Queue createMailQueue(MailProperties mailProperties) {
        Map<String, Object> args = new HashMap<>(2);
        args.put(DeadLetterKeyProperties.DEAD_LETTER_QUEUE_KEY, mailProperties.getDeadExchangesName());
        args.put(DeadLetterKeyProperties.DEAD_LETTER_ROUTING_KEY, mailProperties.getDeadRoutingKey());
        return new Queue(mailProperties.getQueueName(), true, false, false, args);
    }

    /**
     * (邮件)交换机与队列绑定
     *
     * @param createMailQueue
     * @param createMailFanoutConfig
     * @return
     */
    @Bean
    Binding bindingQueueMail(Queue createMailQueue, FanoutExchange createMailFanoutConfig) {
        return BindingBuilder.bind(createMailQueue).to(createMailFanoutConfig);
    }

    /**
     * 创建邮件死信交换机
     */
    @Bean
    DirectExchange createMailDeadExchange(MailProperties mailProperties) {
        return new DirectExchange(mailProperties.getDeadExchangesName(), true, true);
    }

    /**
     * 创建邮件死信队列
     *
     * @return
     */
    @Bean
    Queue createDeadMailQueue(MailProperties mailProperties) {
        return new Queue(mailProperties.getDeadQueueName(), true);
    }

    /**
     * 邮件死信交换机与死信队列绑定
     *
     * @param createDeadMailQueue
     * @param createMailDeadExchange
     * @return
     */
    @Bean
    Binding bindingDeadQueueMail(Queue createDeadMailQueue, DirectExchange createMailDeadExchange, MailProperties mailProperties) {
        return BindingBuilder.bind(createDeadMailQueue).to(createMailDeadExchange).with(mailProperties.getDeadRoutingKey());
    }

    /**
     * 邮件消费队列
     *
     * @return
     */
    @Bean
    MailReceiver mailReceiver() {
        return new MailReceiver();
    }

}
