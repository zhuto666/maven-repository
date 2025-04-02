package com.log.autoconfigure;

import com.log.core.receiver.SystemLogReceiver;
import com.log.core.template.AbstractLogTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Kevin
 * @date 2024/9/10 15:05
 */
@Configuration(proxyBeanMethods = false)
public class SystemLogReceiverAutoConfiguration {

    /**
     * 日志消费队列
     *
     * @return
     */
    @Bean
    SystemLogReceiver systemLogReceiver(Map<String, AbstractLogTemplate> abstractLogTemplateHashMap) {
        return new SystemLogReceiver(abstractLogTemplateHashMap);
    }

}
