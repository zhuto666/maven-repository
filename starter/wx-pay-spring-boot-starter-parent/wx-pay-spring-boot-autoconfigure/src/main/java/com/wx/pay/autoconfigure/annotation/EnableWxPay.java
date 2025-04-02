package com.wx.pay.autoconfigure.annotation;

import com.wx.pay.autoconfigure.selector.WxPaySelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:19 星期二
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({WxPaySelector.class})
public @interface EnableWxPay {
}
