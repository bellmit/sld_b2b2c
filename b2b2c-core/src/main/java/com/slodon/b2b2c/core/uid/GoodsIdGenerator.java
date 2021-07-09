package com.slodon.b2b2c.core.uid;

import com.slodon.b2b2c.core.constant.BizTypeConst;
import com.slodon.smartid.client.utils.SmartId;

public class GoodsIdGenerator {

    /**
     * 位数，10^11，共12位
     */
    private final static long DIGIT = (long) 1e11;

    /**
     * 生成商品id-12位
     *
     * @return
     */
    public static long goodsIdGenerator() {
        return DIGIT + SmartId.nextId(BizTypeConst.GOODS_COMMON);
    }

    /**
     * 生成商品扩展id-12位
     *
     * @return
     */
    public static long goodsExtendIdGenerator() {
        return 6 * DIGIT + SmartId.nextId(BizTypeConst.GOODS_EXTEND);
    }

    /**
     * 生成货品code-12位
     *
     * @return
     */
    public static long productCodeGenerator() {
        return 2 * DIGIT + SmartId.nextId(BizTypeConst.PRODUCT_CODE);
    }

    /**
     * 生成货品id-12位
     *
     * @return
     */
    public static long productIdGenerator() {
        return 2 * DIGIT + SmartId.nextId(BizTypeConst.PRODUCT);
    }

    /**
     * 生成订单号-12位
     */
    public static String getOrderSn() {
        return 3 * DIGIT + SmartId.nextId(BizTypeConst.ORDERS) + "";
    }

    /**
     * 生成付款单号-12位
     */
    public static String getPaySn() {
        return 4 * DIGIT + SmartId.nextId(BizTypeConst.ORDER_PAY) + "";
    }

    /**
     * 生成售后单号-12位
     */
    public static String getAfsSn() {
        return 5 * DIGIT + SmartId.nextId(BizTypeConst.ORDER_AFS) + "";
    }

    /**
     * 生成结算单号-12位
     */
    public static String getBillSn() {
        return 6 * DIGIT + SmartId.nextId(BizTypeConst.BILL) + "";
    }

    /**
     * 生成用户名称
     */
    public static String getMemberName() {
        String prefix = "WWWSLODONCOM";
        int random = (int) (Math.random() * prefix.length());
        return prefix.charAt(random) + "" + SmartId.nextId(BizTypeConst.MEMBER_NAME);
    }


}
