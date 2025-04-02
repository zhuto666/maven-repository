package com.wx.pay.core.util;

import com.ijpay.core.kit.WxPayKit;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wx.pay.core.properties.WxPayProperties;
import com.wx.pay.core.strategy.factory.WxPayHandlerFactory;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 9:42 星期四
 */

@Slf4j
public class MyPayKitUtil implements InitializingBean {

    public WxPayProperties wxPayProperties;
    public static WxPayProperties staticWxPayProperties;

    @Override
    public void afterPropertiesSet() {
        staticWxPayProperties = this.wxPayProperties;
    }

    public MyPayKitUtil(WxPayProperties wxPayProperties) {
        this.wxPayProperties = wxPayProperties;
    }

    public static CloseableHttpClient initHttpClient() {
        // 私钥
        String privateKey = staticWxPayProperties.getPrivateKey();
        // 商户号
        String mchId = staticWxPayProperties.getMchId();
        // 商户API证书的证书序列号
        String mchSerialNo = staticWxPayProperties.getMchSerialNo();
        // 商户Api私钥
        String apiV3Key = staticWxPayProperties.getApiV3Key();
        try {
            // 加载商户私钥（privateKey：私钥字符串）
            PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(MyPayKitUtil.getPrivateKey(privateKey).getBytes(StandardCharsets.UTF_8)));
            // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
            CertificatesManager certificatesManager = CertificatesManager.getInstance();
            certificatesManager.putMerchant(mchId, new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
            Verifier verifier = certificatesManager.getVerifier(mchId);
            // 初始化httpClient
            return WechatPayHttpClientBuilder.create()
                    .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(verifier)).build();
        } catch (GeneralSecurityException | HttpCodeException | NotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建订单
     *
     * @param total       金额(单位：分)
     * @param description 商品描述
     * @param outTradeNo  商品订单号
     * @return str
     */
    public static String createOrder(Integer total, String description, String outTradeNo, String openid, String tradeType) {
        // 初始化 HttpClient
        CloseableHttpClient closeableHttpClient = MyPayKitUtil.initHttpClient();
        try {
            // 完成签名并执行请求
            try (CloseableHttpResponse response = closeableHttpClient.execute(
                    WxPayHandlerFactory.getWxPayHandlerFactory().httpPost(staticWxPayProperties, total, description,
                            outTradeNo, openid, tradeType))) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // 处理成功
                    log.info("微信支付下单，处理成功，返回结果为：" + EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                } else if (statusCode == 204) {
                    // 处理成功，无返回Body
                    log.info("微信支付下单，处理成功，无返回Body");
                    return EntityUtils.toString(response.getEntity());
                } else {
                    log.error("微信支付下单，处理失败,返回code为：{},返回结果为：{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                }
            } finally {
                // 释放资源
                closeableHttpClient.close();
            }
        } catch (IOException e) {
            log.error("微信支付下单出现异常：{}", e.getMessage());
            throw new CustomException("微信支付下单出现异常：" + e.getMessage());
        }
    }

    /**
     * 关闭订单
     *
     * @param outTradeNo 【商户订单号】 商户系统内部订单号，可以是数字、大小写字母_-*的任意组合，且在同一个商户号下唯一
     * @return str
     */
    public static String closeOrder(String outTradeNo) {
        // 商户订单号查询订单接口
        String outTradeNoUrl = staticWxPayProperties.getOutTradeNoUrl();
        // 商户号
        String mchId = staticWxPayProperties.getMchId();
        // 初始化 HttpClient
        CloseableHttpClient closeableHttpClient = MyPayKitUtil.initHttpClient();
        try {
            // 请求URL
            HttpPost httpPost = new HttpPost(outTradeNoUrl + outTradeNo + "/close");
            Map<String, Object> map = new HashMap<>();
            map.put("mchid", mchId);
            // 请求body参数
            String reqdata = JsonTools.objectToJson(map);
            log.info("请求body参数：{}", reqdata);
            StringEntity entity = new StringEntity(reqdata, "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            //完成签名并执行请求
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // 处理成功
                    log.info("关闭订单，处理成功，返回结果为：" + EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                } else if (statusCode == 204) {
                    // 处理成功，无返回Body
                    log.info("关闭订单，处理成功，无返回Body");
                    return EntityUtils.toString(response.getEntity());
                } else {
                    log.error("关闭订单，请求失败，返回code为：{},返回结果为：{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                }
            } finally {
                // 释放资源
                closeableHttpClient.close();
            }
        } catch (IOException ex) {
            log.error("商户订单号查询订单出现异常：{}", ex.getMessage());
            throw new CustomException("商户订单号查询订单出现异常：" + ex.getMessage());
        }
    }

    /**
     * 密文解密
     *
     * @param associatedData 附加数据。
     * @param nonce          加密使用的随机串。
     * @param ciphertext     Base64编码后的开启/停用结果数据密文。
     * @return str
     */
    public static String payNotify(String associatedData, String nonce, String ciphertext) {
        String apiV3Key = staticWxPayProperties.getApiV3Key();
        try {
            String json = new AesUtil(apiV3Key.getBytes()).decryptToString(associatedData.getBytes(),
                    nonce.getBytes(),
                    ciphertext);
            log.info("微信支付支付通知明文：{}", json);
            return json;
        } catch (GeneralSecurityException ex) {
            log.error("微信支付支付通知异常：" + ex.getMessage());
            throw new CustomException("微信支付支付通知异常：" + ex.getMessage());
        }
    }

    /**
     * 商户订单号查询订单
     *
     * @param outTradeNo 【商户订单号】 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一。
     * @return str
     */
    public static String getOutTradeNo(String outTradeNo) {
        // 商户订单号查询订单接口
        String outTradeNoUrl = staticWxPayProperties.getOutTradeNoUrl();
        // 商户号
        String mchId = staticWxPayProperties.getMchId();
        // 初始化 HttpClient
        CloseableHttpClient closeableHttpClient = MyPayKitUtil.initHttpClient();
        try {
            // 请求URL
            HttpGet httpGet = new HttpGet(outTradeNoUrl + outTradeNo + "?mchid=" + mchId);
            httpGet.setHeader("Accept", "application/json");
            //完成签名并执行请求
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // 处理成功
                    log.info("商户订单号查询订单，处理成功，返回结果为：" + EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                } else if (statusCode == 204) {
                    // 处理成功，无返回Body
                    log.info("商户订单号查询订单，处理成功，无返回Body");
                    return EntityUtils.toString(response.getEntity());
                } else {
                    log.error("商户订单号查询订单，请求失败，返回code为：{},返回结果为：{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                }
            } finally {
                // 释放资源
                closeableHttpClient.close();
            }
        } catch (IOException ex) {
            log.error("商户订单号查询订单出现异常：{}", ex.getMessage());
            throw new CustomException("商户订单号查询订单出现异常：" + ex.getMessage());
        }
    }

    /**
     * 退款申请
     *
     * @param outRefundNo 【商户退款单号】 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @param refund      【退款金额】 退款金额，单位为分，只能为整数，不能超过原订单支付金额。
     * @param total       【原订单金额】 原支付交易的订单总金额，单位为分，只能为整数。
     * @return str
     */
    public static String refunds(String outRefundNo, String refund, String total) {
        // 退款申请接口
        String refundsUrl = staticWxPayProperties.getRefundsUrl();
        // 退款申请回调接口
        String refundsNotifyUrl = staticWxPayProperties.getRefundsNotifyUrl();
        // 初始化 HttpClient
        CloseableHttpClient closeableHttpClient = MyPayKitUtil.initHttpClient();
        try {
            // 请求URL
            HttpPost httpPost = new HttpPost(refundsUrl);
            Map<String, Object> orderNativeMap = new HashMap<>();
            orderNativeMap.put("out_refund_no", outRefundNo);
            orderNativeMap.put("notify_url", refundsNotifyUrl);
            Map<String, Object> amount = new HashMap<>();
            amount.put("refund", refund);
            amount.put("currency", "CNY");
            amount.put("total", total);
            orderNativeMap.put("amount", amount);
            // 请求body参数
            String reqdata = JsonTools.objectToJson(orderNativeMap);
            log.info("请求body参数：{}", reqdata);
            StringEntity entity = new StringEntity(reqdata, "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            // 完成签名并执行请求
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // 处理成功
                    log.info("退款申请，处理成功，返回结果为：" + EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                } else if (statusCode == 204) {
                    // 处理成功，无返回Body
                    log.info("退款申请，处理成功，无返回Body");
                    return EntityUtils.toString(response.getEntity());
                } else {
                    log.error("退款申请，请求失败,返回code为：{},返回结果为：{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                }
            } finally {
                // 释放资源
                closeableHttpClient.close();
            }
        } catch (IOException e) {
            log.error("退款申请出现异常：{}", e.getMessage());
            throw new CustomException("退款申请出现异常：" + e.getMessage());
        }
    }

    /**
     * 查询单笔退款（通过商户退款单号）
     *
     * @param outRefundNo 【商户退款单号】 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     * @return str
     */
    public static String getRefundsOutTradeNo(String outRefundNo) {
        // 退款接口
        String outTradeNoUrl = staticWxPayProperties.getRefundsUrl();
        // 初始化 HttpClient
        CloseableHttpClient closeableHttpClient = MyPayKitUtil.initHttpClient();
        try {
            // 请求URL
            // 退款申请接口与查询单笔退款（通过商户退款单号） 接口一致，区别在于多了一个请求参数和请求方式
            HttpGet httpGet = new HttpGet(outTradeNoUrl + "/" + outRefundNo);
            httpGet.setHeader("Accept", "application/json");
            //完成签名并执行请求
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // 处理成功
                    log.info("查询单笔退款，处理成功，返回结果为：" + EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                } else if (statusCode == 204) {
                    // 处理成功，无返回Body
                    log.info("查询单笔退款，处理成功，无返回Body");
                    return EntityUtils.toString(response.getEntity());
                } else {
                    log.error("查询单笔退款，请求失败,返回code为：{},返回结果为：{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return EntityUtils.toString(response.getEntity());
                }
            } finally {
                // 释放资源
                closeableHttpClient.close();
            }
        } catch (IOException ex) {
            log.error("查询单笔退款出现异常：{}", ex.getMessage());
            throw new CustomException("查询单笔退款出现异常：" + ex.getMessage());
        }
    }

    /**
     * 获取商户私钥
     *
     * @param keyPath 商户私钥证书路径
     * @return 商户私钥
     */
    public static String getPrivateKey(String keyPath) {
        try {
            String originalKey;
            try (InputStream is = new DefaultResourceLoader().getResource(keyPath).getInputStream()) {
                int n;
                StringBuilder sb = new StringBuilder();
                while ((n = is.read()) != -1) {
                    sb.append((char) n);
                }
                originalKey = sb.toString();
            }
            return originalKey;
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
    }

    /**
     * 生成 签名，使用字段AppID、timeStamp、nonceStr、package计算得出的签名值
     *
     * @param prepayId 预支付交易会话标识
     * @return 签名
     */
    public static String generatePaySign(String prepayId) {
        // 初始化 privateKey
        PrivateKey privateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(MyPayKitUtil.getPrivateKey(staticWxPayProperties.getPrivateKey()).getBytes(StandardCharsets.UTF_8)));
        try {
            return JsonTools.objectToJson(WxPayKit.jsApiCreateSign(staticWxPayProperties.getAppId(), prepayId, privateKey));
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
    }

}
