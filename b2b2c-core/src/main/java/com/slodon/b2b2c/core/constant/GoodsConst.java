package com.slodon.b2b2c.core.constant;

/**
 * 商品常量
 **/
public class GoodsConst {

    /**
     * 是否推荐：1、推荐；0、不推荐
     */
    public final static int IS_RECOMMEND_YES = 1;
    public final static int IS_RECOMMEND_NO = 0;

    /**
     * 产品状态：
     * 11-放入仓库无需审核；
     * 12-放入仓库审核通过；
     * 20-立即上架待审核；
     * 21-放入仓库待审核；
     * 3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；
     * 4-审核驳回(平台驳回）；
     * 5-商品下架（商户自行下架）；
     * 6-违规下架（平台违规下架操作）；
     * 7-已删除（状态1、5、6可以删除后进入此状态）
     */
    public final static int GOODS_STATE_WAREHOUSE_NO_AUDIT = 11;
    public final static int GOODS_STATE_WAREHOUSE_AUDIT_PASS = 12;
    public final static int GOODS_STATE_SELL_NOW_TO_AUDIT = 20;
    public final static int GOODS_STATE_WAREHOUSE_TO_AUDIT = 21;
    public final static int GOODS_STATE_UPPER = 3;
    public final static int GOODS_STATE_REJECT = 4;
    public final static int GOODS_STATE_LOWER_BY_STORE = 5;
    public final static int GOODS_STATE_LOWER_BY_SYSTEM = 6;
    public final static int GOODS_STATE_DELETE = 7;

    /**
     * 管理后台商品列表筛选状态
     * 2-待审核商品；3-在售商品；4-仓库中商品；6-违规下架商品
     */
    public final static int GOODS_STATE_2 = 2;
    public final static int GOODS_STATE_3 = 3;
    public final static int GOODS_STATE_4 = 4;
    public final static int GOODS_STATE_6 = 6;

    /**
     * 产品发布选择状态：
     * 1-放入仓库中
     * 2-立即售卖
     */
    public final static int GOODS_STATE_IN_WAREHOUSE = 1;
    public final static int GOODS_STATE_SALE_NOW = 2;

    /**
     * 是否自营：1-自营；2-入驻商家GOODS_STATE_LOWER_BY_SYSTEM
     */
    public final static int IS_SELF_YES = 1;
    public final static int IS_SELF_NO = 2;

    /**
     * 平台商品分类状态：1、分类正常；2、分类关闭
     */
    public final static int PRODUCT_CATE_STATE_1 = 1;
    public final static int PRODUCT_CATE_STATE_2 = 2;

    /**
     * 商家是否推荐：0、不推荐；1、推荐
     */
    public final static int STORE_IS_RECOMMEND_YES = 1;
    public final static int STORE_IS_RECOMMEND_NO = 0;

    /**
     * 是否启用规格：0、没有启用规格；1、启用规格
     */
    public final static int IS_SPEC_NO = 0;
    public final static int IS_SPEC_YES = 1;

    /**
     * 是否启用属性：是否展示：0-不展示，1-展示
     */
    public final static int IS_ATTRIBUTE_NO = 0;
    public final static int IS_ATTRIBUTE_YES = 1;

    /**
     * 运费计算类型：1、按件，2、按重量，3、按体积
     */
    public final static int TRANSPORT_TYPE_1 = 1;
    public final static int TRANSPORT_TYPE_2 = 2;
    public final static int TRANSPORT_TYPE_3 = 3;

    /**
     * 店铺状态：1、店铺正常；2、店铺关闭
     */
    public final static int STORE_STATE_1 = 1;
    public final static int STORE_STATE_2 = 2;

    /**
     * 货品状态  1-正常 0-已删除 2-禁用 3-锁定
     */
    public final static int PRODUCT_STATE_0 = 0;
    public final static int PRODUCT_STATE_1 = 1;
    public final static int PRODUCT_STATE_2 = 2;
    public final static int PRODUCT_STATE_3 = 3;

