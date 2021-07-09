package com.slodon.b2b2c.core.decoration;

/**
 * 装修NavBar
 */
public class DecoNavBar extends DecoImg {

    private String content;
    private Integer sort;

    public DecoNavBar() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
