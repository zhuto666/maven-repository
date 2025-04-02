package com.log.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevin
 * @date 2024/9/10
 */
@AllArgsConstructor
@Getter
public enum SystemCallTypeEnum {

    /**
     * 系统Api
     */
    SYSTEM_API("SYSTEM-API"),

    /**
     * 系统提供给第三方的Web Hook Api
     */
    SYSTEM_WEB_HOOK("SYSTEM-WEB-HOOK"),

    /**
     * 第三方Api
     */
    THIRD_PARTY_API("THIRD-PARTY-API");

    private final String systemTypeCode;

}
