package com.slodon.b2b2c.vo.business;


import com.slodon.b2b2c.business.pojo.Complain;
import com.slodon.b2b2c.business.pojo.ComplainTalk;
import com.slodon.b2b2c.business.pojo.OrderAfterService;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.ComplainConst;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 封装admin投诉详情vo
 */
@Data
public class ComplainDetailVO implements Serializable {
    private static final long serialVersionUID = -6058704760433332945L;
    @ApiModelProperty("投诉id")
    private Integer complainId;

    @ApiModelProperty("投诉主题id")
    private Integer complainSubjectId;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("订单货品明细id")
    private Long orderProductId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品货品ID")
    private Long productId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品规格")
    private String specValues;

    @ApiModelProperty("投诉会员id")
    private Integer complainMemberId;

    @ApiModelProperty("投诉会员名称")
    private String complainMemberName;

    @ApiModelProperty("被投诉店铺id")
    private Long storeId;

    @ApiModelProperty("被投诉店铺名称")
    private String storeName;

    @ApiModelProperty("投诉内容")
    private String complainContent;

    @ApiModelProperty("投诉图片（最多6张图片）")
    private List<String> complainPic;

    @ApiModelProperty("投诉时间")
    private Date complainTime;

    @ApiModelProperty("投诉审核时间")
    private Date complainAuditTime;

    @ApiModelProperty("投诉审核管理员id；0位超期系统自动处理")
    private Integer complainAuditAdminId;

    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    @ApiModelProperty("商户申诉内容")
    private String appealContent;

    @ApiModelProperty("商户申诉图片（最多6张图片）")
    private List<String> appealImage;

    @ApiModelProperty("商户申诉时间")
    private Date appealTime;

    @ApiModelProperty("申诉的店铺管理员ID")
    private Long appealVendorId;

    @ApiModelProperty("平台仲裁意见")
    private String adminHandleContent;

    @ApiModelProperty("平台仲裁时间")
    private Date adminHandleTime;

    @ApiModelProperty("平台仲裁管理员id")
    private Integer adminHandleId;

    @ApiModelProperty("投诉对话状态，1-新投诉/2-待申诉(投诉通过转给商家)/3-对话中(商家已申诉)/4-待仲裁/5-已撤销/6-会员胜诉/7-商家胜诉）")
    private Integer complainState;

    @ApiModelProperty("投诉状态，1-待平台处理/2-待商家申诉/3-待双方对话/4-待平台仲裁/5-会员撤销投诉/6-会员胜诉/7-商家胜诉")
    private String complainStateValue;

    @ApiModelProperty("处理截止时间（用于前台展示）")
    private Date handleDeadline;


    @ApiModelProperty("对话信息列表")
    private List<ComplainTalkInfo> complainTalkInfoList;

    public ComplainDetailVO(Complain complain, OrderAfterService orderAfterService, OrderProduct orderProduct) {

        this.complainId = complain.getComplainId();
        this.complainSubjectId = complain.getComplainSubjectId();
        this.afsSn = complain.getAfsSn();
        this.orderSn = complain.getOrderSn();

        //商品信息
        this.orderProductId = orderAfterService.getOrderProductId();
        this.goodsId = orderProduct.getGoodsId();
        this.productId = orderProduct.getProductId();
        this.goodsName = orderProduct.getGoodsName();
        this.goodsImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null);
        this.specValues = orderProduct.getSpecValues();

        //投诉信息
        this.complainMemberId = complain.getComplainMemberId();
        this.complainMemberName = complain.getComplainMemberName();
        this.storeId = complain.getStoreId();
        this.storeName = complain.getStoreName();
        this.complainContent = complain.getComplainContent();
        this.complainPic = dealImages(complain.getComplainPic());
        this.complainTime = complain.getComplainTime();

        //平台审核
        this.complainAuditTime = complain.getComplainAuditTime();
        this.complainAuditAdminId = complain.getComplainAuditAdminId();
        this.refuseReason = complain.getRefuseReason();

