package com.log.selector;

import com.log.autoconfigure.SystemLogReceiverAutoConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Kevin
 * @date 2024/9/10 16:30
 */
public class SystemLogReceiverSelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{SystemLogReceiverAutoConfiguration.class.getName()};
    }

} 