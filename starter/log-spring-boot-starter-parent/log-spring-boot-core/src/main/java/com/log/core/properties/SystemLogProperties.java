package com.log.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin
 * @date 2024/9/10 16:22
 */
@Data
@ConfigurationProperties(prefix = "spring.rabbitmq.syslog")
public class SystemLogProperties {

    /**
     * 交换机名称
     */
    private String exchangesName = "X_SYS_LOG";

    /**
     * 队列名称
     */
    private String queueName = "Q_SYS_LOG";

    /**
     * 死信交换机名称
     */
    private String deadExchangesName = "DEAD_X_SYS_LOG";

    /**
     * 死信队列名称
     */
    private String deadQueueName = "DEAD_Q_SYS_LOG";

    /**
     * 死信路由Key
     */
    private String deadRoutingKey = "DEAD_X_SYS_LOG_ROUTING_KEY";

    /**
     * 是否启用惰性队列模式
     */
    private Boolean lazyQueueModeFlag = Boolean.FALSE;
}
