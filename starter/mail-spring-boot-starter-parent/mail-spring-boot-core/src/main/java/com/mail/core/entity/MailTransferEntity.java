package com.mail.core.entity;

import lombok.Data;

/**
 * @author Kevin
 * @date 2024/6/25 14:17
 */
@Data
public class MailTransferEntity {

    /**
     * 邮件实体对象
     */
    private MailEntity mailEntity;

    /**
     * 邮件Id
     */
    private Object mailId;

    /**
     * bean名称
     */
    private String beanName;

    /**
     * 发件人
     */
    private String form;

    /**
     * 邮件是否发送成功
     */
    private boolean success = Boolean.FALSE;
}
