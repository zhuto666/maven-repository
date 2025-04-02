package com.mail.core.template.impl;

import com.mail.core.entity.MailEntity;
import com.mail.core.entity.MailTransferEntity;
import com.mail.core.enums.MailTypeEnum;
import com.mail.core.template.AbstractMailTemplate;
import com.mail.core.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Kevin
 * @date 2024/6/25 14:25
 */
public class SimpleMailTemplateImpl extends AbstractMailTemplate {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean isSupport(String mailType) {
        return mailType.equals(MailTypeEnum.SIMPLE.getType());
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
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = MailUtils.getMimeMessageHelper(mimeMessage, mailEntity, from);
            // 添加正文
            mimeMessageHelper.setText(mailEntity.getText());
            return mimeMessage;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
