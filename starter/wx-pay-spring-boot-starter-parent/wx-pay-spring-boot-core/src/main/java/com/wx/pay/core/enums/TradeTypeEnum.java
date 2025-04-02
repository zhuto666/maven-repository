package com.wx.pay.core.enums;

import lombok.Getter;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/18 11:46 星期四
 */
@Getter
public enum TradeTypeEnum {

    JSAPI("jsapi", "公众号支付", "wxPayJSAPIHandler"),

    NATIVE("native", "扫码支付", "wxPayNativeHandler"),

    App("app", "App支付", ""),

    M_WEB("h5", "H5支付", ""),

    MICROPAY("MICROPAY", "付款码支付", ""),

    FACEPAY("FACEPAY", "刷脸支付", ""),

    ;

    private final String tradeTypeCode;
    private final String tradeTypeClass;
    private final String tradeTypeDescribe;

    TradeTypeEnum(String tradeTypeCode, String tradeTypeDescribe, String tradeTypeClass) {
        this.tradeTypeCode = tradeTypeCode;
        this.tradeTypeDescribe = tradeTypeDescribe;
        this.tradeTypeClass = tradeTypeClass;
    }

    public static String getTradeTypeClass(String tradeTypeCode) {
        for (TradeTypeEnum tradeType : TradeTypeEnum.values()) {
            if (tradeType.getTradeTypeCode().equals(tradeTypeCode)) {
                return tradeType.getTradeTypeClass();
            }
        }
        return null;
    }
}
