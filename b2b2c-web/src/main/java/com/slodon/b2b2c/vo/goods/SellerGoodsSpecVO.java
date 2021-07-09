package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsSpec;
import com.slodon.b2b2c.goods.pojo.GoodsSpecValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装seller商品规格列表VO对象
 */
@Data
public class SellerGoodsSpecVO {

    @ApiModelProperty("规格id")
    private Integer specId;

    @ApiModelProperty("规格名称")
    private String specName;

    @ApiModelProperty("店铺id，0为系统创建")
    private Long storeId;

    @ApiModelProperty("状态 0-不展示 1-展示，可以删除，但是必须没有其他商品使用")
    private Integer state;

    @ApiModelProperty("排序")
    private Integer sort;


    @ApiModelProperty("规则值")
    private List<SellerGoodsSpecValueVO> valueList=new ArrayList<>();

    public SellerGoodsSpecVO(GoodsSpec goodsSpec, List<GoodsSpecValue> specValues) {
        if(!CollectionUtils.isEmpty(specValues))
        {
            specValues.forEach(specValue -> {
                this.valueList.add(new SellerGoodsSpecValueVO(specValue));
            });
        }
        this.specId = goodsSpec.getSpecId();
        this.specName = goodsSpec.getSpecName();
        this.state = goodsSpec.getState();
        this.sort = goodsSpec.getSort();
        this.storeId = goodsSpec.getStoreId();
    }


    /**
     * 规格值表
     */
    @Data
    public class SellerGoodsSpecValueVO implements Serializable {
        private static final long serialVersionUID = 235344565765768723L;

        public SellerGoodsSpecValueVO(GoodsSpecValue goodsSpecValue)
        {
            this.specValueId=goodsSpecValue.getSpecValueId();
            this.specValue=goodsSpecValue.getSpecValue();
            this.createTime=goodsSpecValue.getCreateTime();
        }

        @ApiModelProperty("规格值id")
        private Integer specValueId;

        @ApiModelProperty("规格值")
        private String specValue;

        @ApiModelProperty("创建时间")
        private Date createTime;
    }
}