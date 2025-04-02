package com.wx.pay.core.strategy.service.impl;

import com.wx.pay.core.properties.WxPayProperties;
import com.wx.pay.core.strategy.service.AbstractWxPayHandler;
import com.zhongqin.commons.util.JsonTools;
import com.zhongqin.commons.util.LocalDateTimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 11:58 星期四
 */
@Service
public class WxPayJSAPIHandler extends AbstractWxPayHandler {


    @Override
    protected String splicingUrl(String url, String tradeType) {
        return url + tradeType;
    }

    @Override
    protected String request(WxPayProperties wxPayProperties, Integer total, String description, String outTradeNo, String openid) {
        Map<String, Object> orderNativeMap = new HashMap<>();
        orderNativeMap.put("appid", wxPayProperties.getAppId());
        orderNativeMap.put("mchid", wxPayProperties.getMchId());
        orderNativeMap.put("out_trade_no", outTradeNo);
        orderNativeMap.put("description", description);
        orderNativeMap.put("notify_url", wxPayProperties.getNotifyUrl());
        // 生成订单过期时间
        String timeExpire = LocalDateTimeUtil.getStringByLocalDateTime(LocalDateTime.now().plusMinutes(wxPayProperties.getJsapiExpire()), "yyyy-MM-dd'T'HH:mm:ss+08:00");
        // 设置订单过期时间
        orderNativeMap.put("time_expire", timeExpire);
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency", "CNY");
        amountMap.put("total", total);
        orderNativeMap.put("amount", amountMap);
        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("openid", openid);
        orderNativeMap.put("payer", payerMap);
        // 请求body参数
        return JsonTools.objectToJson(orderNativeMap);
    }

}
