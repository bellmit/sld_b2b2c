package com.slodon.b2b2c.controller.promotion.front;

import com.slodon.b2b2c.core.constant.PreSellConst;
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
import com.slodon.b2b2c.model.promotion.PresellGoodsModel;
import com.slodon.b2b2c.model.promotion.PresellLabelModel;
import com.slodon.b2b2c.model.promotion.PresellModel;
import com.slodon.b2b2c.promotion.example.PresellExample;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.PresellLabelExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import com.slodon.b2b2c.vo.promotion.FrontPreSellDetailVO;
import com.slodon.b2b2c.vo.promotion.FrontPresellVO;
import com.slodon.b2b2c.vo.promotion.PreSellLabelVO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "front-预售")
@RestController
@RequestMapping("v3/promotion/front/preSell")
public class FrontPreSellController extends BaseController {

    @Resource
    private PresellModel presellModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;
    @Resource
    private PresellLabelModel presellLabelModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("预售列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<FrontPresellVO> list(HttpServletRequest request, Integer labelId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块未开启");
        }

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询预售标签
        PresellLabelExample example = new PresellLabelExample();
        example.setIsShow(PreSellConst.IS_SHOW_1);
        example.setOnGoing("notNull");
        example.setOrderBy("sort asc, create_time desc");
        List<PresellLabel> labelList = presellLabelModel.getPresellLabelList(example, null);
        List<PreSellLabelVO> labelVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(labelList)) {
            labelList.forEach(label -> {
                labelVOS.add(new PreSellLabelVO(label));
            });
        }
        //查询该标签下已发布的活动
        PresellExample presellExample = new PresellExample();
        if (!StringUtil.isNullOrZero(labelId)) {
            presellExample.setPresellLabelId(labelId);
        }
        presellExample.setState(PreSellConst.ACTIVITY_STATE_2);
        presellExample.setStartTimeBefore(new Date());
        presellExample.setEndTimeAfter(new Date());
        List<Presell> presellList = presellModel.getPresellList(presellExample, null);
        List<FrontPresellVO.FrontPreSellGoodsVO> goodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(presellList)) {
            StringBuilder presellIdIn = new StringBuilder();
            for (Presell presell : presellList) {
                presellIdIn.append(",").append(presell.getPresellId());
            }
            //查询预售商品
            PresellGoodsExample goodsExample = new PresellGoodsExample();
            goodsExample.setPresellIdIn(presellIdIn.substring(1));
            goodsVOS = presellGoodsModel.getFrontPreSellGoodsList(goodsExample, pager);
        }
        return SldResponse.success(new FrontPresellVO(labelVOS, goodsVOS, pager));
    }

    @SneakyThrows
    @ApiOperation("预售商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionId", value = "活动id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<FrontPreSellDetailVO> detail(HttpServletRequest request, Long productId, Integer promotionId) {
        AssertUtil.notNullOrZero(productId, "预售货品id不能为空");
        AssertUtil.notNullOrZero(promotionId, "预售活动id不能为空");

        Member member = UserUtil.getUser(request, Member.class);

        //查询预售活动
        Presell presell = presellModel.getPresellByPresellId(promotionId);
        AssertUtil.notNull(presell, "预售活动不存在");
        //查询预售商品
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(promotionId);
        example.setProductId(productId);
        List<PresellGoods> presellGoodsList = presellGoodsModel.getPresellGoodsList(example, null);
        AssertUtil.notEmpty(presellGoodsList, "预售商品不存在");
        FrontPreSellDetailVO detailVO = new FrontPreSellDetailVO(presell, presellGoodsList.get(0));
        if (!StringUtils.isEmpty(presell.getEndTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            Date now = new Date();
            if (now.before(presell.getStartTime())) {
                date = format.parse(format.format(presell.getStartTime()));
            } else {
                date = format.parse(format.format(presell.getEndTime()));
            }
            long second = (date.getTime() - now.getTime()) / 1000;
            detailVO.setDistanceEndTime(second);
        }
        if (member.getMemberId() != null) {
            String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + presellGoodsList.get(0).getGoodsId() + "_" + member.getMemberId());
            detailVO.setPurchasedNum(StringUtil.isEmpty(purchasedNum) ? 0 : Integer.parseInt(purchasedNum));
            //判断是否达到限购数量(普通商品详情已判断库存是否不足，所以这里不加库存判断)
            if (!StringUtil.isEmpty(purchasedNum)) {
                if (presell.getBuyLimit() > 0 && Integer.parseInt(purchasedNum) >= presell.getBuyLimit()) {
                    detailVO.setIsCanBuy(false);
                }
            }
        }
        return SldResponse.success(detailVO);
    }

}
