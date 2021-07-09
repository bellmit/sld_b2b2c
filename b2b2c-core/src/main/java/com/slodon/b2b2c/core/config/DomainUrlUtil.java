/*
 * copyright
 * slodon
 * version-v2.3
 * date-2021-03-29
 */
package com.slodon.b2b2c.core.config;

import lombok.extern.slf4j.Slf4j;

/**
 * DomainUrlUtil
 */
@Slf4j
public class DomainUrlUtil {

    public static final String SLD_API_URL = "http://admin.sohomall.jp";                //主站的URL
    public static final String SLD_STATIC_RESOURCES = "http://admin.sohomall.jp";       //静态资源的URL

    public static final String SLD_H5_URL = "http://m.sohomall.jp ";                     //移动端前端地址
    public static final String SLD_PC_URL = "http://www.sohomall.jp/";                   //pc前端地址

    public static final String SLD_ADMIN_URL = "http://admin.sohomall.jp";              //admin前端地址
    public static final String SLD_SELLER_URL = "http://seller.sohomall.jp";            //seller前端地址

    public static final String SLD_IMAGE_RESOURCES = "http://image.sohomall.jp";                            //图片资源的URL
    public static final String QIUNIUYUN_IMAGE_RESOURCES = "http://javaiamg.slodon.cn";                          //七牛云图片资源的URL
    public static final String TENCENTCLOUD_IMAGE_RESOURCES = "https://jbbcimgadev.slodon.cn";                   //腾讯云图片资源的URL
    public static final String ALIYUN_IMAGE_RESOURCES = "http://bucket333a33333.oss-cn-beijing.aliyuncs.com";    //阿里云图片资源的URL

    public static final String SLD_ES_URL = "sld-es";                       //Elastic Search URL
    public static final Integer SLD_ES_PORT = 9200;                         //Elastic Search PORT
    public static final String ES_INDEX_NAME = "slodon-b2b2c";              //es索引名称
    public static final String INTEGRAL_ES_INDEX_NAME = "slodon_integral";  //积分商城es索引名称

    public static final String SLD_MQ_HOST = "sld-mq";                      //rabbitMq host
    public static final Integer SLD_MQ_PORT = 5672;                         //rabbitMq 端口号
    public static final String SLD_MQ_NAME_PREFIX = "slodon-b2b2c";         //rabbitMq 名称前缀（交换机、队列名称）

    public static final String SLD_REDIS_HOST = "sld-redis";                //redis host
    public static final Integer SLD_REDIS_PORT = 6379;                      //redis 端口
    public static final String SLD_REDIS_PASSWORD = "111111";               //redis 密码

    public static final Integer UPLOAD_IMAGE_TYPE = 1;                      //上传图片类型，1-公共上传，2-七牛云上传，3-腾讯云上传，4-阿里云上传

}
