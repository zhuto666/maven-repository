package com.mail.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevin
 * @date 2024/6/25 14:21
 */
@AllArgsConstructor
@Getter
public enum MailTypeEnum {

    /**
     * 普通邮件
     */
    SIMPLE("普通邮件", "SIMPLE"),

    /**
     * HTML格式邮件
     */
    HTML("HTML模板格式邮件", "HTML");

    /**
     * 邮件描述
     */
    private final String desc;

    /**
     * 邮件类型
     */
    private final String type;

    public static MailTypeEnum getMailByType(String type) {
        MailTypeEnum[] values = MailTypeEnum.values();
        for (MailTypeEnum mailType : values) {
            if (type.equals(mailType.getType())) {
                return mailType;
            }
        }
        return null;
    }
}
