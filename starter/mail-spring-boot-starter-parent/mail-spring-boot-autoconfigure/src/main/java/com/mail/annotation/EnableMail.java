package com.mail.annotation;

import com.mail.autoconfigure.MailAutoConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/6/25 16:48 星期二
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MailAutoConfigure.class})
public @interface EnableMail {

}
