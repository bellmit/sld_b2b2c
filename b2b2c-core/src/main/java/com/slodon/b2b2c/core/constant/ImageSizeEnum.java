package com.slodon.b2b2c.core.constant;

/**
 * 图片尺寸,一般只有商品图片需要设置
 */
public enum  ImageSizeEnum {
    _520x520("520","520"),
    _300x300("300","300"),
    _100x100("100","100"),
    LARGE("",""),//大图，原图
    MIDDLE("520","520"),//中图
    SMALL("100","100"),//小图
    CUSTOM("","");//自定义尺寸图片

    private String height; //图片高度
    private String width; //图片宽度

    ImageSizeEnum(String height, String width) {
        this.height = height;
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * 自定义图片尺寸
     * @param height 高
     * @param width 宽
     * @return
     */
    public static ImageSizeEnum of(String height,String width){
        ImageSizeEnum imageSizeEnum = ImageSizeEnum.valueOf("CUSTOM");
        imageSizeEnum.setHeight(height);
        imageSizeEnum.setWidth(width);
        return imageSizeEnum;
    }
}
