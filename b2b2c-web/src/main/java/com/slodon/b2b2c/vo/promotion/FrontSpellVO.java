package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.response.PagerInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装front端拼团列表VO对象
 * @Author wuxy
 */
@Data
public class FrontSpellVO implements Serializable {

    private static final long serialVersionUID = 2461495710045690442L;
    @ApiModelProperty("标签列表")
    private List<SpellLabelVO> labelList;

    @ApiModelProperty("商品列表")
    private List<FrontSpellGoodsVO> goodsList;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public FrontSpellVO(List<SpellLabelVO> labelList, List<FrontSpellGoodsVO> goodsList, PagerInfo pager) {
        this.labelList = labelList;
        this.goodsList = goodsList;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    private class Pagination implements Serializable {

        private static final long serialVersionUID = -2392688603585441372L;
        @ApiModelProperty("当前页面位置")
        private Integer current;
        @ApiModelProperty("分页大小")
        private Integer pageSize;
        @ApiModelProperty("总数")
        private Integer total;

        private Pagination(Integer current, Integer pageSize, Integer total) {
            this.current = current;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}
