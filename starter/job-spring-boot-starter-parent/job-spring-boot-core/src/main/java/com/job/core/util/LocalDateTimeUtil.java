package com.job.core.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * 时间转换工具类
 *
 * @author Kevin
 * @date 2020/9/10 14:13
 */
public class LocalDateTimeUtil {

    private static final String START_TIME = " 00:00:00";

    private static final String END_TIME = " 23:59:59";

    private static final String START_DAY = "-01-01";

    private static final String END_DAY = "-12-31";

    /**
     * 得到mysql默认最早时间 1970-01-01 00:00:00
     *
     * @return
     */
    public static LocalDateTime defaultDate() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse("1970-01-01 00:00:00", df);
    }


    /**
     * Date转换为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertDateToLocalDteTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime
     * @return
     */
    public static Date convertLocalDteTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param pattern
     * @return
     */
    public static String getLocalDateTimeNow(String pattern) {
        return getStringByLocalDateTime(LocalDateTime.now(), pattern);
    }

    /**
     * LocalDateTime转指定格式String
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String getStringByLocalDateTime(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime转指定yyyy-MM-dd HH:mm:ss String
     *
     * @param localDateTime
     * @return
     */
    public static String getStringByLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * String转指定格式LocalDateTime
     *
     * @param str
     * @param format 参数time的格式
     * @return
     */
    public static LocalDateTime getLocalDateTimeByString(String str, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(str, df);
    }

    /**
     * String转指定格式LocalDateTime
     *
     * @param str
     * @return
     */
    public static LocalDateTime getLocalDateTimeByString(String str) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(str, df);
    }

    /**
     * 特殊格式String转LocalDateTime
     *
     * @param str
     * @return
     */
    public static LocalDateTime getLocalDateTimeBySpecialString(String str, String format) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            date = sdf.parse(str);
            return convertDateToLocalDteTime(date);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * long类型时间戳转LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getLocalDateTimeByTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转long类型时间戳
     *
     * @param localDateTime
     * @return
     */
    public static long getTimestampByLocalDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 获取两个日期的差
     *
     * @param startTime
     * @param endTime
     * @param field     单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) {
            return period.getYears();
        }
        if (field == ChronoUnit.MONTHS) {
            return period.getYears() * 12L + period.getMonths();
        }
        return field.between(startTime, endTime);
    }

    /**
     * 获取一天的最小时间
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getDayStart(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取一天的最大时间
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getDayEnd(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取本月的第一天
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate getFirstDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
    }

    /**
     * 获取本月的最后一天
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate getLastDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
    }

    /**
     * 获取今年的第一天
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate getFirstDayOfYear(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.firstDayOfYear()).toLocalDate();
    }

    /**
     * 获取今年的最后一天
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate getLastDayOfYear(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.lastDayOfYear()).toLocalDate();
    }

    /**
     * 获取星期几
     *
     * @param localDateTime
     * @return
     */
    public static String getWeek(LocalDateTime localDateTime) {
        HashMap<String, String> weekHashMap = new HashMap<>(7);
        weekHashMap.put("MONDAY", "星期一");
        weekHashMap.put("TUESDAY", "星期二");
        weekHashMap.put("WEDNESDAY", "星期三");
        weekHashMap.put("THURSDAY", "星期四");
        weekHashMap.put("FRIDAY", "星期五");
        weekHashMap.put("SATURDAY", "星期六");
        weekHashMap.put("SUNDAY", "星期日");
        String week = localDateTime.getDayOfWeek().toString();
        return weekHashMap.get(week);
    }

    /**
     * 一年的开始时间加00:00:00
     *
     * @param year
     * @return
     */
    public static LocalDateTime getYearStartDateTime(String year) {
        if (Objects.isNull(year)) {
            return null;
        }
        String yearStartDateTime = year + START_DAY + START_TIME;
        return getLocalDateTimeByString(yearStartDateTime);
    }

    /**
     * 一年的结束时间加23:59:59
     *
     * @param year
     * @return
     */
    public static LocalDateTime getYearEndDateTime(String year) {
        if (Objects.isNull(year)) {
            return null;
        }
        String yearEndDateTime = year + END_DAY + END_TIME;
        return getLocalDateTimeByString(yearEndDateTime);
    }

    /**
     * 日期转换 0,1,2,3,4,5,6 转 星期天,星期一,星期二,星期三,星期四,星期五,星期六
     *
     * @param str str
     * @return str
     */
    public static String dateConversion(String str) {
        String[] split = str.split(",");
        StringBuilder receiveDate = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            switch (split[i]) {
                case "0":
                    receiveDate.append("星期天");
                    break;
                case "1":
                    receiveDate.append("星期一");
                    break;
                case "2":
                    receiveDate.append("星期二");
                    break;
                case "3":
                    receiveDate.append("星期三");
                    break;
                case "4":
                    receiveDate.append("星期四");
                    break;
                case "5":
                    receiveDate.append("星期五");
                    break;
                case "6":
                    receiveDate.append("星期六");
                    break;
            }
            if (i < split.length - 1) {
                receiveDate.append(",");
            }
        }
        return String.valueOf(receiveDate);
    }

}
