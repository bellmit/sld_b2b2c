package com.slodon.b2b2c.core.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @program: slodon
 * @Description 列表返回数据
 * @Author wuxy
 */
@Data
public class PageVO<T> {

    @ApiModelProperty("数据结果集")
    private List<T> list;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public PageVO(List<T> list, PagerInfo pager) {
        this.list = list;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    private class Pagination {

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
