package com.mail.core.factory;

import com.mail.core.enums.MailTypeEnum;
import com.mail.core.template.AbstractMailTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kevin
 * @date 2024/6/25 14:54
 */
public class MailFactory {

    @Autowired(required = false)
    private Map<String, AbstractMailTemplate> abstractMailTemplateMap;

    public AbstractMailTemplate getMailTemplate(String mailType) {
        if (CollectionUtils.isEmpty(abstractMailTemplateMap)) {
            throw new RuntimeException("abstractMailServiceMap is null");
        }
        if (StringUtils.isEmpty(mailType)) {
            throw new RuntimeException("mail type required");
        }
        MailTypeEnum mailTypeEnum = MailTypeEnum.getMailByType(mailType);
        Optional.ofNullable(mailTypeEnum).orElseThrow(() -> new RuntimeException("mail type invalid"));
        Collection<AbstractMailTemplate> values = abstractMailTemplateMap.values();
        for (AbstractMailTemplate mailTemplate : values) {
            if (mailTemplate.isSupport(mailType)) {
                return mailTemplate;
            }
        }
        throw new RuntimeException("no supported implementations were found");
    }
}
