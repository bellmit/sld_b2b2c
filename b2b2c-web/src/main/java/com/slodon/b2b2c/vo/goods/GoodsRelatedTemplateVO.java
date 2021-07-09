package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsRelatedTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsRelatedTemplateVO {
    

    @ApiModelProperty("模板id")
    private Integer templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("模版位置(1-顶部，2-底部）")
    private Integer templatePosition;

    @ApiModelProperty("模版内容")
    private String templateContent;

    public GoodsRelatedTemplateVO(GoodsRelatedTemplate goodsRelatedTemplate) {
        this.templateId = goodsRelatedTemplate.getTemplateId();
        this.templateName =goodsRelatedTemplate.getTemplateName();
        this.templatePosition = goodsRelatedTemplate.getTemplatePosition();
        this.templateContent = goodsRelatedTemplate.getTemplateContent();
    }
}
