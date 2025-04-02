package com.mail.core.utils;

import com.mail.core.entity.MailEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author Kevin
 * @date 2024/6/25 14:21
 */
public class MailUtils {

    public static MimeMessageHelper getMimeMessageHelper(MimeMessage mimeMessage, MailEntity mailEntity, String from)
            throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        // 设置发件人邮箱
        Assert.notNull(from, "from is null");
        mimeMessageHelper.setFrom(from);
        // 设置收件人邮箱
        Assert.notNull(mailEntity.getTo(), "to is null");
        mimeMessageHelper.setTo(mailEntity.getTo());
        // 设置邮件标题
        Assert.notNull(mailEntity.getSubject(), "subject is null");
        mimeMessageHelper.setSubject(mailEntity.getSubject());
        if (null != mailEntity.getCc()) {
            // 设置CC
            mimeMessageHelper.setCc(mailEntity.getCc());
        }
        if (null != mailEntity.getBcc() && mailEntity.getBcc().length > 0) {
            // 设置BCC
            mimeMessageHelper.setBcc(mailEntity.getBcc());
        }
        if (null != mailEntity.getSentDate()) {
            // 设置发送时间
            mimeMessageHelper.setSentDate(mailEntity.getSentDate());
        }
        if (null != mailEntity.getFilePathList()) {
            // 添加附件
            List<String> filePathList = mailEntity.getFilePathList();
            for (String filePath : filePathList) {
                FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
                String fileName = fileSystemResource.getFilename();
                mimeMessageHelper.addAttachment(Objects.requireNonNull(fileName), fileSystemResource);
            }
        }
        return mimeMessageHelper;
    }
}
