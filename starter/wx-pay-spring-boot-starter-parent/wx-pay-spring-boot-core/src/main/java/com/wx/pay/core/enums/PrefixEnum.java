package com.wx.pay.core.enums;

import lombok.Getter;

/**
 * 默认前缀枚举
 *
 * @author Kevin
 * @version 1.0
 * @date 2024/7/15 13:48 星期一
 */
@Getter
public enum PrefixEnum {

    /**
     * 用户默认昵称前缀
     */
    NICK_NAME("用户_"),

    /**
     * 后台用户昵称默认前缀
     */
    SYS_NAME("管理员_"),

    /**
     * 订单号默认前缀
     */
    ORDER_PRE("MALL"),
    ;

    private final String prefix;

    public String getPrefix() {
        return this.prefix;
    }

    PrefixEnum(String prefix) {
        this.prefix = prefix;
    }

}
