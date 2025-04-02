package com.wx.pay.core.enums;

import lombok.Getter;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/7/19 13:42 星期五
 */
@Getter
public enum TradeStateEnum {

    /**
     * SUCCESS：支付成功
     */
    SUCCESS("SUCCESS", "支付成功"),

    /**
     * REFUND：转入退款
     */
    REFUND("REFUND", "转入退款"),

    /**
     * NOTPAY：未支付
     */
    NOTPAY("NOTPAY", "未支付"),

    /**
     * CLOSED：已关闭
     */
    CLOSED("CLOSED", "已关闭"),

    /**
     * REVOKED：已撤销（付款码支付）
     */
    REVOKED("REVOKED", "已撤销（付款码支付）"),

    /**
     * USERPAYING：用户支付中（付款码支付）
     */
    USERPAYING("USERPAYING", "用户支付中（付款码支付）"),

    /**
     * PAYERROR：支付失败(其他原因，如银行返回失败)
     */
    PAYERROR("PAYERROR", "支付失败(其他原因，如银行返回失败)"),
    ;

    private final String tradeStateCode;
    private final String tradeStateDescribe;

    TradeStateEnum(String tradeStateCode, String tradeStateDescribe) {
        this.tradeStateCode = tradeStateCode;
        this.tradeStateDescribe = tradeStateDescribe;
    }

}
