package com.slodon.b2b2c.core.constant;

/**
 * 店铺相关常量
 */
public class StoreConst {

    /**
     * 店铺类型：1-自营店铺，2-入驻店铺
     */
    public static final int IS_OWN_STORE = 1;
    public static final int NO_OWN_STORE = 2;

    /**
     * 店铺状态： 1、开启；2、关闭；3、伪删除
     */
    public static final int STORE_STATE_OPEN = 1;
    public static final int STORE_STATE_CLOSE = 2;
    public static final int STORE_STATE_DELETE = 3;

    /**
     * 店铺入驻信查重字段类型:1-公司名称；2-店铺名称
     */
    public static final int APPLY_VALID_TYPE_COMPANY_NAME = 1;
    public static final int APPLY_VALID_TYPE_STORE_NAME = 2;

    /**
     * 入驻类型：0-个人入驻，1-企业入驻
     */
    public static final int APPLY_TYPE_PERSON = 0;
    public static final int APPLY_TYPE_COMPANY = 1;

    /**
     * 店铺入驻申请状态：1:店铺信息提交申请，2：店铺信息审核通过，3：店铺信息审核失败，4：开通店铺(支付完成)
     */
    public static final int STATE_1_SEND_APPLY = 1;
    public static final int STATE_2_DONE_APPLY = 2;
    public static final int STATE_3_FAIL_APPLY = 3;
    public static final int STATE_4_STORE_OPEN = 4;

    /**
     * 店铺绑定商品分类申请状态：1-提交审核;2-审核通过;3-审核失败;
     */
    public static final int STORE_CATEGORY_STATE_SEND = 1;
    public static final int STORE_CATEGORY_STATE_PASS = 2;
    public static final int STORE_CATEGORY_STATE_FALSE = 3;

    /**
     * 结算方式：1-按月结算，2-按周结算
     */
    public static final int BILL_TYPE_MONTH = 1;
    public static final int BILL_TYPE_WEEK = 2;

    /**
     * 店铺续签状态：1：待付款；2续签成功
     */
    public static final int STORE_RENEW_STATE_WAITPAY = 1;
    public static final int STORE_RENEW_STATE_SUCCESS = 2;

    /**
     * member_follow_store  会员收藏商铺表
     * 是否置顶：0、不置顶；1、置顶
     */
    public static final int IS_TOP_0 = 0;
    public static final int IS_TOP_1 = 1;

    /**
     * 店铺地址类型：1-发货地址；2-收货地址
     */
    public static final int ADDRESS_TYPE_DELIVER = 1;
    public static final int ADDRESS_TYPE_RECEIVE = 2;

    /**
     * 收货地址是否默认：1-是;0-否
     */
    public static final int ADDRESS_IS_DEFAULT = 1;
    public static final int ADDRESS_NOT_DEFAULT = 0;


    /**
     * 店铺绑定快递公司状态：1-开启；0-关闭
     */
    public static final String EXPRESS_STATE_OPEN = "1";
    public static final String EXPRESS_STATE_CLOSE = "0";

    /**
     * 0-不推荐，1-推荐
     */
    public static final String IS_RECOMMEND_1 = "1";
    public static final String IS_RECOMMEND_0 = "0";
}
