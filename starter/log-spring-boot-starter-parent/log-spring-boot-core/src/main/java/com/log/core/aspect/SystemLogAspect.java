package com.log.core.aspect;

import com.alibaba.fastjson.JSON;
import com.log.core.annotation.SystemLog;
import com.log.core.entity.SystemLogRequestParam;
import com.log.core.exception.SystemLogException;
import com.log.core.template.AbstractLogTemplate;
import com.log.core.util.BeanNameUtil;
import com.log.core.util.IpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author Kevin
 * @date 2024/9/10 11:33
 */
@Aspect
public class SystemLogAspect {

    @Autowired(required = false)
    private Map<String, AbstractLogTemplate> abstractLogTemplateHashMap;

    @Around(value = "@annotation(systemLog)")
    public Object around(ProceedingJoinPoint point, SystemLog systemLog) throws Throwable {
        if (ObjectUtils.isEmpty(abstractLogTemplateHashMap) || abstractLogTemplateHashMap.size() == 0) {
            throw new SystemLogException("abstractLogTemplate is null");
        }
        String beanName = BeanNameUtil.getConvert(systemLog.beanClass().getSimpleName());
        if (!abstractLogTemplateHashMap.containsKey(beanName)) {
            throw new SystemLogException("beanType is null");
        }
        // 获取出模板方法实现类
        AbstractLogTemplate abstractLogTemplate = abstractLogTemplateHashMap.get(beanName);
        // class名称,方法名,参数,返回值,执行时间
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        Object[] args = point.getArgs();
        Object result;
        // 封装日志对象
        SystemLogRequestParam systemLogRequestParam = new SystemLogRequestParam(
                className,
                methodName,
                JSON.toJSONString(args),
                null,
                Boolean.TRUE,
                (long) 0,
                IpUtil.getIpAddress(),
                IpUtil.getUserSystem(),
                IpUtil.getUserBrowser(),
                IpUtil.getServerIpPort(),
                beanName,
                systemLog.systemCallTypeEnum().getSystemTypeCode(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        long beginTime = System.currentTimeMillis();
        try {
            result = point.proceed();
            long endTime = System.currentTimeMillis() - beginTime;
            systemLogRequestParam.setResult(JSON.toJSONString(result));
            systemLogRequestParam.setResponseTime(endTime);
        } catch (Throwable throwable) {
            systemLogRequestParam.setStatus(Boolean.FALSE);
            systemLogRequestParam.setResult(JSON.toJSONString(getStackTrace(throwable)));
            throw throwable;
        } finally {
            abstractLogTemplate.writeSystemLog(systemLogRequestParam, systemLog.async());
        }
        return result;
    }

    /**
     * 获取异常详情信息
     *
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }

}
