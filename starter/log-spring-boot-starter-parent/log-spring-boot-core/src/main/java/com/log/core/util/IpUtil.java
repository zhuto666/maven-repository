package com.log.core.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Kevin
 * @Date: 2019/4/30 10:49
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final int IP_MAX_LENGTH = 15;

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getResponse();
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，获取X-Forwarded-For中第一个非unknown的有效IP字符串就是真实ip。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @return
     */
    public static String getIpAddress() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return "";
            }
            // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
            } else if (ip.length() > IP_MAX_LENGTH) {
                String[] ips = ip.split(",");
                for (String strIp : ips) {
                    if (!(UNKNOWN.equalsIgnoreCase(strIp))) {
                        ip = strIp;
                        break;
                    }
                }
            }
            return ip;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取服务部署根路径 http:// + ip + port
     *
     * @return
     */
    public static String getServerIpPort() {
        HttpServletRequest request = getRequest();
        if (Objects.isNull(request)) {
            return "";
        }
        return Objects.requireNonNull(request).getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    /**
     * 获取当前用户浏览器型号
     */
    public static String getUserBrowser() {
        try {
            HttpServletRequest request = getRequest();
            if (Objects.isNull(request)) {
                return "";
            }
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            Browser browser = userAgent.getBrowser();
            return browser.toString();
        } catch (Exception e) {
            return StringPool.EMPTY;
        }
    }


    /**
     * 获取当前用户系统型号
     */
    public static String getUserSystem() {
        try {
            HttpServletRequest request = getRequest();
            if (Objects.isNull(request)) {
                return "";
            }
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            OperatingSystem operatingSystem = userAgent.getOperatingSystem();
            return operatingSystem.toString();
        } catch (Exception e) {
            return StringPool.EMPTY;
        }
    }
}
