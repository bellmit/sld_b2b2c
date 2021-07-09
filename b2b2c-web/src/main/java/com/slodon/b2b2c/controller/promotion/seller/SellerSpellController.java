package com.slodon.b2b2c.controller.promotion.seller;

import com.alibaba.fastjson.JSONArray;
import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.dto.SpellAddDTO;
import com.slodon.b2b2c.promotion.dto.SpellUpdateDTO;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-拼团")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/spell")
public class SellerSpellController extends BaseController {

    @Resource
    private SpellModel spellModel;
    @Resource
    private SpellGoodsModel spellGoodsModel;
    @Resource
    private SpellLabelModel spellLabelModel;
    @Resource
    private SpellTeamModel spellTeamModel;
    @Resource
    private SpellTeamMemberModel spellTeamMemberModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("拼团列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-待发布；2-未开始；3-进行中；4-已失效；5-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SpellVO>> list(HttpServletRequest request, String spellName, Date startTime,
                                            Date endTime, Integer state) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellExample example = new SpellExample();
        example.setStoreId(vendor.getStoreId());
        example.setSpellNameLike(spellName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == SpellConst.STATE_1) {
                example.setState(SpellConst.ACTIVITY_STATE_1);
            } else if (state == SpellConst.STATE_2) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == SpellConst.STATE_3) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == SpellConst.STATE_4) {
                example.setState(SpellConst.ACTIVITY_STATE_3);
            } else if (state == SpellConst.STATE_5) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setEndTimeBefore(new Date());
            }
        }
        example.setStateNotEquals(SpellConst.ACTIVITY_STATE_4);
        List<Spell> list = spellModel.getSpellList(example, pager);
        List<SpellVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(spell -> {
                vos.add(new SpellVO(spell));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("拼团详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<SpellDetailVO> detail(HttpServletRequest request, Integer spellId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellId, "拼团id不能为空");

        Spell spell = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");
        AssertUtil.isTrue(!spell.getStoreId().equals(vendor.getStoreId()), "无权限");
        SpellDetailVO detailVO = new SpellDetailVO(spell);

        //查询拼团活动商品信息
        String fields = "goods_id, goods_name, goods_image";
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        example.setGroupBy("goods_id, goods_name, goods_image");
        example.setOrderBy("goods_id desc");
        List<SpellGoods> list = spellGoodsModel.getSpellGoodsListFieldsByExample(fields, example);
        List<SpellDetailVO.GoodsVO> goodsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellGoods spellGoods : list) {
                SpellDetailVO.GoodsVO goodsVO = new SpellDetailVO.GoodsVO(spellGoods);

                SpellGoodsExample goodsExample = new SpellGoodsExample();
                goodsExample.setSpellId(spellId);
                goodsExample.setGoodsId(spellGoods.getGoodsId());
                List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(goodsExample, null);
                List<SpellDetailVO.GoodsVO.SpellProductVO> productList = new ArrayList<>();
                for (SpellGoods goods : spellGoodsList) {
                    SpellDetailVO.GoodsVO.SpellProductVO productVO = new SpellDetailVO.GoodsVO.SpellProductVO(goods);
                    //查询sku库存
                    Product product = productModel.getProductByProductId(goods.getProductId());
                    AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                    productVO.setStock(product.getProductStock());
                    productList.add(productVO);
                }
                goodsVO.setProductList(productList);

                goodsList.add(goodsVO);
            }
        }
        detailVO.setGoodsList(goodsList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("拼团活动标签列表")
    @GetMapping("labelList")
    public JsonResult<List<SpellLabelVO>> labelList() {
        SpellLabelExample example = new SpellLabelExample();
        example.setIsShow(SpellConst.IS_SHOW_1);
        example.setOrderBy("sort asc, create_time desc");
        List<SpellLabel> list = spellLabelModel.getSpellLabelList(example, null);
        List<SpellLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(label -> {
                vos.add(new SpellLabelVO(label));
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("选择商品下的货品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品id集合，用逗号隔开", required = true, paramType = "query")
    })
    @GetMapping("productList")
    public JsonResult<List<SpellGoodsVO>> productList(String goodsIds) {
        AssertUtil.notEmpty(goodsIds, "商品id不能为空");
        AssertUtil.notFormatFrontIds(goodsIds, "商品ids格式错误,请重试");
        String[] split = goodsIds.split(",");
        List<SpellGoodsVO> vos = new ArrayList<>();
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            Long goodsId = Long.valueOf(s);
            // 查询商品信息
            Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
            AssertUtil.notNull(goods, "获取商品信息为空，请重试");

            // 查询货品信息
            ProductExample example = new ProductExample();
            example.setGoodsId(goodsId);
            List<Product> productList = productModel.getProductList(example, null);
            AssertUtil.notEmpty(productList, "获取商品下的货品信息为空，请重试");

            List<SpellProductVO> productVOS = new ArrayList<>();
            productList.forEach(product -> {
                productVOS.add(new SpellProductVO(product));
            });
            SpellGoodsVO vo = new SpellGoodsVO(goods);
            vo.setProductList(productVOS);
            vos.add(vo);
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("新建拼团")
    @VendorLogger(option = "新建拼团")
    @PostMapping("add")
    public JsonResult addSpell(HttpServletRequest request, SpellAddDTO spellAddDTO) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        AssertUtil.isTrue(StringUtil.isEmpty(spellAddDTO.getGoodsList()), "拼团商品信息不能为空");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        String goodsList = spellAddDTO.getGoodsList();
        List<SpellGoods> spellGoodsList = JSONArray.parseArray(goodsList, SpellGoods.class);

        Spell spell = new Spell();
        BeanUtils.copyProperties(spellAddDTO, spell);
        spell.setStoreId(vendor.getStoreId());
        spell.setStoreName(vendor.getStore().getStoreName());
        spell.setCreateVendorId(vendor.getVendorId());
        spellModel.saveSpell(spell, spellGoodsList);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑拼团")
    @VendorLogger(option = "编辑拼团")
    @PostMapping("update")
    public JsonResult updateSpell(HttpServletRequest request, SpellUpdateDTO spellUpdateDTO) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        AssertUtil.isTrue(StringUtil.isEmpty(spellUpdateDTO.getGoodsList()), "拼团商品信息不能为空");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        String goodsList = spellUpdateDTO.getGoodsList();
        List<SpellGoods> spellGoodsList = JSONArray.parseArray(goodsList, SpellGoods.class);

        Spell spell = new Spell();
        BeanUtils.copyProperties(spellUpdateDTO, spell);
        spell.setStoreId(vendor.getStoreId());
        spell.setStoreName(vendor.getStore().getStoreName());
        spellModel.updateSpell(spell, spellGoodsList);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除拼团")
    @VendorLogger(option = "删除拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团id", required = true)
    })
    @PostMapping("del")
    public JsonResult delSpell(HttpServletRequest request, Integer spellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellId, "拼团id不能为空");
        Spell spellBySpellId = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spellBySpellId, "获取的拼团信息为空,请重试");
        AssertUtil.isTrue(!spellBySpellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        spellModel.deleteSpell(spellId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("发布拼团")
    @VendorLogger(option = "发布拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishSpell(HttpServletRequest request, Integer spellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellId, "拼团id不能为空");
        Spell spellBySpellId = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spellBySpellId, "获取的拼团信息为空,请重试");
        AssertUtil.isTrue(!spellBySpellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        Spell spell = new Spell();
        spell.setSpellId(spellId);
        spell.setState(SpellConst.ACTIVITY_STATE_2);
        spellModel.updateSpell(spell);
        return SldResponse.success("发布成功");
    }

    @ApiOperation("失效拼团")
    @VendorLogger(option = "失效拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidSpell(HttpServletRequest request, Integer spellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellId, "拼团id不能为空");
        Spell spellBySpellId = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spellBySpellId, "获取的拼团信息为空,请重试");
        AssertUtil.isTrue(!spellBySpellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        Spell spell = new Spell();
        spell.setSpellId(spellId);
        spell.setState(SpellConst.ACTIVITY_STATE_3);
        spellModel.updateSpell(spell);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("复制拼团")
    @VendorLogger(option = "复制拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copySpell(HttpServletRequest request, Integer spellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        AssertUtil.notNullOrZero(spellId, "请选择要复制的数据!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Spell spell = new Spell();
        spell.setSpellId(spellId);
        spell.setStoreId(vendor.getStoreId());
        spellModel.copySpell(spell);
        return SldResponse.success("复制成功");
    }

    @ApiOperation("查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query")
    })
    @GetMapping("goodList")
    public JsonResult<PageVO<SpellGoodsListVO>> goodList(HttpServletRequest request, Integer spellId, String goodsName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        example.setGoodsNameLike(goodsName);
        List<SpellGoodsListVO> list = spellGoodsModel.getSpellGoodsList2(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("删除拼团活动商品")
    @VendorLogger(option = "删除拼团活动商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query")
    })
    @PostMapping("delGoods")
    public JsonResult delPreSellGoods(HttpServletRequest request, Integer spellId, Long goodsId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");
        AssertUtil.notNullOrZero(goodsId, "商品id不能为空");

        spellGoodsModel.deleteSpellGoodsByGoodsId(spellId, goodsId, vendor.getStoreId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("查看团队列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "拼团状态", paramType = "query")
    })
    @GetMapping("teamList")
    public JsonResult<PageVO<SpellTeamVO>> teamList(HttpServletRequest request, Integer spellId, Long goodsId, Integer state) {
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");
        //查询拼团活动
        Spell spell = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellTeamExample example = new SpellTeamExample();
        example.setSpellId(spellId);
        example.setGoodsId(goodsId);
        example.setState(state);
        List<SpellTeam> list = spellTeamModel.getSpellTeamList(example, pager);
        List<SpellTeamVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellTeam team : list) {
                SpellTeamVO teamVO = new SpellTeamVO(team);
                SpellTeamMemberExample memberExample = new SpellTeamMemberExample();
                memberExample.setSpellTeamId(team.getSpellTeamId());
                memberExample.setPayState(SpellConst.PAY_STATE_1);
                memberExample.setOrderBy("participate_time asc");
                List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(memberExample, null);
                List<SpellTeamMemberVO> memberVOS = new ArrayList<>();
                for (SpellTeamMember member : memberList) {
                    memberVOS.add(new SpellTeamMemberVO(member));
                }
                teamVO.setMemberList(memberVOS);
                //开启模拟成团并且拼团进行中的才可以模拟成团
                if (spell.getIsSimulateGroup() == SpellConst.IS_SIMULATE_GROUP_1
                        && team.getState() == SpellConst.SPELL_GROUP_STATE_1) {
                    teamVO.setIsSimulateGroup(SpellConst.IS_SIMULATE_GROUP_1);
                } else {
                    teamVO.setIsSimulateGroup(SpellConst.IS_SIMULATE_GROUP_0);
                }
                vos.add(teamVO);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("模拟成团")
    @VendorLogger(option = "模拟成团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellTeamId", value = "拼团活动团队id", required = true, paramType = "query")
    })
    @PostMapping("simulateGroup")
    public JsonResult simulateGroup(HttpServletRequest request, Integer spellTeamId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块已关闭，不能操作");
        }

        //参数校验
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(spellTeamId, "拼团活动团队id不能为空");
        SpellTeam team = spellTeamModel.getSpellTeamBySpellTeamId(spellTeamId);
        AssertUtil.notNull(team, "查询的拼团活动团队信息为空");
        Spell spell = spellModel.getSpellBySpellId(team.getSpellId());
        AssertUtil.notNull(spell, "查询的拼团活动信息为空");
        AssertUtil.isTrue(!spell.getStoreId().equals(vendor.getStoreId()), "无权限操作该拼团活动");
        AssertUtil.isTrue(spell.getIsSimulateGroup() != SpellConst.IS_SIMULATE_GROUP_1, "拼团活动:[" + spell.getSpellName() + "]未开启模拟成团");
        AssertUtil.isTrue(team.getState() != SpellConst.SPELL_GROUP_STATE_1, "只有正在进行中的拼团活动才可以模拟成团");
        AssertUtil.isTrue(team.getJoinedNum() >= spell.getRequiredNum(), "拼团活动团队已参团人数必须小于活动要求成团人数");

        //编辑拼团活动团队
        SpellTeam spellTeam = new SpellTeam();
        spellTeam.setSpellTeamId(spellTeamId);
        spellTeam.setState(SpellConst.SPELL_GROUP_STATE_2);
        spellTeam.setFinishType(SpellConst.FINISH_TYPE_2);
        spellTeam.setFinishTime(new Date());
        spellTeamModel.updateSpellTeam(spellTeam);
        return SldResponse.success("模拟成团成功");
    }
}
