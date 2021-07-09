package com.slodon.b2b2c.helpdesk.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HelpdeskMsgExample implements Serializable {
    private static final long serialVersionUID = -475090836969017128L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer msgIdNotEquals;

    /**
     * 用于批量操作
     */
    private String msgIdIn;

    /**
     * 消息id小于
     */
    private Long msgIdLt;

    /**
     * 消息ID
     */
    private Integer msgId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 会员名
     */
    private String memberName;

    /**
     * 会员名,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 会员头像
     */
    private String memberAvatar;

    /**
     * 商户ID
     */
    private Long vendorId;

    /**
     * 商户名称
     */
    private String vendorName;

    /**
     * 商户名称,用于模糊查询
     */
    private String vendorNameLike;

    /**
     * 商户头像
     */
    private String vendorAvatar;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 消息内容，JSON格式，解析参考文档
     */
    private String msgContent;

    /**
     * 消息内容，JSON格式，解析参考文档,用于模糊查询
     */
    private String msgContentLike;

    /**
     * 消息状态:1为已读,2为未读,默认为2
     */
    private Integer msgState;

    /**
     * 消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户
     */
    private Integer msgType;

    /**
     * 用户类型：1、会员发送；2、商户发送
     */
    private Integer userType;

    /**
     * 大于等于开始时间
     */
    private Date addTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date addTimeBefore;

    /**
     * 是否删除:0为未删除,1为已删除
     */
    private Integer isDelete;

    /**
     * 微信用户头像
     */
    private String wxAvatarImg;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照msgId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;

    /**
     * 查询条件
     */
    private String firstContact;
}