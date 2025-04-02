package com.job.core.registrar;

import com.job.core.task.ScheduledTask;
import com.job.core.util.LocalDateTimeUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/12/9 15:11 星期一
 */
@Component("cronTaskRegistrar")
public class CronTaskRegistrar implements DisposableBean {

    /**
     * 定时任务
     */
    private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    private TaskScheduler taskScheduler;

    /**
     * 返回所有的定时任务
     */
    public Map<Runnable, ScheduledTask> getScheduledTasks() {
        return this.scheduledTasks;
    }

    private CronTaskRegistrar() {
        // 私有构造函数，确保不能直接实例化
    }

    public CronTaskRegistrar(TaskScheduler taskScheduler) {
        // 私有构造函数，确保不能直接实例化
        this.taskScheduler = taskScheduler;
    }

    /**
     * 添加定时任务
     *
     * @param task           任务
     * @param cronExpression 表达式
     * @param flag           执行策略 true:执行一次 false:循环执行
     */
    public void addCronTask(Runnable task, String cronExpression, Boolean flag) {
        addCronTask(new CronTask(task, cronExpression), flag);
    }

    /**
     * 添加定时任务
     *
     * @param cronTask 任务
     * @param flag     执行策略 true:执行一次 false:循环执行
     */
    public void addCronTask(CronTask cronTask, Boolean flag) {
        if (cronTask != null) {
            Runnable task = cronTask.getRunnable();
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }
            this.scheduledTasks.put(task, scheduleCronTask(cronTask, flag));
        }
    }

    /**
     * 移除定时任务
     */
    public void removeCronTask(Runnable task) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(task);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    /**
     * 添加定时任务
     *
     * @param cronTask 任务
     * @param flag     执行策略 true:执行一次 false:循环执行
     * @return ScheduledTask
     */
    public ScheduledTask scheduleCronTask(CronTask cronTask, Boolean flag) {
        ScheduledTask scheduledTask = new ScheduledTask();
        if (flag) {
            // 解析 Cron 表达式
            CronExpression cronExpression = CronExpression.parse(cronTask.getTrigger().toString());
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 获取下次执行时间
            LocalDateTime nextExecutionTime = cronExpression.next(now);
            Date date = LocalDateTimeUtil.convertLocalDteTimeToDate(Objects.requireNonNull(nextExecutionTime));
            // 执行策略 true:执行一次 false:循环执行为"执行一次"时，只执行一次定时任务
            scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), date);
            return scheduledTask;
        }
        // 执行策略 true:执行一次 false:循环执行为"循环执行"时，直接执行定时任务
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }


    /**
     * 取消所有定时任务
     */
    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }

}