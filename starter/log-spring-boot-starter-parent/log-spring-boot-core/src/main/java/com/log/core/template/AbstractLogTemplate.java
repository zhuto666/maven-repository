package com.log.core.template;

import com.alibaba.fastjson.JSON;
import com.log.core.entity.SystemLogRequestParam;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Kevin
 * @date 2024/9/10 11:38
 */
public abstract class AbstractLogTemplate {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${spring.rabbitmq.syslog.queueName}")
    private String queueName;

    /**
     * 抽象方法写入日志
     *
     * @param systemLogRequestParam
     * @param async
     */
    public void writeSystemLog(SystemLogRequestParam systemLogRequestParam, boolean async) {
        // 获取操作人id
        String operatorId = this.getOperatorId();
        // 获取操作人名称
        String operatorName = this.getOperatorName();
        systemLogRequestParam.setOperatorId(operatorId);
        systemLogRequestParam.setOperatorName(operatorName);
        if (async) {
            asyncSendToMq(queueName, systemLogRequestParam);
        } else {
            writeLogToDb(systemLogRequestParam);
        }
    }


    /**
     * 抽象方法,子类实现,往数据库中写入日志
     *
     * @param systemLogRequestParam
     */
    public abstract void writeLogToDb(SystemLogRequestParam systemLogRequestParam);


    /**
     * 获取操作人的id
     *
     * @return
     */
    protected abstract String getOperatorId();

    /**
     * 获取操作人的名称
     *
     * @return
     */
    protected abstract String getOperatorName();


    /**
     * MQ异步推送消息
     *
     * @param queueName
     * @param object
     */
    private void asyncSendToMq(String queueName, Object object) {
        String message = JSON.toJSONString(object);
        // 消息id设置在请求头里面 用UUID做全局ID 保证每次重试消息id唯一
        Message messages = MessageBuilder.withBody(message.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding(StandardCharsets.UTF_8.toString())
                .setMessageId(UUID.randomUUID().toString()).build();
        rabbitTemplate.convertAndSend(queueName, messages);
    }

}
