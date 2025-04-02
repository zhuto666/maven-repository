package com.mail.core.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin
 * @date 2024/6/25 14:13
 */
@Data
public class MailEntity {

    /**
     * 收件人
     */
    private String[] to;

    /**
     * 抄送
     */
    private String[] cc;

    /**
     * 暗抄送
     */
    private String[] bcc;

    /**
     * 发送时间
     */
    private Date sentDate;

    /**
     * 主题
     */
    private String subject;

    /**
     * 文本
     */
    private String text;

    /**
     * 模板路径
     */
    private String template;

    /**
     * 模板参数
     */
    private Map<String, Object> templatePara;

    /**
     * 附件地址
     */
    private List<String> filePathList;

    /**
     * 同步异步
     */
    private Boolean async = Boolean.TRUE;


    public MailEntity() {
    }

    /**
     * HTML模板构造方法
     *
     * @param subject
     * @param template
     * @param templatePara
     * @param to
     */
    public MailEntity(String subject, String template, Map<String, Object> templatePara, String... to) {
        this.to = to;
        this.subject = subject;
        this.template = template;
        this.templatePara = templatePara;
    }

    /**
     * HTML模板构造方法 带附件
     *
     * @param subject
     * @param template
     * @param templatePara
     * @param filePathList
     * @param to
     */
    public MailEntity(String subject, String template, Map<String, Object> templatePara, List<String> filePathList, String... to) {
        this.to = to;
        this.subject = subject;
        this.template = template;
        this.templatePara = templatePara;
        this.filePathList = filePathList;
    }

    /**
     * 普通邮件构造方法
     *
     * @param subject
     * @param text
     * @param template
     * @param to
     */
    public MailEntity(String subject, String text, String template, String... to) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.template = template;
    }
}
