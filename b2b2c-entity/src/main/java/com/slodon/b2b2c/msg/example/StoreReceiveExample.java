package com.slodon.b2b2c.msg.example;



import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StoreReceiveExample implements Serializable {
    private static final long serialVersionUID = -6754607851059004243L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer receiveIdNotEquals;

    /**
     * 用于批量操作
     */
    private String receiveIdIn;

    /**
     * 接收消息id
     */
    private Integer receiveId;

    /**
     * 模板编码
     */
    private String tplCode;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商户id
     */
    private Long vendorId;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 消息内容,用于模糊查询
     */
    private String msgContentLike;

    /**
     * 跳转页面需要的参数
     */
    private String msgLinkInfo;

    /**
     * 大于等于开始时间
     */
    private Date msgSendTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date msgSendTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date msgOperateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date msgOperateTimeBefore;

    /**
     * 消息状态：0--未读，1--已读，2--删除
     */
    private Integer msgState;

    /**
     * 推送内容id(非推送默认为0)
     */
    private Integer pushId;

    /**
     * 数据来源：0-非系统推送；1-系统推送
     */
    private Integer source;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照receiveId倒序排列
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
     * 消息状态：0--未读，1--已读，2--删除
     */
    private Integer msgStateNotEquals;

    /**
     * 消息类型
     */
    private String tplType;
}