package com.slodon.b2b2c.core.util;

import com.slodon.b2b2c.core.exception.MallException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 断言工具
 */
public class AssertUtil {

    /**
     * 条件断言
     *
     * @param condition
     * @param msg
     */
    public static void isTrue(boolean condition, String msg) {
        if (condition) {
            throw new MallException(msg);
        }
    }

    /**
     * 非空断言
     *
     * @param o
     * @param msg
     */
    public static void notNull(Object o, String msg) {
        if (o == null) {
            throw new MallException(msg);
        }
    }

    /**
     * 非空非0断言
     *
     * @param n
     * @param msg
     */
    public static void notNullOrZero(Integer n, String msg) {
        if (n == null || n == 0) {
            throw new MallException(msg);
        }
    }

    /**
     * 非空非0断言
     *
     * @param n
     * @param msg
     */
    public static void notNullOrZero(BigDecimal n, String msg) {
        if (n == null || n.compareTo(BigDecimal.ZERO) == 0) {
            throw new MallException(msg);
        }
    }

    /**
     * 非空非0断言
     *
     * @param n
     * @param msg
     */
    public static void notNullOrZero(Long n, String msg) {
        if (n == null || n == 0L) {
            throw new MallException(msg);
        }
    }

    /**
     * 集合非空断言
     *
     * @param collection
     * @param msg
     */
    public static void notEmpty(Collection collection, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MallException(msg);
        }
    }

    /**
     * 字符串非空断言
     *
     * @param str
     * @param msg
     */
    public static void notEmpty(String str, String msg) {
        if (StringUtils.isEmpty(str)) {
            throw new MallException(msg);
        }
    }

    /**
     * 逗号分割的id字符串 每项都是整数断言,
     *
     * @param Ids 逗号分割的Id
     * @param msg
     * @return
     */
    public static void notFormatIds(String Ids, String msg) {
        if (StringUtils.isEmpty(Ids)) {
            throw new MallException(msg);
        }
        String POSITIVE_INT_NUM = "^[1-9]\\d*$";//正整数
        for (String Id : Ids.split(",")) {
            if (!Id.matches(POSITIVE_INT_NUM)) {
                throw new MallException(msg);
            }
        }
    }

    /**
     * 前端传参,防sql注入
     * 逗号分割的id字符串 只有数字和逗号断言,
     *
     * @param Ids 逗号分割的Id
     * @param msg
     * @return
     */
    public static void notFormatFrontIds(String Ids, String msg) {
        //只有数字和逗号
        String PATTERN = "^[,0-9]+$";
        if (!Ids.matches(PATTERN)) {
            throw new MallException(msg);
        }
    }

    /**
     * 排序断言
     *
     * @param sort
     */
    public static void sortCheck(Integer sort) {
        if (sort == null) {
            throw new MallException("排序不能为空,请重试!");
        }
        if (sort < 0 || sort > 255) {
            throw new MallException("排序值需在0～255内，请重试！");
        }
    }

    /**
     * 邮箱断言
     *
     * @param email
     * @return
     */
    public static void emailCheck(String email) {
        String check = "^([a-zA-Z0-9]+[-_.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[-_.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        if (!matcher.matches()) {
            throw new MallException("请输入正确的邮箱");
        }
    }

    /**
     * 手机号断言
     *
     * @param mobile
     * @return
     */
    public static void mobileCheck(String mobile) {
        String check = "^1[3-9]\\d{9}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(mobile);
        if (!matcher.matches()) {
            throw new MallException("请输入正确的手机号");
        }
    }
}
