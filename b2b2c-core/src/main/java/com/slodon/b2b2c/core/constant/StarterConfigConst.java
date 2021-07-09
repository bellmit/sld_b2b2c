package com.slodon.b2b2c.core.constant;

import com.slodon.b2b2c.core.config.DomainUrlUtil;

public class StarterConfigConst {
    /**
     * 数据源配置--前缀
     */
    public final static String DATASOURCE_PREFIX_WRITE = "slodon.b2b2c.datasource.write";
    public final static String DATASOURCE_PREFIX_READ = "slodon.b2b2c.datasource.read";

    /**
     * 数据源配置--mapper接口扫描路径
     */
    public final static String DATASOURCE_MAPPER_SCAN_BASE_PACKAGE_WRITE = "com.slodon.b2b2c.dao.write.*";
    public final static String DATASOURCE_MAPPER_SCAN_BASE_PACKAGE_READ = "com.slodon.b2b2c.dao.read.*";

    /**
     * 数据源配置--xml文件路径
     */
    public final static String DATASOURCE_MAPPER_XML_LOCATIONS_WRITE = "classpath:mapper/write/**/*.xml";
    public final static String DATASOURCE_MAPPER_XML_LOCATIONS_READ = "classpath:mapper/read/**/*.xml";

    /**
     * 数据源配置--实体类路径
     */
    public final static String DATASOURCE_TYPE_ALIASES_PACKAGE_WRITE = "com.slodon.b2b2c.*.pojo";
    public final static String DATASOURCE_TYPE_ALIASES_PACKAGE_READ = "com.slodon.b2b2c.*.pojo";

    /**
     * redis配置前缀
     */
    public final static String REDIS_PREFIX = "slodon.b2b2c.redis";

    /**
     * redisson配置前缀
     */
    public final static String REDISSON_PREFIX = "slodon.b2b2c.redisson";

    /**
     * mq配置前缀
     */
    public final static String MQ_PREFIX = "slodon.b2b2c.mq";

    /**
     * mq交换机名称配置
     */
    public final static String MQ_EXCHANGE_NAME = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_exchange";

    /**
     * mq队列名称配置（交换机到队列的路由名称与队列名称相同）
     */
    public final static String MQ_QUEUE_NAME_MEMBER_MSG = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_member_msg";//会员消息
    public final static String MQ_QUEUE_NAME_SELLER_MSG = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_seller_msg";//店铺消息
    public final static String MQ_QUEUE_NAME_ADMINLOG_MSG = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_adminLog_msg";//admin日志消息
    public final static String MQ_QUEUE_NAME_SELLERLOG_MSG = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_sellerLog_msg";//vendor日志消息
    public final static String MQ_QUEUE_NAME_ORDER_SUBMIT = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_order_submit1";//提交订单队列
    public final static String MQ_QUEUE_NAME_MEMBER_INTEGRAL = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_queue_member_integral";//提交会员队列

    /**
     * 提交订单入mq队列标识前缀，标识存入redis中，key=前缀+支付单号
     */
    public final static String ORDER_SUBMIT_MQ_REDIS_PREFIX = "order_submit_";

    /**
     * mq连接工厂名称
     */
    public final static String MQ_FACTORY_NAME_SINGLE_PASS_ERR = DomainUrlUtil.SLD_MQ_NAME_PREFIX + "_factory_single_pass_err";//连接工厂，单一消费者，发生异常丢弃消息
}
