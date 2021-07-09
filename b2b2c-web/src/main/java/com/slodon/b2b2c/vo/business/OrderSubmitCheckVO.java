package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单确认、订单提交前检测购物车状态VO
 */
@Data
public class OrderSubmitCheckVO {
    @ApiModelProperty("检测状态，0==检测通过，1==全部下架，2==部分下架，3==全部无货，4==部分无货，5==活动失效，6==商品限购")
    private Integer state = STATE_0;
    @ApiModelProperty("检测状态值，0==检测通过，1==全部下架，2==部分下架，3==全部无货，4==部分无货，5==活动失效，6==商品限购")
    private String stateValue;
    @ApiModelProperty("检测不通过的sku列表")
    private List<OrderSubmitPageVO.StoreGroupVO.ProductVO> productList = new ArrayList<>();

    public String getStateValue() {
        String value = "检测通过";
        switch (getState()) {
            case STATE_1:
                value = "本单购买商品全部下架";
                break;
            case STATE_2:
                value = "以下商品下架";
                break;
            case STATE_3:
                value = "本单购买商品全部无货";
                break;
            case STATE_4:
                value = "以下商品无货";
                break;
            case STATE_5:
                value = "商品活动信息发生变化";
                break;
            case STATE_6:
                value = "本单商品已达限购数量";
                break;
        }

        //翻译
        value = Language.translate(value);
        return value;
    }

    /**
     * 检测状态常量
     */
    public static final int STATE_0 = 0;
    public static final int STATE_1 = 1;
    public static final int STATE_2 = 2;
    public static final int STATE_3 = 3;
    public static final int STATE_4 = 4;
    public static final int STATE_5 = 5;
    public static final int STATE_6 = 6;
}
