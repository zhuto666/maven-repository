package com.mail.core.template.impl;

import com.mail.core.entity.MailEntity;
import com.mail.core.entity.MailTransferEntity;
import com.mail.core.enums.MailTypeEnum;
import com.mail.core.template.AbstractMailTemplate;
import com.mail.core.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Kevin
 * @date 2024/6/25 14:25
 */
public class HtmlMailTemplateImpl extends AbstractMailTemplate {


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public boolean isSupport(String mailType) {
        return mailType.equals(MailTypeEnum.HTML.getType());
    }

    @Override
    public void before(MailTransferEntity mailTransferEntity) {

    }

    @Override
    public void after(MailTransferEntity mailTransferEntity) {

    }

    @Override
    public MimeMessage getMimeMessage(String from, MailEntity mailEntity) {
        try {
            // 验证Html模板地址Null
            Assert.notNull(mailEntity.getTemplate(), "template is null");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = MailUtils.getMimeMessageHelper(mimeMessage, mailEntity, from);
            // 添加正文（使用thymeleaf模板）
            Context context = new Context();
            context.setVariables(mailEntity.getTemplatePara());
            String content = templateEngine.process(mailEntity.getTemplate(), context);
            mimeMessageHelper.setText(content, true);
            return mimeMessage;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
