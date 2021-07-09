package com.slodon.b2b2c.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能：日期处理相关的操作
 */
public class TimeUtil {

    /**
     * 取得当天日期,格式 2019-02-11
     *
     * @return
     */
    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 取得当天日期,格式 20190211
     *
     * @return
     */
    public static String getTodayNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 给出日期转换成格式 2019-02-11,如果date为空那么返回null
     *
     * @param date
     * @return
     */
    public static String getZDDay(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 处理返回前端时间格式（yyyy.MM.dd）
     *
     * @param date
     * @return
     */
    public static String dealTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }

    /**
     * 处理返回前端时间格式（MM-dd HH:mm）
     *
     * @param date
     * @return
     */
    public static String dealMonthAndDay(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(date);
    }

    /**
     * 取得当天日期时间,格式 2019-02-11 23:9:21
     *
     * @return
     */
    public static String getTodaytime() {
        Calendar cl = new GregorianCalendar();
        return getToday() + " " + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":"
                + cl.get(Calendar.SECOND) + " ";
    }

    /**
     * 取得当前时间,格式 23:12:20
     *
     * @return
     */
    public static String getTime() {
        Calendar cl = new GregorianCalendar();
        return cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":"
                + cl.get(Calendar.SECOND) + " ";
    }

    /**
     * 取得当前小时
     *
     * @return
     */
    public static int getHour() {
        Calendar cl = new GregorianCalendar();
        return cl.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 取得当前分钟
     *
     * @return
     */
    public static int getMinute() {
        Calendar cl = new GregorianCalendar();
        return cl.get(Calendar.MINUTE);
    }

    /**
     * 取得当前秒
     *
     * @return
     */
    public static int getSecond() {
        Calendar cl = new GregorianCalendar();
        return cl.get(Calendar.SECOND);
    }

    /**
     * 取得当前日期 格式为20190211
     *
     * @return
     */
    public static String getNoFormatToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 取得当前时间 格式为231611
     *
     * @return
     */
    public static String getNoFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 取得当前年份
     *
     * @return
     */
    public static String getYear() {
        return TimeUtil.getNoFormatToday().substring(0, 4);
    }

    /**
     * 取得当前月份
     *
     * @return
     */
    public static String getMonth() {
        return TimeUtil.getNoFormatToday().substring(4, 6);
    }

    /**
     * 取得当前日
     *
     * @return
     */
    public static String getDay() {
        return TimeUtil.getNoFormatToday().substring(6, 8);
    }

    /**
     * 返回N天前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11
     *
     * @param number
     * @return
     */
    public static String getYesterday(int number) {
        String strYesterday = "";
        Calendar cale = null;
        cale = new GregorianCalendar();
        cale.add(Calendar.DATE, number);
        strYesterday = TimeUtil.getStrByCalendar(cale);
        return strYesterday;
    }

    /**
     * 返回N年前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static String getYearAgo(Date date, int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.YEAR, number);
        return format.format(cale.getTime());
    }

    /**
     * 返回N月前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static String getMonthAgo(Date date, int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.MONTH, number);
        return format.format(cale.getTime());
    }

    /**
     * 返回N天前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static String getDayAgo(Date date, int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.DATE, number);
        return format.format(cale.getTime());
    }

    /**
     * 返回N小时前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static String getHourAgo(Date date, int number) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.HOUR, number);
        return format.format(cale.getTime());
    }

    /**
     * 返回N小时前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static Date getHourAgoDate(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.HOUR, number);
        return cale.getTime();
    }

    /**
     * 返回N天前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static Date getDayAgoDate(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.DATE, number);
        return cale.getTime();
    }

    /**
     * 返回N月前（后的）日期，正数是后的日期，负数是前的日期
     *
     * @param number
     * @return
     */
    public static Date getMonthAgoDate(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.MONTH, number);
        return cale.getTime();
    }

