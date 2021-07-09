package com.slodon.b2b2c.controller.promotion.front;

import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.vo.promotion.FrontSeckillDetailVO;
import com.slodon.b2b2c.vo.promotion.FrontSeckillGoodsVO;
import com.slodon.b2b2c.vo.promotion.FrontSeckillLabelVO;
import com.slodon.b2b2c.vo.promotion.FrontSeckillStageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "front-限时秒杀")
@RestController
@Slf4j
@RequestMapping("v3/promotion/front/seckill")
public class FrontSeckillController extends BaseController {

    @Resource
    private SeckillModel seckillModel;
    @Resource
    private SeckillLabelModel seckillLabelModel;
    @Resource
    private SeckillStageModel seckillStageModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;
    @Resource
    private SeckillStageProductBindMemberModel seckillStageProductBindMemberModel;

    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("秒杀标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", paramType = "query", required = true)
    })
    @GetMapping("getSeckillLabel")
    public JsonResult<PageVO<FrontSeckillLabelVO>> getSeckillLabel(HttpServletRequest request, Integer seckillId) {
        String isEnable = stringRedisTemplate.opsForValue().get("seckill_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("秒杀模块未开启");
        }

        AssertUtil.notNullOrZero(seckillId, "秒杀活动id不能为空");

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SeckillLabelExample example = new SeckillLabelExample();
        example.setIsShow(SeckillConst.IS_SHOW_YES);
        //查询当前秒杀活动的标签
        example.setSeckillId(seckillId);
        example.setEndTimeBefore(TimeUtil.getDayEndFormatYMDHMS(new Date()));
        example.setQueryTime("notNull");
        example.setOrderBy("sort asc, create_time desc");
        List<SeckillLabel> list = seckillLabelModel.getSeckillLabelList(example, pager);
        List<FrontSeckillLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckillLabel -> {
                vos.add(new FrontSeckillLabelVO(seckillLabel));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("秒杀场次列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", paramType = "query", required = true)
    })
    @GetMapping("getSeckillStage")
    public JsonResult<PageVO<FrontSeckillStageVO>> getSeckillStage(HttpServletRequest request, Integer seckillId) {
        String isEnable = stringRedisTemplate.opsForValue().get("seckill_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("秒杀模块未开启");
        }

        AssertUtil.notNullOrZero(seckillId, "秒杀活动id不能为空");
        List<FrontSeckillStageVO> vos = new ArrayList<>();
        //进行中 当前时间位于开始时间和结束时间之间
        SeckillStageExample example = new SeckillStageExample();
        example.setSeckillId(seckillId);
        example.setStartTimeBefore(new Date());
        example.setEndTimeAfter(new Date());
        List<SeckillStage> list = seckillStageModel.getSeckillStageList(example, null);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckillStage -> {
                FrontSeckillStageVO vo = new FrontSeckillStageVO(seckillStage);
                //查询秒杀商品列表
                SeckillStageProductExample productExample = new SeckillStageProductExample();
                productExample.setStageId(seckillStage.getStageId());
                productExample.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
                List<SeckillStageProduct> productList = seckillStageProductModel.getSeckillStageProductList(productExample, null);
                if (!CollectionUtils.isEmpty(productList)) {
                    //场次下有秒杀商品,加入集合
                    vos.add(vo);
                }
            });
        }

        //未开始 当前时间在活动开始时间之前
        SeckillStageExample notStartStageExample = new SeckillStageExample();
        notStartStageExample.setSeckillId(seckillId);
        notStartStageExample.setStartTimeAfter(new Date());
        Date endTime = TimeUtil.getDayEndFormatYMDHMS(new Date());
        notStartStageExample.setEndTimeBefore(endTime);
        notStartStageExample.setOrderBy("start_time ASC");
        List<SeckillStage> notStartList = seckillStageModel.getSeckillStageList(notStartStageExample, null);
        if (!CollectionUtils.isEmpty(notStartList)) {
            notStartList.forEach(seckillStage -> {
                FrontSeckillStageVO vo = new FrontSeckillStageVO(seckillStage);
                //查询秒杀商品列表
                SeckillStageProductExample productExample = new SeckillStageProductExample();
                productExample.setStageId(seckillStage.getStageId());
                productExample.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
                List<SeckillStageProduct> productList = seckillStageProductModel.getSeckillStageProductList(productExample, null);
                if (!CollectionUtils.isEmpty(productList)) {
                    //场次下有秒杀商品,加入集合
                    vos.add(vo);
                }
            });
        }
        return SldResponse.success(new PageVO<>(vos, null));
    }

    @ApiOperation("秒杀商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", paramType = "query"),
            @ApiImplicitParam(name = "labelId", value = "标签id,0 表示全部标签", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "stageId", value = "场次id,如果标签id为0则必填", paramType = "query")
    })
    @GetMapping("goodsList")
    public JsonResult<PageVO<FrontSeckillGoodsVO>> getSeckillGoodsList(HttpServletRequest request, Integer seckillId, Integer labelId, Integer stageId) {
        String isEnable = stringRedisTemplate.opsForValue().get("seckill_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("秒杀模块未开启");
        }

        AssertUtil.isTrue(StringUtil.isNullOrZero(labelId) && StringUtil.isNullOrZero(stageId), "全部标签下,场次stageId不能为空或0值");
        AssertUtil.isTrue(!"1".equals(stringRedisTemplate.opsForValue().get("seckill_is_enable")), "秒杀活动未开启");

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);
        List<FrontSeckillGoodsVO> vos = seckillModel.getSeckillGoodsList(pager, seckillId, labelId, stageId, member.getMemberId());
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("轮播图")
    @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", paramType = "query", required = true)
    @GetMapping("banner")
    public JsonResult getBanner(HttpServletRequest request, Integer seckillId) {
        String isEnable = stringRedisTemplate.opsForValue().get("seckill_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("秒杀模块未开启");
        }

        AssertUtil.notNullOrZero(seckillId, "秒杀活动id不能为空");
        List<String> list = new ArrayList<>();
        //查询进行中的秒杀活动

        SeckillExample example = new SeckillExample();
        example.setSeckillId(seckillId);
        List<Seckill> seckillList = seckillModel.getSeckillList(example, null);
        Map<String, Object> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(seckillList)) {
            map.put("banner", seckillList.get(0).getBanner());
        }
        return SldResponse.success(map);
    }

    @ApiOperation("设置/取消提醒")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stageProductId", value = "秒杀商品id", required = true, paramType = "query")
    })
    @PostMapping("isRemind")
    public JsonResult isRemind(HttpServletRequest request, Integer stageProductId) {
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.notNullOrZero(stageProductId, "秒杀商品id不能为空");
        Boolean notice = seckillStageProductBindMemberModel.editNotice(stageProductId, member.getMemberId(), member.getMemberName());
        return SldResponse.success(notice ? "设置提醒成功" : "取消提醒成功");
    }

    @ApiOperation("秒杀商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionId", value = "活动id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<FrontSeckillDetailVO> detail(HttpServletRequest request, Long productId, Integer promotionId) throws Exception {
        AssertUtil.notNullOrZero(productId, "秒杀货品id不能为空");
        AssertUtil.notNullOrZero(promotionId, "秒杀活动id不能为空");
        Member member = UserUtil.getUser(request, Member.class);

        //查询秒杀商品信息
        SeckillStageProductExample seckillStageProductExample = new SeckillStageProductExample();
        seckillStageProductExample.setSeckillId(promotionId);
        seckillStageProductExample.setProductId(productId);
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductModel.getSeckillStageProductList(seckillStageProductExample, null);
        AssertUtil.isTrue(CollectionUtils.isEmpty(seckillStageProductList), "获取秒杀商品信息为空");
        SeckillStageProduct seckillStageProduct = seckillStageProductList.get(0);
        //查询货品信息
        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "获取货品品信息为空，请重试");

        //登录后,未开始更改设置提醒
        Boolean notice = false;
        SeckillStageProductBindMemberExample example = new SeckillStageProductBindMemberExample();
        example.setMemberId(member.getMemberId());
        example.setStageProductId(seckillStageProduct.getStageProductId());
        List<SeckillStageProductBindMember> list = seckillStageProductBindMemberModel.getSeckillStageProductBindMemberList(example, null);
        if (!StringUtils.isEmpty(member.getMemberId()) && !CollectionUtils.isEmpty(list) && seckillStageProduct.getStartTime().after(new Date())) {
            //登录后,未开始的商品，更改设置提醒
            notice = true;
        }
        //货品库存读redis
        String stockStr = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productId);
        if (stockStr != null) {
            seckillStageProduct.setProductStock(Integer.parseInt(stockStr));
        }
        FrontSeckillDetailVO detailVO = new FrontSeckillDetailVO(seckillStageProduct);
        detailVO.setIsRemind(notice);
        detailVO.setSpecValues(product.getSpecValues());
        if (!StringUtils.isEmpty(detailVO.getEndTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            Date date1 = new Date();
            if (date1.before(detailVO.getStartTime())) {
                date = format.parse(format.format(detailVO.getStartTime()));
            } else {
                date = format.parse(format.format(detailVO.getEndTime()));
            }

            Date now = format.parse(format.format(new Date()));
            long second = (date.getTime() - now.getTime()) / 1000;
            detailVO.setDistanceEndTime(second);
        }
        return SldResponse.success(detailVO);
    }


}
