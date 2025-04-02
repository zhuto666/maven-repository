package com.log.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Kevin
 * @date 2024/9/10 13:53
 */
@Data
@AllArgsConstructor
public class SystemLogRequestParam {
    /**
     * 控制器名称
     */
    private String controllerName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数
     */
    private String params;

    /**
     * 返回值
     */
    private String result;

    /**
     * 响应状态
     */
    private Boolean status;

    /**
     * 响应时间
     */
    private Long responseTime;

    /**
     * IP地址
     */
    private String clientIp;

    /**
     * 客户端系统
     */
    private String clientSystem;

    /**
     * 客户端浏览器
     */
    private String clientBrowser;

    /**
     * 服务端IP
     */
    private String serverIp;

    /**
     * 操作人Id
     */
    private String operatorId;

    /**
     * bean
     */
    private String beanName;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 系统调用类型
     */
    private String systemCallTypeCode;

    /**
     * 请求时间
     * yyyy-MM-dd HH:mm:ss
     */
    private String requestGmt;

    public SystemLogRequestParam(String controllerName, String methodName, String params, String result, Boolean status, Long responseTime, String clientIp,
                                 String clientSystem, String clientBrowser, String serverIp, String beanName, String systemCallTypeCode, String requestGmt) {
        this.controllerName = controllerName;
        this.methodName = methodName;
        this.params = params;
        this.result = result;
        this.status = status;
        this.responseTime = responseTime;
        this.clientIp = clientIp;
        this.clientSystem = clientSystem;
        this.clientBrowser = clientBrowser;
        this.serverIp = serverIp;
        this.beanName = beanName;
        this.systemCallTypeCode = systemCallTypeCode;
        this.requestGmt = requestGmt;
    }
}
