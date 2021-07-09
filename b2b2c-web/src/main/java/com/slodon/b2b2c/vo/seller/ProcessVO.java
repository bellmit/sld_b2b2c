package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 申请进程VO对象
 */
@Data
public class ProcessVO {

    @ApiModelProperty("状态：0-未入驻，1-店铺信息提交申请；2-店铺信息审核通过；3-店铺信息审核失败；4-开通店铺(支付完成)")
    private Integer applyStep;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    @ApiModelProperty("拒绝备注")
    private String auditInfo;

    @ApiModelProperty("店铺等级id")
    private Integer storeGradeId;

    @ApiModelProperty("店铺等级名称")
    private String gradeName;

    @ApiModelProperty("收费标准（每年）")
    private String price;

    @ApiModelProperty("开店时长（年）")
    private String applyYear;

    @ApiModelProperty("应付金额（元）")
    private String payAmount;

    @ApiModelProperty("状态值：1、待审核；2、代付款；3、已拒绝；")
    private String stateValue;

    @ApiModelProperty("经营类目集合")
    private List<StoreGoodsCateVO> storeGoodsCateVOList;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("支付单号")
    private String paySn;

    public ProcessVO() {
    }

    public ProcessVO(StoreGrade storeGrade, StoreApply storeApply) {
        storeGradeId = storeGrade.getGradeId();
        gradeName = storeGrade.getGradeName();
        price = storeGrade.getPrice();
        applyStep = storeApply.getState();
        refuseReason = storeApply.getRefuseReason();
        auditInfo = storeApply.getAuditInfo();
        storeName = storeApply.getStoreName();
        paySn = storeApply.getPaySn();

    }

}
