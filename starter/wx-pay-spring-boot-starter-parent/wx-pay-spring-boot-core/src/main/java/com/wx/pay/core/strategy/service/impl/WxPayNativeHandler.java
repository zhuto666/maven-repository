package com.wx.pay.core.strategy.service.impl;

import com.wx.pay.core.properties.WxPayProperties;
import com.wx.pay.core.strategy.service.AbstractWxPayHandler;
import com.zhongqin.commons.util.JsonTools;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 11:58 星期四
 */
@Service
public class WxPayNativeHandler extends AbstractWxPayHandler {


    @Override
    protected String splicingUrl(String url, String tradeType) {
        return url + tradeType;
    }

    @Override
    protected String request(WxPayProperties wxPayProperties, Integer total, String description, String outTradeNo, String openid) {
        Map<String, Object> orderNativeMap = new HashMap<>();
        orderNativeMap.put("appid", wxPayProperties.getAppId());
        orderNativeMap.put("mchid", wxPayProperties.getMchId());
        orderNativeMap.put("description", description);
        orderNativeMap.put("notify_url", wxPayProperties.getNotifyUrl());
        orderNativeMap.put("out_trade_no", outTradeNo);
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency", "CNY");
        amountMap.put("total", total);
        orderNativeMap.put("amount", amountMap);
        // 请求body参数
        return JsonTools.objectToJson(orderNativeMap);
    }


}
