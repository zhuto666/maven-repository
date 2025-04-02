package com.log.annotation;

import com.log.selector.SystemLogSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kevin
 * @date 2024/9/10 16:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({SystemLogSelector.class})
public @interface EnableSystemLog {
}
