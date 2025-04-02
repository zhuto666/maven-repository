package com.wx.pay.autoconfigure.autoconfigure;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wx.pay.core.properties.WxPayProperties;
import com.wx.pay.core.strategy.service.impl.WxPayJSAPIHandler;
import com.wx.pay.core.strategy.service.impl.WxPayNativeHandler;
import com.wx.pay.core.util.MyPayKitUtil;
import com.zhongqin.commons.util.SpringContextUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 9:34 星期四
 */
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayAutoConfigure {

    @Bean
    MyPayKitUtil myPayKitUtil(WxPayProperties wxPayProperties) {
        return new MyPayKitUtil(wxPayProperties);
    }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public WxPayNativeHandler wxPayNativeHandler() {
        return new WxPayNativeHandler();
    }

    @Bean
    public WxPayJSAPIHandler wxPayJSAPIHandler() {
        return new WxPayJSAPIHandler();
    }

}
