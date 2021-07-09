package com.slodon.b2b2c.controller.promotion.admin;


import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillStageModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Api(tags = "admin-秒杀活动场次")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/seckillStage")
public class AdminSeckillStageController extends BaseController {

    @Resource
    private SeckillStageModel seckillStageModel;

    @Resource
    private SeckillStageProductModel seckillStageProductModel;


    @ApiOperation("秒杀活动场次列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", paramType = "query"),
            @ApiImplicitParam(name = "stageName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-未开始;2-进行中;3-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillStageVO>> getList(HttpServletRequest request, Integer seckillId, String stageName, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        String fields = "IF(start_time < NOW( ) && end_time > NOW( ),1,IF( start_time > NOW( ), 2, 3 ) ) sort";
        SeckillStageExample example = new SeckillStageExample();
        example.setSeckillId(seckillId);
        example.setStageNameLike(stageName);
        if (state != null) {
            switch (state) {
                case SeckillConst.SECKILL_STAGE_STATE_1: //未开始 当前时间在活动开始时间之前
                    example.setStartTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STAGE_STATE_2: //进行中 当前时间位于开始时间和结束时间之间
                    example.setStartTimeBefore(new Date());
                    example.setEndTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STAGE_STATE_3: //已结束 当前时间在活动结束时间之后
                    example.setEndTimeBefore(new Date());
                    break;
            }
        }
        example.setOrderBy("sort,start_time");
        List<SeckillStage> list = seckillStageModel.getSeckillStageFieldList(fields, example, pager);
        List<SeckillStageVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckillStage -> {
                SeckillStageVO vo = new SeckillStageVO(seckillStage);
                vo.setProductCount(seckillStageProductModel.getCountByStageID(seckillStage.getStageId()));
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
