package com.wx.pay.core.strategy.factory;

import com.wx.pay.core.enums.TradeTypeEnum;
import com.wx.pay.core.properties.WxPayProperties;
import com.wx.pay.core.strategy.service.AbstractWxPayHandler;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.ExceptionUtil;
import com.zhongqin.commons.util.SpringContextUtil;
import org.apache.http.client.methods.HttpPost;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 13:19 星期四
 */
@Component
public class WxPayHandlerFactory {

    private static WxPayHandlerFactory instance;

    private WxPayHandlerFactory() {
        // 私有构造函数，确保不能直接实例化
    }

    /**
     * 静态工厂方法用于创建实例
     *
     * @return wxPayHandlerFactory
     */
    public static WxPayHandlerFactory getInstance() {
        if (instance == null) {
            instance = new WxPayHandlerFactory();
        }
        return instance;
    }

    /**
     * 使用@Bean注解将静态工厂方法的返回值注入Spring容器
     *
     * @return WxPayHandlerFactory
     */
    @Bean
    public static WxPayHandlerFactory getWxPayHandlerFactory() {
        return getInstance();
    }


    /**
     * 根据交易类型获取请求体
     *
     * @param tradeType 交易类型
     * @return request
     */
    public HttpPost httpPost(WxPayProperties wxPayProperties, Integer total, String description, String outTradeNo, String openid, String tradeType) {
        try {
            // 根据支付方式去寻找相对应的执行者
            AbstractWxPayHandler abstractWxPayHandler = (AbstractWxPayHandler)
                    SpringContextUtil.getBean(TradeTypeEnum.getTradeTypeClass(tradeType));
            return abstractWxPayHandler.execute(wxPayProperties, total, description,
                    outTradeNo, openid, tradeType, abstractWxPayHandler);
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ExceptionUtil.getStackTrace(ex));
        }
    }

}
