package com.wx.pay.core.strategy.service;

import com.wx.pay.core.properties.WxPayProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 11:55 星期四
 */
@Slf4j
public abstract class AbstractWxPayHandler {

    /**
     * 根据支付类型拼接请求路径
     *
     * @param url       初始请求路径
     * @param tradeType 支付方式
     * @return
     */
    protected abstract String splicingUrl(String url, String tradeType);

    /**
     * 配置请求参数
     *
     * @param wxPayProperties 微信支付配置
     * @param total           金额
     * @param description     描述
     * @param outTradeNo      订单号
     * @param openid          openid
     * @return str
     */
    protected abstract String request(WxPayProperties wxPayProperties, Integer total, String description, String outTradeNo, String openid);

    /**
     * 执行工厂
     *
     * @param wxPayProperties      微信支付配置
     * @param total                金额
     * @param description          描述
     * @param outTradeNo           订单号
     * @param openid               openid
     * @param tradeType            支付类型
     * @param abstractWxPayHandler 处理者
     * @return
     */
    public HttpPost execute(WxPayProperties wxPayProperties, Integer total, String description, String outTradeNo, String openid, String tradeType, AbstractWxPayHandler abstractWxPayHandler) {
        // 请求头
        String url = abstractWxPayHandler.splicingUrl(wxPayProperties.getUrl(), tradeType);
        log.info("请求头url：{}", url);
        HttpPost httpPost = new HttpPost(url);
        // 请求体
        String requestData = abstractWxPayHandler.request(wxPayProperties, total, description, outTradeNo, openid);
        log.info("请求body参数：{}", requestData);
        StringEntity entity = new StringEntity(requestData, "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        return httpPost;
    }

}
