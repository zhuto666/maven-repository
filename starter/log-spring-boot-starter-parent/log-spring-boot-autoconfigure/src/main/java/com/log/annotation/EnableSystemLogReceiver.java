package com.log.annotation;

import com.log.selector.SystemLogReceiverSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kevin
 * @date 2024/9/10 16:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({SystemLogReceiverSelector.class})
public @interface EnableSystemLogReceiver {
}
