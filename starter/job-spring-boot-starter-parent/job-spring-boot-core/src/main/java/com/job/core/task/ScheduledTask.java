package com.job.core.task;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/12/9 15:07 星期一
 */
public class ScheduledTask {

    /**
     * 任务
     */
    public volatile ScheduledFuture<?> future;

    /**
     * 取消定时任务
     */
    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }

}
