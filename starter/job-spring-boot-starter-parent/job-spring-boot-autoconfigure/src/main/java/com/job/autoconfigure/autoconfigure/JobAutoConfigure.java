package com.job.autoconfigure.autoconfigure;

import com.job.core.config.SchedulingConfig;
import com.job.core.registrar.CronTaskRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/12/13 14:12 星期五
 */
@Configuration(proxyBeanMethods = false)
public class JobAutoConfigure {

    @Bean
    public TaskScheduler taskScheduler() {
        return new SchedulingConfig().taskScheduler();
    }

    @Bean
    public CronTaskRegistrar cronTaskRegistrar(TaskScheduler taskScheduler) {
        return new CronTaskRegistrar(taskScheduler);
    }

}
