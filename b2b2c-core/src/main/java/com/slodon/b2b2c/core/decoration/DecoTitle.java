package com.slodon.b2b2c.core.decoration;

import lombok.Data;
import lombok.ToString;

/**
 * 装修页文字标题类
 */
@Data
@ToString
public class DecoTitle {

    private String title;              //主标题
    private String subTitle;           //副标题
    private String titleColor;         //标题颜色
    private String titleBgColor;       //标题背景颜色
    private String linkUrl;            //标题链接

}
