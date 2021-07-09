package com.slodon.b2b2c.enums;

import com.slodon.b2b2c.core.constant.UploadConst;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传source枚举类
 */
public enum UploadSourceEnum {
    //seller相关
    SELLER_APPLY("sellerApply", "seller/apply", UploadConst.BUCKET_NAME_IMAGE, "入驻图片"),//入驻图片
    GOODS("goods", "seller/goods", UploadConst.BUCKET_NAME_IMAGE, "商品图片"),//商品图片
    VIDEO("video", "seller/video", UploadConst.BUCKET_NAME_VIDEO, "商品视频"),//商品视频
    SELLER_BRAND("sellerBrand", "seller/brand", UploadConst.BUCKET_NAME_IMAGE, "商家申请品牌"),//商家申请品牌
    SELLER_DECO("sellerDeco", "seller/deco", UploadConst.BUCKET_NAME_IMAGE, "商户装修图片"),//商户装修图片
    APPEAL("appeal", "seller/appeal", UploadConst.BUCKET_NAME_IMAGE, "申诉图片"),//申诉图片
    LOGO("logo", "seller/logo", UploadConst.BUCKET_NAME_IMAGE, "商家logo"),//商家logo

    //会员相关
    COMPLAIN("complain", "member/complain", UploadConst.BUCKET_NAME_IMAGE, "投诉图片"),//投诉图片
    HEAD_IMG("headImg", "member/headImg", UploadConst.BUCKET_NAME_IMAGE, "头像"),//头像
    EVALUATE("evaluate", "member/evaluate", UploadConst.BUCKET_NAME_IMAGE, "评价图片"),//评价图片
    AFTER_SALE("afterSale", "member/after", UploadConst.BUCKET_NAME_IMAGE, "售后申请图片"),//售后申请图片

    //admin相关
    ADMIN_BRAND("adminBrand", "admin/brand", UploadConst.BUCKET_NAME_IMAGE, "品牌图片"),//品牌图片
    SETTING("setting", "admin/setting", UploadConst.BUCKET_NAME_IMAGE, "系统设置图片"),//系统设置图片
    ADMIN_DECO("adminDeco", "admin/deco", UploadConst.BUCKET_NAME_IMAGE, "装修图片"),//装修图片
    FRIEND_PARTNER("friendPartner", "admin/friend", UploadConst.BUCKET_NAME_IMAGE, "合作伙伴图片");//合作伙伴图片

    /**
     * source-enum
     */
    public static Map<String, UploadSourceEnum> sourceMap = new HashMap<>();

    static {
        //构造map
        for (UploadSourceEnum value : UploadSourceEnum.values()) {
            sourceMap.put(value.getSource(), value);
        }
    }

    private String source;//资源名，前端上传图片传参source的值
    private String path;//图片存储中间路径
    private String bucketName;//图片存储桶名称（前缀路径）
    private String des;//描述

    /**
     * 构造方法
     *
     * @param source     资源名，前端上传图片传参source的值
     * @param path       图片存储中间路径
     * @param bucketName 图片存储桶名称（前缀路径）
     */
    UploadSourceEnum(String source, String path, String bucketName, String des) {
        this.source = source;
        this.path = path;
        this.bucketName = bucketName;
        this.des = des;
    }

    public String getSource() {
        return source;
    }

    public String getPath() {
        return path;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getDes() {
        return des;
    }

    /**
     * 上传图片接口文档需要的字段说明
     *
     * @param args
     */
    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        sourceMap.keySet().forEach(s -> {
            stringBuilder.append(";").append(s).append("==").append(sourceMap.get(s).getDes());
        });
        System.out.println(stringBuilder.toString().substring(1));
    }
}