    /**
     * 品牌是否推荐：1、推荐；0、不推荐
     */
    public final static int IS_BRAND_RECOMMEND_YES = 1;
    public final static int IS_BRAND_RECOMMEND_NO = 0;

    /**
     * 品牌审核拒绝或通过,1-通过,0-拒绝
     */
    public final static int BRAND_AUDIT_AGREE = 1;
    public final static int BRAND_AUDIT_REJECT = 0;

    /**
     * 商品审核拒绝或通过,1-通过,0-拒绝
     */
    public final static String GOODS_AUDIT_AGREE = "1";
    public final static String GOODS_AUDIT_REJECT = "0";

    /**
     * 品牌状态 状态操作只有系统管理员可操作，店铺申请后店铺就不能再操作了)状态 1、系统创建显示中；2、提交审核（待审核，不显示）；3、审核失败；4、删除"
     */
    public final static int BRAND_STATE_1 = 1;
    public final static int BRAND_STATE_2 = 2;
    public final static int BRAND_STATE_3 = 3;
    public final static int BRAND_STATE_4 = 4;

    /**
     * 品牌排序 默认值
     */
    public final static int BRAND_SORT = 1;

    /**
     * 模版位置 :1-顶部，2-底部
     */
    public final static int TEMPLATE_CONTENT_1 = 1;
    public final static int TEMPLATE_CONTENT_2 = 2;

    /**
     * 规格类型1、文字；2、图片
     */
    public final static int SPEC_TYPE_1 = 1;
    public final static int SPEC_TYPE_2 = 2;

    /**
     * 是否违规下架 0-否，1-是
     */
    public final static int ILLEGAL_YES = 1;
    public final static int ILLEGAL_NOT = 0;

    /**
     * 商品是否被锁定：0-否，1-是；商品参与活动后被锁定，锁定期间不能编辑，活动结束后解除锁定
     */
    public final static int IS_LOCK_YES = 1;
    public final static int IS_LOCK_NO = 0;

    /**
     * 商品是否被删除：0-否，1-是；商品伪删除
     */
    public final static int GOODS_IS_DELETE_YES = 1;
    public final static int GOODS_IS_DELETE_NO = 0;

    /**
     * 是否主图：1、主图；2、非主图，主图只能有一张
     */
    public final static int PICTURE_IS_MAIN_YES = 1;
    public final static int PICTURE_IS_MAIN_NO = 2;

    /**
     * 是否默认展示或加车货品：0-否；1-是
     */
    public final static int PRODUCT_IS_DEFAULT_YES = 1;
    public final static int PRODUCT_IS_DEFAULT_NO = 0;

    /**
     * 是否主规格,1-是，0-不是
     */
    public final static int IS_MAIN_SPEC_YES = 1;
    public final static int IS_MAIN_SPEC_NO = 0;

    /**
     * 是否已经预警过 1==否 2==是
     */
    public static final int PRODUCT_STOCK_WARNING_STATE_1 = 1;
    public static final int PRODUCT_STOCK_WARNING_STATE_2 = 2;

    /**
     * 是否可以开具增值税发票0-不可以；1-可以
     */
    public static final int IS_VAT_INVOICE_YES = 0;
    public static final int IS_VAT_INVOICE_NO = 1;

    /**
     * 状态：1、评价；2、审核通过，前台显示；3、删除
     */
    public static final int COMMENT_UNAUDIT = 1;
    public static final int COMMENT_AUDIT = 2;
    public static final int COMMENT_DEL = 3;

    /**
     * 发布商品是否需要审核，0-不需要，1-需要
     */
    public static final String GOODS_PUBLISH_NEED_AUDIT_YES = "1";
    public static final String GOODS_PUBLISH_NEED_AUDIT_NO = "0";

    /**
     * 商品参与活动状态 1-未参与活动，2-参与活动
     */
    public static final int GOOD_ACTIVITY_STATE_1 = 1;
    public static final int GOOD_ACTIVITY_STATE_2 = 2;
}
