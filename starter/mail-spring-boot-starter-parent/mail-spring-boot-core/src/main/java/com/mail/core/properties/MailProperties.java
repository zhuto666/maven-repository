package com.mail.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin
 * @date 2024/6/25 14:12
 */
@Data
@ConfigurationProperties(MailProperties.PREFIX)
public class MailProperties {

    public static final String PREFIX = "spring.rabbitmq.mail";

    /**
     * 交换机名称
     */
    private String exchangesName = "X_MAIL";

    /**
     * 队列名称
     */
    private String queueName = "Q_MAIL";

    /**
     * 死信交换机名称
     */
    private String deadExchangesName = "DEAD_X_MAIL";

    /**
     * 死信队列名称
     */
    private String deadQueueName = "DEAD_Q_MAIL";

    /**
     * 死信路由Key
     */
    private String deadRoutingKey = "DEAD_X_MAIL_ROUTING_KEY";
}
