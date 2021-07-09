package com.slodon.b2b2c.controller.promotion.front;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.promotion.pojo.LadderGroupLabel;
import com.slodon.b2b2c.promotion.pojo.LadderGroupRule;
import com.slodon.b2b2c.vo.promotion.FrontLadderGroupDetailVO;
import com.slodon.b2b2c.vo.promotion.FrontLadderGroupVO;
import com.slodon.b2b2c.vo.promotion.LadderGroupLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "front-阶梯团")
@RestController
@RequestMapping("v3/promotion/front/ladder/group")
public class FrontLadderGroupController extends BaseController {

    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private LadderGroupGoodsModel ladderGroupGoodsModel;
    @Resource
    private LadderGroupLabelModel ladderGroupLabelModel;
    @Resource
    private LadderGroupRuleModel ladderGroupRuleModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("阶梯团列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<FrontLadderGroupVO> list(HttpServletRequest request, Integer labelId) {
        String isEnable = stringRedisTemplate.opsForValue().get("ladder_group_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("阶梯团模块未开启");
        }

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询阶梯团标签
        LadderGroupLabelExample example = new LadderGroupLabelExample();
        example.setIsShow(LadderGroupConst.IS_SHOW_1);
        example.setOnGoing("notNull");
        example.setOrderBy("sort asc, create_time desc");
        List<LadderGroupLabel> labelList = ladderGroupLabelModel.getLadderGroupLabelList(example, null);
        List<LadderGroupLabelVO> labelVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(labelList)) {
            labelList.forEach(label -> {
                labelVOS.add(new LadderGroupLabelVO(label));
            });
        }
        //查询该标签下已发布的活动
        LadderGroupExample groupExample = new LadderGroupExample();
        if (!StringUtil.isNullOrZero(labelId)) {
            groupExample.setLabelId(labelId);
        }
        groupExample.setState(LadderGroupConst.STATE_2);
        groupExample.setStartTimeBefore(new Date());
        groupExample.setEndTimeAfter(new Date());
        List<LadderGroup> groupList = ladderGroupModel.getLadderGroupList(groupExample, null);
        List<FrontLadderGroupVO.FrontLadderGroupGoodsVO> goodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupList)) {
            StringBuilder groupIdIn = new StringBuilder();
            for (LadderGroup ladderGroup : groupList) {
                groupIdIn.append(",").append(ladderGroup.getGroupId());
            }
            //查询阶梯团商品
            LadderGroupGoodsExample goodsExample = new LadderGroupGoodsExample();
            goodsExample.setGroupIdIn(groupIdIn.substring(1));
            goodsVOS = ladderGroupGoodsModel.getFrontLadderGroupGoodsList(goodsExample, pager);
        }
        return SldResponse.success(new FrontLadderGroupVO(labelVOS, goodsVOS, pager));
    }

    @SneakyThrows
    @ApiOperation("阶梯团商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionId", value = "活动id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<FrontLadderGroupDetailVO> detail(HttpServletRequest request, Long productId, Integer promotionId) {
        AssertUtil.notNullOrZero(productId, "阶梯团货品id不能为空");
        AssertUtil.notNullOrZero(promotionId, "阶梯团活动id不能为空");
        Member member = UserUtil.getUser(request, Member.class);

        //查询阶梯团活动
        LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(promotionId);
        AssertUtil.notNull(ladderGroup, "阶梯团活动不存在");
        //查询阶梯团商品
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(promotionId);
        example.setProductId(productId);
        List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsModel.getLadderGroupGoodsList(example, null);
        AssertUtil.notEmpty(groupGoodsList, "阶梯团商品不存在");
        LadderGroupGoods ladderGroupGoods = groupGoodsList.get(0);
        FrontLadderGroupDetailVO detailVO = new FrontLadderGroupDetailVO(ladderGroup, ladderGroupGoods);
        if (!StringUtils.isEmpty(ladderGroup.getEndTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            Date now = new Date();
            if (now.before(ladderGroup.getStartTime())) {
                date = format.parse(format.format(ladderGroup.getStartTime()));
            } else {
                date = format.parse(format.format(ladderGroup.getEndTime()));
            }
            long second = (date.getTime() - now.getTime()) / 1000;
            detailVO.setDistanceEndTime(second);
        }
        //查询阶梯团订单已付款的数量
        LadderGroupOrderExtendExample extendExample = new LadderGroupOrderExtendExample();
        extendExample.setGroupId(promotionId);
        extendExample.setOrderSubStateNotEquals(LadderGroupConst.ORDER_SUB_STATE_1);
        Integer extendCount = ladderGroupOrderExtendModel.getLadderGroupOrderExtendCount(extendExample);
        detailVO.setJoinedNum(extendCount);

        //查询阶梯规则
        LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
        ruleExample.setGroupId(promotionId);
        ruleExample.setOrderBy("rule_id asc");
        List<LadderGroupRule> ruleList = ladderGroupRuleModel.getLadderGroupRuleList(ruleExample, null);
        List<FrontLadderGroupDetailVO.LadderGroupRuleVO> ruleVOS = new ArrayList<>();
        ruleList.forEach(ladderGroupRule -> {
            BigDecimal ladderPrice = null;
            if (ladderGroupRule.getLadderLevel() == LadderGroupConst.LADDER_LEVEL_3) {
                ladderPrice = ladderGroupGoods.getLadderPrice3();
            } else if (ladderGroupRule.getLadderLevel() == LadderGroupConst.LADDER_LEVEL_2) {
                ladderPrice = ladderGroupGoods.getLadderPrice2();
            } else {
                ladderPrice = ladderGroupGoods.getLadderPrice1();
            }
            //正序遍历，最后一定是满足条件的
            if (extendCount >= ladderGroupRule.getJoinGroupNum()) {
                detailVO.setCurrentPrice(ladderPrice);
                detailVO.setCurrentLadderLevel(ladderGroupRule.getLadderLevel());
            }
            ruleVOS.add(new FrontLadderGroupDetailVO.LadderGroupRuleVO(ladderGroupRule, ladderPrice));
        });
        detailVO.setRuleList(ruleVOS);
        if (member.getMemberId() != null) {
            String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + ladderGroupGoods.getGoodsId() + "_" + member.getMemberId());
            detailVO.setPurchasedNum(StringUtil.isEmpty(purchasedNum) ? 0 : Integer.parseInt(purchasedNum));
            //判断是否达到限购数量(普通商品详情已判断库存是否不足，所以这里不加库存判断)
            if (!StringUtil.isEmpty(purchasedNum)) {
                if (ladderGroup.getBuyLimitNum() > 0 && Integer.parseInt(purchasedNum) >= ladderGroup.getBuyLimitNum()) {
                    detailVO.setIsCanBuy(false);
                }
            }
        }
        return SldResponse.success(detailVO);
    }
}
