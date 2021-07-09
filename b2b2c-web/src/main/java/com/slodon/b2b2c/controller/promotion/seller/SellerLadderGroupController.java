package com.slodon.b2b2c.controller.promotion.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.dto.LadderGroupAddDTO;
import com.slodon.b2b2c.promotion.dto.LadderGroupUpdateDTO;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-阶梯团")
@RestController
@RequestMapping("v3/promotion/seller/ladder/group")
public class SellerLadderGroupController extends BaseController {

    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private LadderGroupGoodsModel ladderGroupGoodsModel;
    @Resource
    private LadderGroupLabelModel ladderGroupLabelModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private LadderGroupRuleModel ladderGroupRuleModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("阶梯团列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-待发布；2-未开始；3-进行中；4-已失效；5-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<LadderGroupVO>> list(HttpServletRequest request, String groupName, String goodsName, Date startTime,
                                                  Date endTime, Integer state) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        LadderGroupExample example = new LadderGroupExample();
        example.setStoreId(vendor.getStoreId());
        example.setGroupNameLike(groupName);
        example.setGoodsNameLike(goodsName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == LadderGroupConst.ACTIVITY_STATE_1) {
                example.setState(LadderGroupConst.STATE_1);
            } else if (state == LadderGroupConst.ACTIVITY_STATE_2) {
                example.setState(LadderGroupConst.STATE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == LadderGroupConst.ACTIVITY_STATE_3) {
                example.setState(LadderGroupConst.STATE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == LadderGroupConst.ACTIVITY_STATE_4) {
                example.setState(LadderGroupConst.STATE_3);
            } else if (state == LadderGroupConst.ACTIVITY_STATE_5) {
                example.setState(LadderGroupConst.STATE_2);
                example.setEndTimeBefore(new Date());
            }
        }
        example.setStateNotEquals(LadderGroupConst.STATE_4);
        List<LadderGroup> list = ladderGroupModel.getLadderGroupList(example, pager);
        List<LadderGroupVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(ladderGroup -> {
                vos.add(new LadderGroupVO(ladderGroup));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("阶梯团详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<LadderGroupDetailVO> detail(HttpServletRequest request, Integer groupId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(groupId, "阶梯团id不能为空");

        LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(groupId);
        AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
        AssertUtil.isTrue(!ladderGroup.getStoreId().equals(vendor.getStoreId()), "无权限");
        LadderGroupDetailVO detailVO = new LadderGroupDetailVO(ladderGroup);

        //查询阶梯团规则信息
        LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
        ruleExample.setGroupId(groupId);
        ruleExample.setOrderBy("rule_id asc");
        List<LadderGroupRule> ruleList = ladderGroupRuleModel.getLadderGroupRuleList(ruleExample, null);
        detailVO.setRuleList(ruleList);

        //查询阶梯团活动商品信息
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(groupId);
        example.setOrderBy("group_goods_id asc");
        List<LadderGroupGoods> list = ladderGroupGoodsModel.getLadderGroupGoodsList(example, null);
        LadderGroupDetailVO.LadderGroupGoodsInfo goodsInfo = null;
        if (!CollectionUtils.isEmpty(list)) {
            goodsInfo = new LadderGroupDetailVO.LadderGroupGoodsInfo(list.get(0));
            List<LadderGroupProductVO> productVOS = new ArrayList<>();
            list.forEach(ladderGroupGoods -> {
                LadderGroupProductVO productVO = new LadderGroupProductVO(ladderGroupGoods);
                //查询sku库存
                Product product = productModel.getProductByProductId(ladderGroupGoods.getProductId());
                AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                productVO.setStock(product.getProductStock());
                productVOS.add(productVO);
            });
            goodsInfo.setProductList(productVOS);
        }
        detailVO.setGoodsInfo(goodsInfo);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("阶梯团活动标签列表")
    @GetMapping("labelList")
    public JsonResult<List<LadderGroupLabelVO>> labelList() {
        LadderGroupLabelExample example = new LadderGroupLabelExample();
        example.setIsShow(LadderGroupConst.IS_SHOW_1);
        example.setOrderBy("sort asc, create_time desc");
        List<LadderGroupLabel> list = ladderGroupLabelModel.getLadderGroupLabelList(example, null);
        List<LadderGroupLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(label -> {
                vos.add(new LadderGroupLabelVO(label));
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("新建阶梯团")
    @VendorLogger(option = "新建阶梯团")
    @PostMapping("add")
    public JsonResult addLadderGroup(HttpServletRequest request, @RequestBody LadderGroupAddDTO ladderGroupAddDTO) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        AssertUtil.notEmpty(ladderGroupAddDTO.getRuleList(), "阶梯规则信息不能为空");
        AssertUtil.notEmpty(ladderGroupAddDTO.getProductList(), "阶梯商品信息不能为空");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        ladderGroupModel.saveLadderGroup(ladderGroupAddDTO, vendor);

        return SldResponse.success("添加成功", "阶梯团活动名称:" + ladderGroupAddDTO.getGroupName());
    }

    @ApiOperation("编辑阶梯团")
    @VendorLogger(option = "编辑阶梯团")
    @PostMapping("update")
    public JsonResult updateLadderGroup(HttpServletRequest request, @RequestBody LadderGroupUpdateDTO ladderGroupUpdateDTO) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        AssertUtil.notEmpty(ladderGroupUpdateDTO.getRuleList(), "阶梯规则信息不能为空");
        AssertUtil.notEmpty(ladderGroupUpdateDTO.getProductList(), "阶梯商品信息不能为空");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        ladderGroupModel.updateLadderGroup(ladderGroupUpdateDTO);

        return SldResponse.success("编辑成功", "阶梯团活动id:" + ladderGroupUpdateDTO.getGroupId());
    }

    @ApiOperation("删除阶梯团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团id", required = true)
    })
    @VendorLogger(option = "删除阶梯团")
    @PostMapping("del")
    public JsonResult delLadderGroup(HttpServletRequest request, Integer groupId) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        ladderGroupModel.deleteLadderGroup(groupId, vendor.getStoreId());

        return SldResponse.success("删除成功", "阶梯团活动id:" + groupId);
    }

    @ApiOperation("发布阶梯团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团id", required = true)
    })
    @VendorLogger(option = "发布阶梯团")
    @PostMapping("publish")
    public JsonResult publishLadderGroup(HttpServletRequest request, Integer groupId) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        LadderGroup ladderGroup = new LadderGroup();
        ladderGroup.setGroupId(groupId);
        ladderGroup.setState(LadderGroupConst.STATE_2);
        ladderGroup.setStoreId(vendor.getStoreId());
        ladderGroupModel.updateLadderGroup(ladderGroup);

