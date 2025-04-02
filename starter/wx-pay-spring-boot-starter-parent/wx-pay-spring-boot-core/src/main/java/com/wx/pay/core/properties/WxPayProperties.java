package com.wx.pay.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 9:37 星期四
 */
@Data
@ConfigurationProperties(WxPayProperties.PREFIX)
public class WxPayProperties {

    public static final String PREFIX = "wx-pay";

    /**
     * 商户ApiV2Key私钥
     */
    private String apiV2Key;

    /**
     * 商户Api私钥
     */
    private String apiV3Key;

    /**
     * 商户API证书的证书序列号
     */
    private String mchSerialNo;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 公众号Id
     */
    private String appId;

    /**
     * 支付下单统一前缀
     */
    private String url;

    /**
     * native 支付调用接口
     */
    private String payUrl;

    /**
     * 支付回调接口
     */
    private String notifyUrl;

    /**
     * 退款接口
     */
    private String refundsUrl = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    /**
     * 退款回调接口
     */
    private String refundsNotifyUrl = "http://crm-sfkj.cjl2616.cn/wx_pay/native/refunds/notify";

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 商户订单号查询订单
     */
    private String outTradeNoUrl = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";

    /**
     * # JSAPI订单有效期(分钟)
     */
    private Integer jsapiExpire;
}
