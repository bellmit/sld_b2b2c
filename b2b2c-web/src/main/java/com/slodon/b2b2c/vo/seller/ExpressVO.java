package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.system.pojo.Express;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lxk
 * @program: slodon
 * @Description 平台快递公司VO对象
 */
@Data
public class ExpressVO {

    @ApiModelProperty("快递公司ID")
    private Integer expressId;

    @ApiModelProperty("物流名称")
    private String expressName;

    @ApiModelProperty("物流编号")
    private String expressCode;

    @ApiModelProperty("1-512；数值小排序靠前")
    private Integer sort;

    @ApiModelProperty("公司网址")
    private String website;

    public ExpressVO(Express express) {
        expressId = express.getExpressId();
        expressName = express.getExpressName();
        expressCode = express.getExpressCode();
        sort = express.getSort();
        website = express.getWebsite();
    }
}
