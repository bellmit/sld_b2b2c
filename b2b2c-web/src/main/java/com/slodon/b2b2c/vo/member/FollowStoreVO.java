package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员收藏商品表
 */
@Data
public class FollowStoreVO implements Serializable {

    private static final long serialVersionUID = 3045568172305513021L;
    @ApiModelProperty("置顶数量")
    private Integer topNumber;

    @ApiModelProperty("关注店铺列表")
    private List<MemberFollowStoreVO> storeList;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public FollowStoreVO(Integer topNumber, List<MemberFollowStoreVO> storeList, PagerInfo pager) {
        this.topNumber = topNumber;
        this.storeList = storeList;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    public static class MemberFollowStoreVO implements Serializable {

        private static final long serialVersionUID = -3353673691979965122L;

        @ApiModelProperty("收藏id")
        private Integer followId;

        @ApiModelProperty("会员ID")
        private Integer memberId;

        @ApiModelProperty("店铺ID")
        private Long storeId;

        @ApiModelProperty("是否置顶：0、不置顶；1、置顶")
        private Integer isTop;

        @ApiModelProperty("是否置顶：0、不置顶；1、置顶")
        private String isTopValue;

        @ApiModelProperty("店铺名称")
        private String storeName;

        @ApiModelProperty("店铺LOGO")
        private String storeLogo;

        @ApiModelProperty("店铺评分服务（每天定时任务计算积分）")
        private String serviceScore;

        @ApiModelProperty("店铺收藏")
        private Integer followNumber;

        @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
        private Integer isOwnStore;

        @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
        private String isOwnStoreValue;

        @ApiModelProperty("店铺推荐商品集合,最多三件商品")
        private List<RecommendGoodsVO> goodsList = new ArrayList<>();

        @ApiModelProperty("本月上新商品")
        private List<RecommendGoodsVO> newGoodsList = new ArrayList<>();

        public MemberFollowStoreVO(MemberFollowStore memberFollowStore, Store store) {
            followId = memberFollowStore.getFollowId();
            memberId = memberFollowStore.getMemberId();
            storeId = memberFollowStore.getStoreId();
            isTop = memberFollowStore.getIsTop();
            isTopValue = dealIsTopValue(memberFollowStore.getIsTop());

            storeName = store.getStoreName();
            storeLogo = dealStoreLogoValue(store.getStoreLogo());
            serviceScore = store.getServiceScore();
            followNumber = store.getFollowNumber();
            isOwnStore = store.getIsOwnStore();
            isOwnStoreValue = dealIsOwnStoreValue(store.getIsOwnStore());
        }

        public static String dealIsTopValue(Integer isTop) {
            String value = null;
            if (StringUtils.isEmpty(isTop)) return null;
            switch (isTop) {
                case StoreConst.IS_TOP_0:
                    value = "不置顶";
                    break;
                case StoreConst.IS_TOP_1:
                    value = "置顶";
                    break;
            }
            //翻译
            value = Language.translate(value);
            return value;
        }

        public static String dealStoreLogoValue(String storeLogo) {
            if (StringUtils.isEmpty(storeLogo)) return null;
            return DomainUrlUtil.SLD_IMAGE_RESOURCES + storeLogo;
        }

        public static String dealIsOwnStoreValue(Integer isOwnStore) {
            String value = null;
            if (StringUtils.isEmpty(isOwnStore)) return null;
            switch (isOwnStore) {
                case StoreConst.IS_OWN_STORE:
                    value = "自营店铺";
                    break;
                case StoreConst.NO_OWN_STORE:
                    value = "入驻店铺";
                    break;
            }
            //翻译
            value = Language.translate(value);
            return value;
        }

        /**
         * 店铺推荐商品信息，每个RecommendGoodsVO对应一个返回的商品信息
         */
        @Data
        @NoArgsConstructor
        public static class RecommendGoodsVO implements Serializable {
            private static final long serialVersionUID = -1141492058948144620L;

            public RecommendGoodsVO(Goods goods) {
                BeanUtils.copyProperties(goods, this);
                mainImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
            }

            @ApiModelProperty("商品ID")
            private Long goodsId;

            @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
            private String goodsName;

            @ApiModelProperty("商品副标题，长度建议140个字符内")
            private String goodsBrief;

            @ApiModelProperty("市场价")
            private BigDecimal marketPrice;

            @ApiModelProperty("商城价销售价格")
            private BigDecimal goodsPrice;

            @ApiModelProperty("商品上架时间（a. 不需要平台审核时即为商户提交上架的时间，b. 需要审核时为平台审核通过的时间）")
            private Date onlineTime;

            @ApiModelProperty("商品主图路径，每个SPU一张主图")
            private String mainImage;

            @ApiModelProperty("默认货品id")
            private Long defaultProductId;
        }
    }

    @Data
    private class Pagination implements Serializable {

        private static final long serialVersionUID = 764490506152330211L;
        @ApiModelProperty("当前页面位置")
        private Integer current;
        @ApiModelProperty("分页大小")
        private Integer pageSize;
        @ApiModelProperty("总数")
        private Integer total;

        private Pagination(Integer current, Integer pageSize, Integer total) {
            this.current = current;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}