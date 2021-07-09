package com.slodon.b2b2c.core.i18n;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 将翻译properties文件转化为map
 *
 * @author
 * @date
 */
public class Language {
    public static final String DEFAULT_LANGUAGE_TYPE = "zh";//默认语言
    //存放翻译文件的map
    public static Map<String/*languageType*/,List<Map<String/*key*/,String/*译文*/>>/*翻译文件列表，size=2,第一个为正常key，第二个为正则动态key*/> map = new HashMap<>();

    static {
        //将翻译文件初始化到map中，map中的每个元素为一种语言

        //eg. 英文
        Map<String/*key*/,String/*译文*/> en_map1 = new HashMap<>();//正常key
        Map<String/*key*/,String/*译文*/> en_map2 = new HashMap<>();//正则动态key
        List<Map<String/*key*/,String/*译文*/>> en_list = new ArrayList<>();//翻译文件列表
        en_list.add(en_map1);
        en_list.add(en_map2);
        map.put("en",en_list);
        //正常key的翻译文件初始化
        ResourceBundle rb1 = ResourceBundle.getBundle("i18n_en");
        Set<String> keySet = rb1.keySet();
        for (String key : keySet) {
            try {
                String s = new String(key.getBytes("ISO8859_1"), StandardCharsets.UTF_8);
                en_map1.put(s, rb1.getString(key));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //动态key的翻译文件初始化
        ResourceBundle rb2 = ResourceBundle.getBundle("i18n_en_extend");
        for (String key : rb2.keySet()) {
            try {
                en_map2.put(new String(key.getBytes("ISO8859_1"), StandardCharsets.UTF_8), rb2.getString(key));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //todo 新增语言时，按照英文初始化方式，放入map中，语言对应的类型，参考 https://www.runoob.com/tags/html-language-codes.html

    }

    /**
     * 获取译文
     *
     * @param msg 原文
     * @param languageType 语言类型
     * @return 译文
     */
    public static String translate(String msg,String languageType){
        if (StringUtils.isEmpty(languageType) || DEFAULT_LANGUAGE_TYPE.equals(languageType)){
            //默认语言，不翻译
            return msg;
        }

        //根据语言类型获取译文列表
        List<Map<String, String>> languageList = map.get(languageType);
        if (CollectionUtils.isEmpty(languageList)){
            //翻译语言不存在，使用默认语言
            return msg;
        }

        Map<String/*key*/,String/*译文*/> en_map1 = languageList.get(0);
        Map<String/*key*/,String/*译文*/> en_map2 = languageList.get(1);

        if (en_map1.containsKey(msg)){
            //常规key
            return en_map1.get(msg);
        }
        for (String s : en_map2.keySet()) {
            if (msg.matches(s)) {
                //正则匹配成功，获取动态值
                String dynamicValue = msg.replaceAll(s, "$1");
                //翻译语句替换动态值
                return en_map2.get(s).replace("${}",dynamicValue);
            }
        }
        //无匹配，直接返回原值
        return msg;
    }

    /**
     * 获取译文
     *
     * @param msg 原文
     * @return 译文
     */
    public static String translate(String msg){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //从请求头中获取语言类型
        String languageType = request.getHeader("Language");
        return translate(msg,languageType);
    }
}
