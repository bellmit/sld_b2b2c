package com.slodon.b2b2c.controller.promotion.seller;


import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillLabelModel;
import com.slodon.b2b2c.promotion.example.SeckillLabelExample;
import com.slodon.b2b2c.promotion.pojo.SeckillLabel;
import com.slodon.b2b2c.vo.promotion.SeckillLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "seller-秒杀标签")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/seckillLabel")
public class SellerSeckillLabelController extends BaseController {

    @Resource
    private SeckillLabelModel seckillLabelModel;

    @ApiOperation("获取秒杀标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillLabelVO>> getList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        SeckillLabelExample seckillLabelExample = new SeckillLabelExample();
        seckillLabelExample.setOrderBy("sort asc, create_time desc");

        List<SeckillLabel> seckillLabelList = seckillLabelModel.getSeckillLabelList(seckillLabelExample, pager);
        List<SeckillLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(seckillLabelList)) {
            for (SeckillLabel seckillLabel : seckillLabelList) {
                SeckillLabelVO vo = new SeckillLabelVO(seckillLabel);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
