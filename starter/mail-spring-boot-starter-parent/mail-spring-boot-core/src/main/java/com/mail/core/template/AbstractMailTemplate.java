package com.mail.core.template;

import com.alibaba.fastjson.JSON;
import com.mail.core.entity.MailEntity;
import com.mail.core.entity.MailTransferEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Kevin
 * @date 2024/6/25 14:25
 */
public abstract class AbstractMailTemplate {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.rabbitmq.mail.queueName}")
    private String queueName;


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired

    private RabbitTemplate rabbitTemplate;

    protected AbstractMailTemplate() {

    }

    /**
     * 是否支持
     *
     * @param mailType
     * @return
     */
    public abstract boolean isSupport(String mailType);

    /**
     * 发送邮件之前
     *
     * @param mailTransferEntity
     */
    public abstract void before(MailTransferEntity mailTransferEntity);

    /**
     * 发送邮件之后
     *
     * @param mailTransferEntity
     */
    public abstract void after(MailTransferEntity mailTransferEntity);

    /**
     * 根据不同邮件类型包装MimeMessage返回
     *
     * @param from
     * @param mailEntity
     * @return
     */
    public abstract MimeMessage getMimeMessage(String from, MailEntity mailEntity);

    /**
     * 发送邮件
     *
     * @param mailEntity
     */
    public void send(MailEntity mailEntity) {
        MailTransferEntity mailTransferEntity = getMailToTransferEntity(mailEntity);
        before(mailTransferEntity);
        // 异步发送邮件
        if (mailEntity.getAsync()) {
            this.asyncSendToMq(queueName, mailTransferEntity);
            return;
        }
        // 同步发送邮件
        MimeMessage mimeMessage = getMimeMessage(from, mailEntity);
        javaMailSender.send(mimeMessage);
        mailTransferEntity.setSuccess(Boolean.TRUE);
        after(mailTransferEntity);
    }

    /**
     * 封装传输对象
     *
     * @param mailEntity
     * @return
     */
    private MailTransferEntity getMailToTransferEntity(MailEntity mailEntity) {
        MailTransferEntity mailTransferEntity = new MailTransferEntity();
        mailTransferEntity.setMailEntity(mailEntity);
        mailTransferEntity.setBeanName(getConvert(this.getClass().getSimpleName()));
        mailTransferEntity.setForm(from);
        return mailTransferEntity;
    }

    /**
     * 将首字母转小写
     *
     * @param str
     * @return
     */
    private static String getConvert(String str) {
        String first = str.substring(0, 1);
        String after = str.substring(1);
        first = first.toLowerCase();
        return first + after;
    }

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