        //商家申诉信息
        this.appealContent = complain.getAppealContent();
        this.appealImage = dealImages(complain.getAppealImage());
        this.appealTime = complain.getAppealTime();
        this.appealVendorId = complain.getAppealVendorId();

        //平台仲裁信息
        this.adminHandleContent = complain.getAdminHandleContent();
        this.adminHandleTime = complain.getAdminHandleTime();
        this.adminHandleId = complain.getAdminHandleId();

        //状态
        this.complainState = complain.getComplainState();
        this.complainStateValue = dealComplainStateValue(complain.getComplainState());

        //截止时间
        this.handleDeadline = complain.getHandleDeadline();
    }

    public static List<String> dealImages(String images) {
        List<String> list = new ArrayList<>();
        if (StringUtil.isEmpty(images)) return null;
        List<String> complainPicList = Arrays.asList(images.split(","));
        complainPicList.forEach(image -> {
            if (!StringUtil.isEmpty(image)) {
                list.add(FileUrlUtil.getFileUrl(image, null));
            }
        });
        return list;
    }

    public static String dealComplainStateValue(Integer complainState) {
        String value = "";
        if (StringUtils.isEmpty(complainState)) return null;
        switch (complainState) {
            case ComplainConst.COMPLAIN_STATE_1:
                value = "待平台处理";
                break;
            case ComplainConst.COMPLAIN_STATE_2:
                value = "待商家申诉";
                break;
            case ComplainConst.COMPLAIN_STATE_3:
                value = "待双方对话";
                break;
            case ComplainConst.COMPLAIN_STATE_4:
                value = "待平台仲裁";
                break;
            case ComplainConst.COMPLAIN_STATE_5:
                value = "会员撤销投诉";
                break;
            case ComplainConst.COMPLAIN_STATE_6:
                value = "会员胜诉";
                break;
            case ComplainConst.COMPLAIN_STATE_7:
                value = "商家胜诉";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    @Data
    public static class ComplainTalkInfo implements Serializable {

        private static final long serialVersionUID = 8735824819216244507L;
        @ApiModelProperty("投诉对话id")
        private Integer complainTalkId;

        @ApiModelProperty("投诉id编号")
        private Integer complainId;

        @ApiModelProperty("投诉对话用户id")
        private Long talkUserId;

        @ApiModelProperty("投诉对话用户名称")
        private String talkUserName;

        @ApiModelProperty("投诉对话用户类型：1-会员，2-商户，3-平台")
        private Integer talkUserType;

        @ApiModelProperty("投诉对话用户类型：1-会员，2-商户，3-平台")
        private String talkUserTypeValue;

        @ApiModelProperty("投诉对话内容")
        private String talkContent;

        @ApiModelProperty("投诉对话时间")
        private Date talkTime;

        public ComplainTalkInfo(ComplainTalk complainTalk) {
            this.complainTalkId = complainTalk.getComplainTalkId();
            this.complainId = complainTalk.getComplainId();
            this.talkUserId = complainTalk.getTalkUserId();
            this.talkUserName = complainTalk.getTalkUserName();
            this.talkUserType = complainTalk.getTalkUserType();
            this.talkUserTypeValue = dealTalkUserTypeValue(complainTalk.getTalkUserType());
            this.talkContent = complainTalk.getTalkContent();
            this.talkTime = complainTalk.getTalkTime();
        }

        public ComplainTalkInfo() {

        }

        public static String dealTalkUserTypeValue(Integer talkUserType) {
            String value = "";
            if (StringUtil.isNullOrZero(talkUserType)) return null;
            switch (talkUserType) {
                case ComplainConst.TALK_USER_TYPE_1:
                    value = "会员";
                    break;
                case ComplainConst.TALK_USER_TYPE_2:
                    value = "商户";
                    break;
                case ComplainConst.TALK_USER_TYPE_3:
                    value = "平台";
                    break;
            }
            //翻译
            value = Language.translate(value);
            return value;
        }
    }
}