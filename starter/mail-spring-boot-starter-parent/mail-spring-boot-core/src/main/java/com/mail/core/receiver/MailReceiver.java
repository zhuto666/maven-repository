package com.mail.core.receiver;

import com.alibaba.fastjson.JSON;
import com.mail.core.entity.MailTransferEntity;
import com.mail.core.template.AbstractMailTemplate;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Kevin
 * @date 2024/6/25 14:54
 */
@Component
public class MailReceiver {

    @Autowired(required = false)
    private Map<String, AbstractMailTemplate> abstractMailTemplateMap;

    @Autowired
    private JavaMailSender javaMailSender;

    @RabbitListener(queues = {"${spring.rabbitmq.mail.queueName}"})
    private void consumeMail(Channel channel, Message message) throws IOException {
        if (CollectionUtils.isEmpty(abstractMailTemplateMap)) {
            throw new RuntimeException("abstractMailServiceMap is null");
        }
        // 获取队列消息
        String resultMsg = new String(message.getBody(), StandardCharsets.UTF_8);
        // 反序列化Bean
        MailTransferEntity mailTransferEntity = JSON.parseObject(resultMsg, MailTransferEntity.class);
        AbstractMailTemplate mailTemplate = abstractMailTemplateMap.get(mailTransferEntity.getBeanName());
        try {
            MimeMessage mimeMessage = mailTemplate.getMimeMessage(mailTransferEntity.getForm(), mailTransferEntity.getMailEntity());
            javaMailSender.send(mimeMessage);
            // 消息确认签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            mailTransferEntity.setSuccess(true);
        } catch (Exception ex) {
            // 拒绝签收给死信队列处理
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            mailTransferEntity.setSuccess(false);
        } finally {
            mailTemplate.after(mailTransferEntity);
        }
    }
}
