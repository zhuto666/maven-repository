package com.log.core.util;

/**
 * @author Kevin
 * @date 2024/9/10 15:00
 */
public class BeanNameUtil {

    /**
     * 将首字母转小写
     *
     * @param str
     * @return
     */
    public static String getConvert(String str) {
        String first = str.substring(0, 1);
        String after = str.substring(1);
        first = first.toLowerCase();
        return first + after;
    }

}
