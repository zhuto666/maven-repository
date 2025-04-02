package com.log.core.receiver;

import com.alibaba.fastjson.JSON;
import com.log.core.entity.SystemLogRequestParam;
import com.log.core.exception.SystemLogException;
import com.log.core.template.AbstractLogTemplate;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Kevin
 * @date 2024/9/10 16:16
 */
public class SystemLogReceiver {

    private final Map<String, AbstractLogTemplate> abstractLogTemplateHashMap;

    public SystemLogReceiver(Map<String, AbstractLogTemplate> abstractLogTemplateHashMap) {
        this.abstractLogTemplateHashMap = abstractLogTemplateHashMap;
    }

    @RabbitListener(queues = {"${spring.rabbitmq.syslog.queueName}"})
    private void consumeSystemLog(Channel channel, Message message) throws IOException {
        try {
            String resultMsg = new String(message.getBody(), StandardCharsets.UTF_8);
            SystemLogRequestParam systemLogRequestParam = JSON.parseObject(resultMsg, SystemLogRequestParam.class);
            String beanName = systemLogRequestParam.getBeanName();
            if (!abstractLogTemplateHashMap.containsKey(beanName)) {
                throw new SystemLogException("beanType is null");
            }
            // 获取出模板方法实现类
            AbstractLogTemplate abstractLogTemplate = abstractLogTemplateHashMap.get(beanName);
            abstractLogTemplate.writeLogToDb(systemLogRequestParam);
            // 消息确认签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception ex) {
            // 拒绝签收给死信队列处理
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

}
