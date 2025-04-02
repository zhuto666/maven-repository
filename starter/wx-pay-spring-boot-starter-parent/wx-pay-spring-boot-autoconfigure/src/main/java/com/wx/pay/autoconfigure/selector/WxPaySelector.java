package com.wx.pay.autoconfigure.selector;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import com.wx.pay.autoconfigure.autoconfigure.WxPayAutoConfigure;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:20 星期二
 */
public class WxPaySelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{WxPayAutoConfigure.class.getName()};
    }

}