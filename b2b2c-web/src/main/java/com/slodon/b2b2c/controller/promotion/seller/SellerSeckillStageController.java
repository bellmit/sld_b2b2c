package com.slodon.b2b2c.controller.promotion.seller;


import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillStageModel;
import com.slodon.b2b2c.promotion.example.SeckillStageExample;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import com.slodon.b2b2c.vo.promotion.SeckillStageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "seller-秒杀场次")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/seckillStage")
public class SellerSeckillStageController extends BaseController {

    @Resource
    private SeckillStageModel seckillStageModel;

    @ApiOperation("获取秒杀场次列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillStageVO>> getList(HttpServletRequest request,
                                                      @RequestParam("seckillId") Integer seckillId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        SeckillStageExample seckillStageExample = new SeckillStageExample();
        seckillStageExample.setSeckillId(seckillId);
        seckillStageExample.setStartTimeAfter(new Date());
        seckillStageExample.setOrderBy("start_time asc");

        List<SeckillStage> seckillStageList = seckillStageModel.getSeckillStageList(seckillStageExample, pager);
        List<SeckillStageVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(seckillStageList)) {
            for (SeckillStage seckillStage : seckillStageList) {
                SeckillStageVO vo = new SeckillStageVO(seckillStage);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
