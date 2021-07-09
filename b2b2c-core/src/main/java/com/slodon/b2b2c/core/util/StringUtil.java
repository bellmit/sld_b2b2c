package com.slodon.b2b2c.core.util;

import com.slodon.b2b2c.core.exception.SLDException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具函数
 */
public final class StringUtil {

    /**
     * 检查字符串是否为空
     * 为null或者长度为0视为空字符串
     *
     * @param value     要检查的字符串
     * @param trim      是否去掉头尾的特定字符
     * @param trimChars 要去掉的特定字符
     * @return
     */
    public static boolean isEmpty(String value, boolean trim, char... trimChars) {
        if (trim)
            return value == null || trim(value, trimChars).length() <= 0;
        return value == null || value.length() <= 0;
    }

    /**
     * 检查字符串是否为空
     * 为null或者长度为0视为空字符串
     * <p>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     *
     * @param value 要检查的字符串
     * @param trim  是否去掉头尾的空格
     * @return
     */
    public static boolean isEmpty(String value, boolean trim) {
        return isEmpty(value, trim, ' ');
    }

    /**
     * 检查字符串是否为空
     * <p>为null或者长度为0视为空字符串
     *
     * @param value 要检查的字符串
     * @return
     */
    public static boolean isEmpty(String value) {
        return isEmpty(value, false);
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     * <p>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     *
     * @param str 要检查的字符串
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果为null，转换为""
     *
     * @param value
     * @return
     */
    public static String nullSafeString(String value) {
        if ("null".equals(value)) {
            return "";
        }
        return value == null ? "" : value;
    }

    /**
     * 确保存入数据库的string值不会引起数据库报错。
     * <p>
     * 1. 数据库不允许为null，value为null时返回""；
     * 2. 超过最大长度时截断字符串。
     *
     * @param value     要存入数据库的字符串值。
     * @param nullable  是否允许为null。
     * @param maxLength 最大长度。
     * @return
     */
    public static String dbSafeString(String value, boolean nullable, int maxLength) {
        if (value == null) {
            if (nullable) {
                return null;
            }
            return nullSafeString(value);
        }
        if (value.length() > maxLength)
            return value.substring(0, maxLength);
        return value;
    }

    /**
     * 去掉头尾空格字符
     *
     * @param value 待处理的字符串
     * @return
     */
    public static String trim(String value) {
        return trim(3, value, ' ');
    }

    /**
     * 去除字符串头尾的特定字符
     *
     * @param value 待处理的字符串
     * @param chars 需要去掉的特定字符
     * @return
     */
    public static String trim(String value, char... chars) {
        return trim(3, value, chars);
    }

    /**
     * 去除字符串头部的特定字符
     *
     * @param value 待处理的字符串
     * @param chars 需要去掉的特定字符
     * @return
     */
    public static String trimStart(String value, char... chars) {
        return trim(1, value, chars);
    }

    /**
     * 去除字符串尾部的特定字符
     *
     * @param value 待处理的字符串
     * @param chars 需要去掉的特定字符
     * @return
     */
    public static String trimEnd(String value, char... chars) {
        return trim(2, value, chars);
    }

    /**
     * 去掉字符串头尾特定字符
     *
     * @param mode  1: 去掉头部特定字符；
     *              2: 去掉尾部特定字符；
     *              3: 去掉头尾特定字符；
     * @param value 待处理的字符串
     * @param chars 需要去掉的特定字符
     * @return
     */
    private static String trim(int mode, String value, char... chars) {
        if (value == null || value.length() <= 0)
            return value;

        int startIndex = 0, endIndex = value.length(), index = 0;
        if (mode == 1 || mode == 3) {
            // trim头部
            while (index < endIndex) {
                if (contains(chars, value.charAt(index++))) {
                    startIndex++;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex)
            return "";

        if (mode == 2 || mode == 3) {
            // trim尾部
            index = endIndex - 1;
            while (index >= 0) {
                if (contains(chars, value.charAt(index--))) {
                    endIndex--;
                    continue;
                }
                break;
            }
        }

        if (startIndex >= endIndex)
            return "";
        if (startIndex == 0 && endIndex == value.length() - 1)
            return value;

        return value.substring(startIndex, endIndex);
    }

    private static boolean contains(char[] chars, char chr) {
        if (chars == null || chars.length <= 0)
            return false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == chr)
                return true;
        }
        return false;
    }

    /**
     * 检查整数是否为空
     *
     * @param val 要检查的字符串
     * @return
     */
    public static boolean isNullOrZero(Integer val) {
        if (val == null || val == 0) {
            return true;
        }
        return false;

    }

    /**
     * 检查整数是否为空
     *
     * @param val 要检查的字符串
     * @return
     */
    public static boolean isNullOrZero(Long val) {
        return val == null || val == 0;

    }

    /**
     * 检查是BigDecimal否为空
     *
     * @param val 要检查的字符串
     * @return
     */
    public static boolean isNullOrZero(BigDecimal val) {
        return val == null || val.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 以特定分隔符分开的重组Integer集合
     *
     * @param string
     * @param sep
     * @return
     */
    public static List<Integer> string2IntegerList(String string, String sep) {
        List<Integer> list;
        try {
            String[] arr = string.split(sep);
            list = new ArrayList<>(arr.length);
            for (String str : arr) {
                Integer i = Integer.valueOf(str);
                list.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw SLDException.PARAMETER_ERROR;
        }
        return list;
    }

    public static List<Integer> string2IntegerList(String string) {
        return string2IntegerList(string, ",");
    }

    /**
     * 以特定分隔符分开的重组String集合
     *
     * @param string
     * @return
     */
    public static List<String> stringList(String string) {
        List<String> list;
        try {
            String[] arr = string.split(",");
            list = new ArrayList<>(arr.length);
            list.addAll(Arrays.asList(arr));
        } catch (Exception e) {
            e.printStackTrace();
            throw SLDException.PARAMETER_ERROR;
        }
        return list;
    }

    /**
     * 过滤特殊字符，如果发生异常返回没有过滤之前的字符串
     *
     * @param str
     * @return
     */
    public static String stringFilterSpecial(String str) {
        // 只允许字母和数字     
        // String   regEx  =  "[^a-zA-Z0-9]";                   
        try {
            // 清除掉所有特殊字符
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 判断是否是字母是返回true，不是返回false
     *
     * @param alpha
     * @return
     */
    public static boolean is_alpha(String alpha) {
        if (alpha == null)
            return false;
        return alpha.matches("[a-zA-Z]+");
    }

    /**
     * 将汉字转成拼音
     * <p>
     * 取首字母或全拼
     *
     * @param hanzi  汉字字符串
     * @param isFull 是否全拼 true:表示全拼 false表示：首字母
     * @return 拼音
     */
    public static String convertHanzi2Pinyin(String hanzi, boolean isFull) {
        /***
         * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
         * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
         * ^[\u4E00-\u9FA5]+$ 匹配简体
         */
        String regExp = "^[\u4E00-\u9FFF]+$";
        StringBuffer sb = new StringBuffer();
        if (hanzi == null || "".equals(hanzi.trim())) {
            return "";
        }
        String pinyin = "";
        for (int i = 0; i < hanzi.length(); i++) {
            char unit = hanzi.charAt(i);
            //是汉字，则转拼音
            if (match(String.valueOf(unit), regExp)) {
                pinyin = convertSingleHanzi2Pinyin(unit);
                if (isFull) {
                    sb.append(pinyin);
                } else {
                    sb.append(pinyin.charAt(0));
                }
            } else {
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    /**
     * 将单个汉字转成拼音
     *
     * @param hanzi 汉字字符
     * @return 拼音
     */
    private static String convertSingleHanzi2Pinyin(char hanzi) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] res;
        StringBuffer sb = new StringBuffer();
        try {
            res = PinyinHelper.toHanyuPinyinStringArray(hanzi, outputFormat);
            sb.append(res[0]);//对于多音字，只用第一个拼音
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    /***
     * 匹配
     * <P>
     * 根据字符和正则表达式进行匹配
     *
     * @param str 源字符串
     * @param regex 正则表达式
     *
     * @return true：匹配成功  false：匹配失败
     */
    private static boolean match(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /***
     *获取汉字拼音或者英文的首字母
     * @param str 源字符串
     **/
    public static String GetFirstChar(String str) {
        String temp = convertHanzi2Pinyin(str, true).toUpperCase();
        return temp.substring(0, 1);
    }
}
