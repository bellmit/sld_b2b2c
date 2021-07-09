package com.slodon.b2b2c.controller.promotion.admin;


import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.promotion.dto.SeckillAddDTO;
import com.slodon.b2b2c.promotion.dto.SeckillUpdateDTO;
import com.slodon.b2b2c.promotion.example.SeckillExample;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.promotion.SeckillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Api(tags = "admin-秒杀活动")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/Seckill")
public class AdminSeckillController extends BaseController {

    @Resource
    private SeckillModel seckillModel;

    @Resource
    private SeckillStageProductModel seckillStageProductModel;


    @ApiOperation("秒杀活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-未开始;2-进行中;3-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillVO>> getList(HttpServletRequest request, String seckillName, Date startTime, Date endTime, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        String fields = "IF(start_time < NOW( ) && end_time > NOW( ),1,IF( start_time > NOW( ), 2, 3 ) ) sort,ABS(NOW()-start_time) time";
        SeckillExample example = new SeckillExample();
        example.setSeckillNameLike(seckillName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (state != null) {
            switch (state) {
                case SeckillConst.SECKILL_STATE_1: //未开始 当前时间在活动开始时间之前
                    example.setStartTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STATE_2: //进行中 当前时间位于开始时间和结束时间之间
                    example.setStartTimeBefore(new Date());
                    example.setEndTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STATE_3: //已结束 当前时间在活动结束时间之后
                    example.setEndTimeBefore(new Date());
                    break;
            }
        }
        example.setOrderBy("sort,time");
        List<Seckill> list = seckillModel.getSeckillFieldList(fields, example, pager);
        List<SeckillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckill -> {
                vos.add(new SeckillVO(seckill));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("未结束秒杀活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-未开始;2-进行中)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("unFinishedList")
    public JsonResult<PageVO<SeckillVO>> getUnFinishedList(HttpServletRequest request, String seckillName, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SeckillExample example = new SeckillExample();
        example.setSeckillNameLike(seckillName);
        //未结束的 结束时间在当前时间之后
        example.setEndTimeAfter(new Date());
        if (state != null) {
            switch (state) {
                case SeckillConst.SECKILL_STATE_1: //未开始 当前时间在活动开始时间之前
                    example.setStartTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STATE_2: //进行中 当前时间位于开始时间和结束时间之间
                    example.setStartTimeBefore(new Date());
                    example.setEndTimeAfter(new Date());
                    break;
            }
        }
        List<Seckill> list = seckillModel.getSeckillList(example, pager);
        List<SeckillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckill -> {
                vos.add(new SeckillVO(seckill));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }


    @ApiOperation("获取秒杀活动详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true)
    })
    @GetMapping("getSeckill")
    public JsonResult<Seckill> getLabel(HttpServletRequest request, @RequestParam("seckillId") Integer seckillId) {
        return SldResponse.success(seckillModel.getSeckillBySeckillId(seckillId));
    }


    @ApiOperation("删除秒杀活动")
    @OperationLogger(option = "删除秒杀活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true)
    })
    @PostMapping("deleteSeckill")
    public JsonResult deleteSeckill(HttpServletRequest request, Integer seckillId) {
        //参数校验
        AssertUtil.notNullOrZero(seckillId, "请选择要删除的数据");
        Seckill seckill = seckillModel.getSeckillBySeckillId(seckillId);
        AssertUtil.notNullOrZero(seckillId, "未查询到当前活动");
        seckillModel.deleteSeckill(seckillId);

        return SldResponse.success("删除成功", "商品标签id:" + seckillId);
    }

    @ApiOperation("新增秒杀活动")
    @OperationLogger(option = "新增秒杀活动")
    @PostMapping("addSeckill")
    public JsonResult addSeckill(HttpServletRequest request, SeckillAddDTO seckillAddDTO) throws Exception {
        Admin admin = UserUtil.getUser(request, Admin.class);
        //验证参数是否为空
        AssertUtil.notEmpty(seckillAddDTO.getSeckillName(), "活动名称不能为空,请重试!");

        seckillModel.saveSeckill(seckillAddDTO, admin.getAdminId());
        return SldResponse.success("保存成功", "活动名称:" + seckillAddDTO.getSeckillName());
    }

    @ApiOperation("编辑秒杀活动")
    @OperationLogger(option = "编辑秒杀活动")
    @PostMapping("updateSeckill")
    public JsonResult updateSeckill(HttpServletRequest request, SeckillUpdateDTO seckillUpdateDTO) throws Exception {

        Admin admin = UserUtil.getUser(request, Admin.class);
        AssertUtil.notNullOrZero(seckillUpdateDTO.getSeckillId(), "请选择要修改的数据,请重试!");

        seckillModel.updateseckill(seckillUpdateDTO, admin.getAdminId());
        return SldResponse.success("修改成功", "秒杀活动名称:" + seckillUpdateDTO.getSeckillName());
    }

    @ApiOperation("设置轮播图")
    @OperationLogger(option = "设置轮播图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true),
            @ApiImplicitParam(name = "banner", value = "轮播图", required = true),
    })
    @PostMapping("setBanner")
    public JsonResult setBanner(HttpServletRequest request, Integer seckillId, String banner) throws Exception {
        Admin admin = UserUtil.getUser(request, Admin.class);
        AssertUtil.notNullOrZero(seckillId, "秒杀活动id不能为空,请重试!");
        AssertUtil.notEmpty(banner, "秒杀轮播图不能为空,请重试!");
        SeckillUpdateDTO seckillUpdateDTO = new SeckillUpdateDTO();
        seckillUpdateDTO.setSeckillId(seckillId);
        seckillUpdateDTO.setBanner(banner);
        seckillModel.updateseckill(seckillUpdateDTO, admin.getAdminId());
        return SldResponse.success("修改成功", "秒杀活动id:" + seckillId);
    }

    @ApiOperation("执行秒杀活动定时任务")
    @GetMapping("secKillJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isStart", value = "1-开始 2-结束", required = true),
    })
    public JsonResult secKillJob(HttpServletRequest request, Integer isStart) {
        boolean bret = false;
        if (isStart.equals(1)) {
            bret = seckillStageProductModel.jobSaveSeckillStageProduct();
        }
        if (isStart.equals(2)) {
            bret = seckillStageProductModel.jobRecoverSeckillProduct();
        }
        return SldResponse.success("执行秒杀定时任务" + "结果:" + bret);
    }


}
