package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.system.pojo.TplPcMallData;
import com.slodon.b2b2c.system.pojo.TplPcMallDeco;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装pc装修页详情VO对象
 * @Author wuxy
 */
@Data
public class TplPcMallDecoDetailVO implements Serializable {

    private static final long serialVersionUID = -4458322394371803424L;
    @ApiModelProperty("装修页id")
    private Integer decoId;

    @ApiModelProperty("装修页类型")
    private String decoType;

    @ApiModelProperty("装修页名称")
    private String decoName;

    @ApiModelProperty("是否启用该装修页；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("有序实例化模板集合")
    private List<TplData> tplData;

    @ApiModelProperty("装修页主导航条数据")
    private MasterNavigationBarData masterNavigationBarData;

    @ApiModelProperty("装修页主轮播图数据")
    private MasterBannerData masterBannerData;

    public TplPcMallDecoDetailVO(TplPcMallDeco tplPcMallDeco) {
        decoId = tplPcMallDeco.getDecoId();
        decoType = tplPcMallDeco.getDecoType();
        decoName = tplPcMallDeco.getDecoName();
        isEnable = tplPcMallDeco.getIsEnable();
        createTime = tplPcMallDeco.getCreateTime();
        updateTime = tplPcMallDeco.getUpdateTime();
        if (!CollectionUtils.isEmpty(tplPcMallDeco.getRankedTplDataList())) {
            this.tplData = new ArrayList<>();
            for (TplPcMallData mallData : tplPcMallDeco.getRankedTplDataList()) {
                tplData.add(new TplData(mallData.getDataId(), mallData.getHtml()));
            }
        }
        if (null != tplPcMallDeco.getMasterNavigationBarData()) {
            this.masterNavigationBarData = new MasterNavigationBarData(tplPcMallDeco.getMasterNavigationBarData().getDataId(),
                    tplPcMallDeco.getMasterNavigationBarData().getHtml(), tplPcMallDeco.getMasterNavigationBarData().getJson());
        }
        if (null != tplPcMallDeco.getMasterBannerData()) {
            this.masterBannerData = new MasterBannerData(tplPcMallDeco.getMasterBannerData().getDataId(),
                    tplPcMallDeco.getMasterBannerData().getHtml(), tplPcMallDeco.getMasterBannerData().getJson());
        }
    }

    @Data
    private class TplData implements Serializable {

        private static final long serialVersionUID = 6203028111224735271L;
        @ApiModelProperty("id")
        private Integer id;

        @ApiModelProperty("实例化装修模板")
        private String html;

        public TplData(Integer id, String html) {
            this.id = id;
            this.html = html;
        }
    }

    @Data
    private class MasterNavigationBarData implements Serializable {

        private static final long serialVersionUID = 6762859264851592433L;
        @ApiModelProperty("id")
        private Integer id;

        @ApiModelProperty("主导航条模板")
        private String html;

        @ApiModelProperty("主导航条数据")
        private String json;

        public MasterNavigationBarData(Integer id, String html, String json) {
            this.id = id;
            this.html = html;
            this.json = json;
        }
    }

    @Data
    private class MasterBannerData implements Serializable {

        private static final long serialVersionUID = 7208688582885451008L;
        @ApiModelProperty("id")
        private Integer id;

        @ApiModelProperty("主轮播图模板")
        private String html;

        @ApiModelProperty("主轮播图数据")
        private String json;

        public MasterBannerData(Integer id, String html, String json) {
            this.id = id;
            this.html = html;
            this.json = json;
        }
    }
}