    /**
     * 返回N月前（后的）第一天日期，正数是后的日期，负数是前的日期
     *
     * @param number
     * @return
     */
    public static Date getMonthAgoDateStart(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.MONTH, number);
        cale.set(cale.get(Calendar.YEAR), cale.get(Calendar.MONDAY), cale.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMinimum(Calendar.DAY_OF_MONTH));

        return cale.getTime();
    }

    /**
     * 返回N月前（后的）最后一天日期，正数是后的日期，负数是前的日期
     *
     * @param number
     * @return
     */
    public static Date getMonthAgoDateEnd(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.MONTH, number);
        cale.set(cale.get(Calendar.YEAR), cale.get(Calendar.MONDAY), cale.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));
        cale.set(Calendar.HOUR_OF_DAY, 24);
        cale.add(Calendar.SECOND, -1);
        return cale.getTime();
    }

    public static void main(String[] args) {
        Date date = getMonthAgoDateEnd(new Date(), 1);
        System.out.println(date);
    }

    /**
     * 返回N年前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static Date getYearAgoDate(Date date, int number) {
        Calendar cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.YEAR, number);
        return cale.getTime();
    }

    /**
     * 返回N天前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param number
     * @return
     */
    public static Date getDateApartDay(int number) {
        Calendar cale;
        cale = new GregorianCalendar();
        cale.add(Calendar.DATE, number);
        return cale.getTime();
    }

    /**
     * 返回N天前（后的）日期，正数是后的日期，负数是前的日期。例如：2009-02-11 12:12:12
     *
     * @param date
     * @param number
     * @return
     */
    public static Date getDateApartDay(Date date, int number) {
        Calendar cale;
        cale = new GregorianCalendar();
        cale.setTime(date);
        cale.add(Calendar.DATE, number);
        return cale.getTime();
    }

    public static boolean dateFlag(int number, String nowdate) {
        boolean flag = false;
        try {
            String d = getYesterday(-number);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date yesterday = sdf.parse(d);
            Date nd = sdf.parse(nowdate);
            flag = yesterday.before(nd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 返回yyyy-MM-dd
     *
     * @param cale
     * @return
     */
    public static String getStrByCalendar(Calendar cale) {
        return (new SimpleDateFormat("yyyy-MM-dd")).format(cale.getTime());
    }

    /**
     * 日期字符串的格式转换,例如"2019-02-11"转换为2009年2月11日
     *
     * @param sDate
     * @return
     */
    public static String getChnDateString(String sDate) {
        if (sDate == null) {
            return null;
        }
        sDate = sDate.trim();
        if (sDate.length() == 7) {
            sDate += "-01";
        }
        StringTokenizer st = new StringTokenizer(sDate, "-");
        int year = 2100;
        int month = 0;
        int day = 1;
        try {
            year = Integer.parseInt(st.nextToken());
            month = Integer.parseInt(st.nextToken()) - 1;
            day = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cl = new GregorianCalendar(year, month, day);
        return cl.get(Calendar.YEAR) + "年" + (cl.get(Calendar.MONTH) + 1) + "月"
                + cl.get(Calendar.DATE) + "日";
    }

    /**
     * 取得某年某月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getMaxDayOfMonth(int year, int month) {
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(cal.getTime());
    }

    /**
     * 取得某年某月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getMinDayOfMonth(int year, int month) {
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    /**
     * 取得中文星期 例如 星期二
     * @param date
     * @return
     */
    public static String getChineseDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINESE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return sdf.format(cal.getTime());
    }

    /**
     * 取得当天的中文日期，像2019年11月28日 星期四
     *
     * @return
     */
    public static String getChineseToDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINESE);
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 取得当天的中文日期，像2019年11月28日 星期四 下午05:06
     *
     * @return
     */
    public static String getChineseToDayTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E a", Locale.CHINESE);
        Calendar cl = new GregorianCalendar();
        return sdf.format(cl.getTime());
    }

    /**
     * 根据字符串，取得日期类
     *
     * @param sDate
     * @return
     */
    public static Calendar getDate(String sDate) {
        if (sDate == null) {
            return null;
        }
        sDate = sDate.trim();
        if (sDate.length() == 7) {
            sDate += "-01";
        }
        StringTokenizer st = new StringTokenizer(sDate, "-");
        int year = 2100;
        int month = 0;
        int day = 1;
        try {
            year = Integer.parseInt(st.nextToken());
            month = Integer.parseInt(st.nextToken()) - 1;
            day = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GregorianCalendar(year, month, day);
    }

    /**
     * 根据日期类取得日期的字符串形式
     *
     * @param sDate
     * @return
     */
    public static String getDateString(Calendar sDate) {
        if (sDate == null) {
            return "";
        }
        return (new SimpleDateFormat("yyyy-MM-dd")).format(sDate.getTime());
    }

    /**
     * 根据日期类取得日期的字符串形式
     *
     * @param sDate
     * @return
     */
    public static String getDateString(Date sDate) {
        if (sDate == null) {
            return "";
        }
        return (new SimpleDateFormat("yyyy-MM-dd")).format(sDate.getTime());
    }

    /**
     * 根据日期类取得日期的字符串形式(yyyy-MM-dd HH:mm:ss)
     *
     * @param sDate
     * @return
     */
    public static String getDateTimeString(Date sDate) {
        if (sDate == null) {
            return "";
        }
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(sDate.getTime());
    }

    /**
     * 根据日期类取年月的字符串形式
     *
     * @param sDate
     * @return
     */
    public static String getYearMonth(Calendar sDate) {
        if (sDate == null) {
            return "";
        }
        return (new SimpleDateFormat("yyyy-MM")).format(sDate.getTime());
    }

    /**
     * 比较两个日期类型的字符串，格式为（yyyy-mm-dd）
     * 如果cale1大于cale2，返回1
     * 如果cale1小于cale2，返回-1
     * 如果相等，返回0
     * 如果格式错误，返回-2
     *
     * @param cale1
     * @param cale2
     * @return
     */
    public static int compareCalendar(String cale1, String cale2) {
        Calendar calendar1 = getDate(cale1);
        Calendar calendar2 = getDate(cale2);
        if (calendar1 == null || calendar2 == null) {
            return -2;
        }
        return calendar1.compareTo(calendar2);
    }

    /**
     * 获取当前日期   格式 yyyy-MM-01 00:00:01
     *
     * @return
     */
    public static String getYearMonth() {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(new Date()) + "-01 00:00:00";
    }

    /**
     * 获取当前时间 格式:YYYYMMDDhhmmss
     *
     * @return
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 字符串转换成日期
     *
     * @param str 必须形如：yyyy-MM-dd HH:mm:ss
     * @return date
     */
    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转换成日期
     *
     * @param str 必须形如：yyyy-MM-dd
     * @return date
     */
    public static Date strToDateYMD(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 返回N天前（后的）星期数，正数是后的星期数，负数是前的星期数。例如：1
     *
     * @param number
     * @return date
     */
    public static Integer getYesterdayOfWeek(int number) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, number);

        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 计算date距离当前时间的天数
     *
     * @param date 要计算的日期
     * @return 天数
     */
    public static Integer countDate(Date date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date = format.parse(format.format(date));
        Date now = format.parse(format.format(new Date()));
        return (int) ((date.getTime() - now.getTime()) / (1000 * 24 * 60 * 60));
    }

    /**
     * 计算两个日期相隔的天数
     *
     * @param from 要计算的日期
     * @param to   要计算的日期
     * @return 天数
     */
    public static Integer countApartDay(Date from, Date to) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        from = format.parse(format.format(from));
        to = format.parse(format.format(to));
        return (int) (Math.abs(from.getTime() - to.getTime()) / (1000 * 24 * 60 * 60));
    }

    /**
     * 计算两个日期范围内的总天数，比如 2019-01-01 到 2019-01-02 之间，总共有2天
     *
     * @param from 要计算的日期
     * @param to   要计算的日期
     * @return 天数
     */
    public static Integer countTotalDay(String from, String to) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return 1 + (int) (Math.abs(format.parse(from).getTime() - format.parse(to).getTime()) / (1000 * 24 * 60 * 60));
    }

    /**
     * 计算n年之后的日期
     *
     * @param date
     * @param number
     * @return
     */
    public static Date countYear(Date date, int number) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, number);
        return calendar.getTime();
    }

    /**
     * 计算n分之后的日期
     *
     * @param date
     * @param number
     * @return
     */
    public static Date countMinute(Date date, int number) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, number);
        return calendar.getTime();
    }

    /**
     * 将一个时间转换为 yyyy-MM-dd 的时间格式
     *
     * @param date
     * @return
     */
    public static Date changeFormatYMD(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date result = null;
        try {
            result = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将一个时间转换为 yyyy-MM-dd 23:59:59 的时间格式
     *
     * @param date
     * @return
     */
    public static Date changeFormatYMDHMS(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 某天开始时间yyyy-MM-dd 00:00:00 的时间格式
     *
     * @param date
     * @return
     */
    public static Date getDayStartFormatYMDHMS(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        return calendar.getTime();
    }

    /**
     * 某天结束时间yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getDayEndFormatYMDHMS(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取距离当前时间n天的时间，n为负数代表当前时间之前,格式为yyyy-MM-dd
     * @param num
     * @return
     */
    public static Date getDateByApart(int num) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, num);
        return changeFormatYMD(calendar.getTime());
    }

    /**
     * 获取距离当前时间n天的时间，n为负数代表当前时间之前,格式为yyyy-MM-dd
     */
    public static String getDateStringByApart(int num) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, num);
        return getZDDay(calendar.getTime());
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param startTime 时间参数 1 格式：1990-01-01 12:00:00
     * @param endTime   时间参数 2 格式：2019-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(Date startTime, Date endTime) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = startTime.getTime();
        long time2 = endTime.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }

    /**
     * 两个时间相差多少小时
     *
     * @param startTime 时间参数 1 格式：2018-01-01 12:00:00
     * @param endTime   时间参数 2 格式：2019-01-01 12:00:00
     * @return long 返回值为：38
     */
    public static long getHourTime(Date startTime, Date endTime) {
        long day = 0;
        long hour = 0;
        long time1 = startTime.getTime();
        long time2 = endTime.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        return day * 24 + hour;
    }


    /**
     * 获取某一时间的年份数
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取某一时间的月份数
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取某一时间的日期数
     *
     * @param date
     * @return
     */
    public static int getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 计算播放时长
     *
     * @param startTime 时间参数 1 格式：1990-01-01 12:00:00
     * @param endTime   时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：小时:分:秒
     */
    public static String getPlaybackTime(Date startTime, Date endTime) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = startTime.getTime();
        long time2 = endTime.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return hour + ":" + min + ":" + sec;
    }

    /**
     * 获取两个日期之间的所有日期
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     * 获取两个日期之间的所有月份
     *
     * @param minDate
     * @param maxDate
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        try {
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    /**
     * 计算出近n天的时间 格式为yyyy-MM-dd
     *
     * @param past
     * @return
     */
    public static List<String> getPastDate(Integer past) {
        if (past == null) {
            throw new RuntimeException("天数不能为空");
        }
        past -= 1;
        if (past < 0) {
            past = 0;
        }
        List<String> list = new ArrayList<>();
        while (past >= 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String result = sdf.format(today);
            list.add(result);
            past--;
        }
        return list;
    }

    /**
     * 获得本周一0点时间
     *
     * @return
     */
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获得本周日23:59:59
     *
     * @return
     */
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.SECOND, -1);
        return cal.getTime();
    }

    /**
     * 获得本月第一天0点时间
     *
     * @return
     */
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天23:59:59
     *
     * @return
     */
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.add(Calendar.SECOND, -1);
        return cal.getTime();
    }

    /**
     * 获取今日开始时间
     *
     * @return
     */
    public static Date getTodayTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

}
