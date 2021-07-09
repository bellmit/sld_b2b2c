package com.slodon.b2b2c.vo.cms;


import com.slodon.b2b2c.cms.pojo.FriendLink;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装合作伙伴VO对象
 */
@Data
public class FriendLinkVO {

    @ApiModelProperty("链接id")
    private Integer linkId;

    @ApiModelProperty("合作伙伴名称")
    private String linkName;

    @ApiModelProperty("链接url")
    private String linkUrl;

    @ApiModelProperty("排序：数字越小，越靠前")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public FriendLinkVO(FriendLink friendLink) {
        linkId = friendLink.getLinkId();
        linkName = friendLink.getLinkName();
        linkUrl = friendLink.getLinkUrl();
        sort = friendLink.getSort();
        createTime = friendLink.getCreateTime();
    }
}
