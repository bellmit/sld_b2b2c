package com.slodon.b2b2c.controller.promotion.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.SeckillModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.promotion.dto.JoinSeckillDTO;
import com.slodon.b2b2c.promotion.example.SeckillExample;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.SellerSeckillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-秒杀活动")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/seckill")
public class SellerSeckillController extends BaseController {

    @Resource
    private SeckillModel seckillModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取秒杀活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态 1-未开始；2-进行中", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SellerSeckillVO>> getList(HttpServletRequest request,
                                                       @RequestParam(value = "seckillName", required = false) String seckillName,
                                                       @RequestParam(value = "startTime", required = false) Date startTime,
                                                       @RequestParam(value = "endTime", required = false) Date endTime,
                                                       @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        SeckillExample seckillExample = new SeckillExample();
        seckillExample.setSeckillNameLike(seckillName);
        seckillExample.setStartTimeBefore(endTime);
        seckillExample.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            switch (state) {
                case SeckillConst.SECKILL_STATE_1:
                    //未开始
                    seckillExample.setStartTimeAfter(new Date());
                    break;
                case SeckillConst.SECKILL_STATE_2:
                    //进行中
                    seckillExample.setStartTimeBefore(new Date());
                    seckillExample.setEndTimeAfter(new Date());
                    break;
            }
        } else {
            //未结束
            seckillExample.setEndTimeAfter(new Date());
        }
        List<Seckill> seckillList = seckillModel.getSeckillList(seckillExample, pager);
        List<SellerSeckillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(seckillList)) {
            for (Seckill seckill : seckillList) {
                SellerSeckillVO vo = new SellerSeckillVO(seckill);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("秒杀活动详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true)
    })
    @GetMapping("datail")
    public JsonResult<SellerSeckillVO> getDetail(HttpServletRequest request, @RequestParam("seckillId") Integer seckillId) {
        Seckill seckill = seckillModel.getSeckillBySeckillId(seckillId);
        AssertUtil.notNull(seckill, "秒杀活动不存在");

        SellerSeckillVO vo = new SellerSeckillVO(seckill);
        return SldResponse.success(vo);
    }

    @ApiOperation("参加秒杀活动")
    @VendorLogger(option = "参加秒杀活动")
    @PostMapping("joinSeckill")
    public JsonResult<Integer> joinSeckill(HttpServletRequest request, @RequestBody JoinSeckillDTO joinSeckillDTO) {
        String isEnable = stringRedisTemplate.opsForValue().get("seckill_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("秒杀模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.isTrue(CollectionUtils.isEmpty(joinSeckillDTO.getGoodsInfoList()), "请选择商品参与秒杀活动");
        if (!StringUtils.isEmpty(joinSeckillDTO.getEndTime())) {
            AssertUtil.isTrue(joinSeckillDTO.getEndTime().before(new Date()), "商品的秒杀结束时间应该大于当前时间");
        }

        for (JoinSeckillDTO.GoodsInfo goodsInfo : joinSeckillDTO.getGoodsInfoList()) {
            //判断商品状态是否是上架状态
            Goods goods = goodsModel.getGoodsByGoodsId(goodsInfo.getGoodsId());
            AssertUtil.notNull(goods, "未获取到商品信息");
            AssertUtil.isTrue(goods.getState() != GoodsConst.GOODS_STATE_UPPER, "商品名称为：" + goods.getGoodsName() + "的商品已失效");
            AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定");
            for (JoinSeckillDTO.ProductInfo productInfo : goodsInfo.getProductInfoList()) {
                //判断货品状态是否是正常状态
                Product product = productModel.getProductByProductId(productInfo.getProductId());
                AssertUtil.notNull(product, "未获取到货品信息");
                AssertUtil.isTrue(product.getState() != GoodsConst.PRODUCT_STATE_1, "商品名称为：" + goods.getGoodsName() + "的货品已锁定");
                AssertUtil.isTrue(productInfo.getSeckillStock() > product.getProductStock(), "商品名称为：" + goods.getGoodsName() + "的秒杀库存不能超过商品库存");
            }
        }
        seckillStageProductModel.saveSeckillStageProductByDto(joinSeckillDTO, vendor);

        return SldResponse.success("参加成功");
    }


}
