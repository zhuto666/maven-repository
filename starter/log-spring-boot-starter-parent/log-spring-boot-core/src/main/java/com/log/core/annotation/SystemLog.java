package com.log.core.annotation;

import com.log.core.enums.SystemCallTypeEnum;

import java.lang.annotation.*;

/**
 * @author Kevin
 * @date 2024/9/10 11:17
 */
@Documented
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemLog {

    /**
     * 同步异步
     */
    boolean async() default true;

    /**
     * 写日志bean的Class地址
     *
     * @return
     */
    Class<?> beanClass();

    /**
     * 系统调用类型
     *
     * @return
     */
    SystemCallTypeEnum systemCallTypeEnum() default SystemCallTypeEnum.SYSTEM_API;
}
