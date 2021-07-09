package com.slodon.b2b2c.business.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @description: 物流信息返回值封装(快递鸟)
 * @author: wuxy
 **/
@ToString
@Data
public class DeliverInfoDTO implements Serializable {

    private static final long serialVersionUID = -720566025842547711L;
    private String EBusinessID;
    private String OrderCode;
    private String ShipperCode;         // 物流公司编码
    private String LogisticCode;        // 快递单号
    private String Success;
    private String State;
    private String Reason;
    private List<DeliverInfoDTO.Traces> Traces;       // 物流信息

    @ToString
    @Data
    public static class Traces {

        private String AcceptTime;      // 派件时间
        private String AcceptStation;   // 派件状态 + 派件地点
        private String Remark;          // 派件状态

    }

    public String getEBusinessID() {
        return EBusinessID;
    }

}
