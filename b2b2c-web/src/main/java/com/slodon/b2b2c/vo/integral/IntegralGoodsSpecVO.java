package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpec;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpecValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装积分商品规格列表VO对象
 */
@Data
public class IntegralGoodsSpecVO implements Serializable {

    private static final long serialVersionUID = 386536766274207249L;
    @ApiModelProperty("规格id")
    private Integer specId;

    @ApiModelProperty("规格名称")
    private String specName;

    @ApiModelProperty("店铺id，0为系统创建")
    private Long storeId;

    @ApiModelProperty("规则值")
    private List<IntegralGoodsSpecValueVO> valueList = new ArrayList<>();

    public IntegralGoodsSpecVO(IntegralGoodsSpec goodsSpec, List<IntegralGoodsSpecValue> specValues) {
        if (!CollectionUtils.isEmpty(specValues)) {
            specValues.forEach(specValue -> {
                this.valueList.add(new IntegralGoodsSpecValueVO(specValue));
            });
        }
        this.specId = goodsSpec.getSpecId();
        this.specName = goodsSpec.getSpecName();
        this.storeId = goodsSpec.getStoreId();
    }

    /**
     * 规格值
     */
    @Data
    public class IntegralGoodsSpecValueVO implements Serializable {

        private static final long serialVersionUID = 2685597795933964081L;
        @ApiModelProperty("规格值id")
        private Integer specValueId;

        @ApiModelProperty("规格值")
        private String specValue;

        public IntegralGoodsSpecValueVO(IntegralGoodsSpecValue goodsSpecValue) {
            this.specValueId = goodsSpecValue.getSpecValueId();
            this.specValue = goodsSpecValue.getSpecValue();
        }
    }

}