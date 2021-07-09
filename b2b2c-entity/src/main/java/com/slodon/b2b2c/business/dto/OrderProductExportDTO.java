package com.slodon.b2b2c.business.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单货品导出dto
 */
@Data
public class OrderProductExportDTO implements Serializable {
    private static final long serialVersionUID = -4955957222770220937L;

    private Long   orderProductId;                //订单货品id
    private String goodsName;                   //商品名称
    private String specValues;                  //商品规格
    private String goodsNum;                    //商品数量
    private String goodsPrice;                  //商品单价(元)
}
