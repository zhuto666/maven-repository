package com.log.selector;

import com.log.autoconfigure.SystemLogAutoConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Kevin
 * @date 2024/9/10 16:30
 */
public class SystemLogSelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{SystemLogAutoConfiguration.class.getName()};
    }

} 