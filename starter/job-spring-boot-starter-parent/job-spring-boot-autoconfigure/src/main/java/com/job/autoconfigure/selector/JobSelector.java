package com.job.autoconfigure.selector;

import com.job.autoconfigure.autoconfigure.JobAutoConfigure;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:20 星期二
 */
public class JobSelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{JobAutoConfigure.class.getName()};
    }

}