        return SldResponse.success("发布成功", "阶梯团活动id:" + groupId);
    }

    @ApiOperation("失效阶梯团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团id", required = true)
    })
    @VendorLogger(option = "失效阶梯团")
    @PostMapping("invalid")
    public JsonResult invalidLadderGroup(HttpServletRequest request, Integer groupId) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        LadderGroup ladderGroup = new LadderGroup();
        ladderGroup.setGroupId(groupId);
        ladderGroup.setState(LadderGroupConst.STATE_3);
        ladderGroup.setStoreId(vendor.getStoreId());
        ladderGroupModel.updateLadderGroup(ladderGroup);

        return SldResponse.success("失效成功", "阶梯团活动id:" + groupId);
    }

    @ApiOperation("复制阶梯团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团id", required = true)
    })
    @VendorLogger(option = "复制阶梯团")
    @PostMapping("copy")
    public JsonResult copyLadderGroup(HttpServletRequest request, Integer groupId) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块已关闭，不能操作");
        }

        AssertUtil.notNullOrZero(groupId, "请选择要复制的数据!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        LadderGroup ladderGroup = new LadderGroup();
        ladderGroup.setGroupId(groupId);
        ladderGroup.setStoreId(vendor.getStoreId());
        ladderGroupModel.copyLadderGroup(ladderGroup);

        return SldResponse.success("复制成功", "阶梯团活动id:" + groupId);
    }

    @ApiOperation("查看团队列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "阶梯团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "阶梯团状态：102-已付定金；103-已付尾款", paramType = "query")
    })
    @GetMapping("teamList")
    public JsonResult<PageVO<LadderGroupOrderExtendVO>> teamList(HttpServletRequest request, Integer groupId, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        LadderGroupOrderExtendExample example = new LadderGroupOrderExtendExample();
        example.setGroupId(groupId);
        example.setOrderSubState(state);
        example.setOrderSubStateNotEquals(LadderGroupConst.ORDER_SUB_STATE_1);
        List<LadderGroupOrderExtend> list = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(example, pager);
        List<LadderGroupOrderExtendVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (LadderGroupOrderExtend orderExtend : list) {
                vos.add(new LadderGroupOrderExtendVO(orderExtend));
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
