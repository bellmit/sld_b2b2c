package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.decoration.DecoUtil;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.system.pojo.TplMobileDeco;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装mobile装修页VO对象
 * @Author wuxy
 */
@Data
public class TplMobileDecoVO implements Serializable {

    private static final long serialVersionUID = 5318055892973493251L;
    @ApiModelProperty("装修页id")
    private Integer decoId;

    @ApiModelProperty("装修页名称")
    private String name;

    @ApiModelProperty("装修页类型(首页home/专题topic/店铺seller/活动activity)")
    private String type;

    @ApiModelProperty("是否启用")
    private Integer android;

    @ApiModelProperty("是否启用")
    private Integer ios;

    @ApiModelProperty("是否启用")
    private Integer h5;

    @ApiModelProperty("是否启用")
    private Integer weixinXcx;

    @ApiModelProperty("是否启用")
    private Integer alipayXcx;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("装修页数据")
    private String data;

    @ApiModelProperty("开屏图数据")
    private String showTip;

    @ApiModelProperty("预览地址")
    private String previewUrl;

    public TplMobileDecoVO(TplMobileDeco tplMobileDeco) {
        decoId = tplMobileDeco.getDecoId();
        name = tplMobileDeco.getName();
        type = tplMobileDeco.getType();
        android = tplMobileDeco.getAndroid();
        ios = tplMobileDeco.getIos();
        h5 = tplMobileDeco.getH5();
        weixinXcx = tplMobileDeco.getWeixinXcx();
        alipayXcx = tplMobileDeco.getAlipayXcx();
        createUserName = tplMobileDeco.getCreateUserName();
        createTime = tplMobileDeco.getCreateTime();
        updateUserName = tplMobileDeco.getUpdateUserName();
        updateTime = tplMobileDeco.getUpdateTime();
        data = tplMobileDeco.getData();
        showTip = tplMobileDeco.getShowTip();
        previewUrl = dealPreviewUrl(tplMobileDeco.getType(), tplMobileDeco.getDecoId());
    }

    public String dealPreviewUrl(String type, Integer decoId) {
        String url;
        switch (type) {
            case "home":
                url = DomainUrlUtil.SLD_H5_URL + "?home_id=" + decoId + "&preview=" + DecoUtil.encodePreview(decoId.toString());
                break;
            case "topic":
                url = DomainUrlUtil.SLD_H5_URL + "/subject.html?topic_id=" + decoId + "&preview=" + DecoUtil.encodePreview(decoId.toString());
                break;
            case "activity":
                url = DomainUrlUtil.SLD_H5_URL + "?home_id=" + decoId + "&preview=" + DecoUtil.encodePreview(decoId.toString());
                break;
            default:
                url = DomainUrlUtil.SLD_H5_URL + "?home_id=" + decoId + "&preview=" + DecoUtil.encodePreview(decoId.toString());
        }
        return url;
    }
}
