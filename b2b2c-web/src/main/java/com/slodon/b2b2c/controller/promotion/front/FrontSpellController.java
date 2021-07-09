package com.slodon.b2b2c.controller.promotion.front;

import com.slodon.b2b2c.core.constant.RedisConst;
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
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.vo.promotion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "front-拼团")
@RestController
@Slf4j
@RequestMapping("v3/promotion/front/spell")
public class FrontSpellController extends BaseController {

    @Resource
    private SpellModel spellModel;
    @Resource
    private SpellLabelModel spellLabelModel;
    @Resource
    private SpellGoodsModel spellGoodsModel;
    @Resource
    private SpellTeamModel spellTeamModel;
    @Resource
    private SpellTeamMemberModel spellTeamMemberModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("拼团列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<FrontSpellVO> list(HttpServletRequest request, Integer labelId) {
        String isEnable = stringRedisTemplate.opsForValue().get("spell_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("拼团模块未开启");
        }

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询拼团标签
        SpellLabelExample example = new SpellLabelExample();
        example.setIsShow(SpellConst.IS_SHOW_1);
        example.setOnGoing("notNull");
        example.setOrderBy("sort asc, create_time desc");
        List<SpellLabel> labelList = spellLabelModel.getSpellLabelList(example, null);
        List<SpellLabelVO> labelVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(labelList)) {
            labelList.forEach(label -> {
                labelVOS.add(new SpellLabelVO(label));
            });
        }
        //查询该标签下已发布的活动
        SpellExample spellExample = new SpellExample();
        if (!StringUtil.isNullOrZero(labelId)) {
            spellExample.setSpellLabelId(labelId);
        }
        spellExample.setState(SpellConst.ACTIVITY_STATE_2);
        spellExample.setStartTimeBefore(new Date());
        spellExample.setEndTimeAfter(new Date());
        List<Spell> spellList = spellModel.getSpellList(spellExample, null);
        List<FrontSpellGoodsVO> goodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(spellList)) {
            //key为spellId，value为要求成团人数
            Map<Integer, Integer> spellMap = new HashMap<>();
            StringBuilder spellIdIn = new StringBuilder();
            for (Spell spell : spellList) {
                spellIdIn.append(",").append(spell.getSpellId());
                spellMap.put(spell.getSpellId(), spell.getRequiredNum());
            }
            SpellGoodsExample goodsExample = new SpellGoodsExample();
            goodsExample.setSpellIdIn(spellIdIn.substring(1));
            goodsVOS = spellGoodsModel.getFrontSpellGoodsList(goodsExample, pager, spellMap);
        }
        return SldResponse.success(new FrontSpellVO(labelVOS, goodsVOS, pager));
    }

    @ApiOperation("拼团商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionId", value = "活动id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<FrontSpellGoodsDetailVO> detail(HttpServletRequest request, Long productId, Integer promotionId) throws Exception {
        AssertUtil.notNullOrZero(productId, "拼团货品id不能为空");
        AssertUtil.notNullOrZero(promotionId, "拼团活动id不能为空");

        Member member = UserUtil.getUser(request, Member.class);

        //查询拼团活动绑定商品表sku
        SpellGoodsExample goodsExample = new SpellGoodsExample();
        goodsExample.setProductId(productId);
        goodsExample.setSpellId(promotionId);
        List<SpellGoods> goodsList = spellGoodsModel.getSpellGoodsList(goodsExample, null);
        AssertUtil.isTrue(CollectionUtils.isEmpty(goodsList), "查询的拼团活动绑定商品信息为空");
        SpellGoods spellGoods = goodsList.get(0);

        //如果该规格库存为0，查询其他规格
        if (spellGoods.getSpellStock() == 0) {
            SpellGoodsExample example = new SpellGoodsExample();
            example.setSpellId(promotionId);
            example.setGoodsId(spellGoods.getGoodsId());
            List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(example, null);
            for (SpellGoods goods : spellGoodsList) {
                //判断哪个规格有库存，展示有库存规格，否则默认原来的
                if (goods.getSpellStock() > 0) {
                    spellGoods = goods;
                    break;
                }
            }
        }

        //查询拼团活动spell
        Spell spell = spellModel.getSpellBySpellId(promotionId);
        AssertUtil.notNull(spell, "查询的拼团活动信息为空");
        FrontSpellGoodsDetailVO vo = new FrontSpellGoodsDetailVO(spellGoods, spell);

        //根据goodsId查询拼团团队
        SpellTeamExample teamExample = new SpellTeamExample();
        teamExample.setSpellId(promotionId);
        teamExample.setGoodsId(spellGoods.getGoodsId());
        List<SpellTeam> spellTeamList = spellTeamModel.getSpellTeamList(teamExample, null);
        if (!CollectionUtils.isEmpty(spellTeamList)) {
            vo.setSpellTeamNum(spellTeamList.get(0).getJoinedNum());
        }

        //赋值剩余时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date date1 = new Date();
        if (date1.before(vo.getStartTime())) {
            //未开始
            date = format.parse(format.format(vo.getStartTime()));
        } else {
            date = format.parse(format.format(vo.getEndTime()));
        }
        Date now = format.parse(format.format(new Date()));
        long second = (date.getTime() - now.getTime()) / 1000;
        vo.setDistanceEndTime(second);

        if (member.getMemberId() != null) {
            String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.SPELL_PURCHASED_NUM_PREFIX + spellGoods.getGoodsId() + "_" + member.getMemberId());
            vo.setPurchasedNum(StringUtil.isEmpty(purchasedNum) ? 0 : Integer.parseInt(purchasedNum));
            //判断是否达到限购数量(普通商品详情已判断库存是否不足，所以这里不加库存判断)
            if (!StringUtil.isEmpty(purchasedNum)) {
                if (spell.getBuyLimit() > 0 && Integer.parseInt(purchasedNum) >= spell.getBuyLimit()) {
                    vo.setIsCanBuy(false);
                }
            }
        }
        return SldResponse.success(vo);
    }

    @ApiOperation("拼团团队列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "拼团商品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query")
    })
    @GetMapping("teamList")
    public JsonResult<PageVO<SpellTeamVO>> teamList(HttpServletRequest request, Long goodsId, Integer spellId) throws Exception {
        AssertUtil.notNullOrZero(goodsId, "拼团商品id不能为空");
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");

        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询该商品全部待拼团队
        SpellTeamExample teamExample = new SpellTeamExample();
        teamExample.setGoodsId(goodsId);
        teamExample.setSpellId(spellId);
        teamExample.setState(SpellConst.SPELL_GROUP_STATE_1);
        teamExample.setLeaderPayState(SpellConst.PAY_STATE_1);
        teamExample.setOrderBy("spell_team_id asc");
        List<SpellTeam> list = spellTeamModel.getSpellTeamList(teamExample, pager);
        List<SpellTeamVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellTeam team : list) {
                SpellTeamVO vo = new SpellTeamVO(team);
                if (member.getMemberId() != null && member.getMemberId().equals(team.getLeaderMemberId())) {
                    vo.setIsSelf(true);
                }
                //查询拼团活动信息
                Spell spell = spellModel.getSpellBySpellId(team.getSpellId());
                AssertUtil.notNull(spell, "查询的拼团活动信息为空");
                vo.setRequiredNum(spell.getRequiredNum());
                vo.setMissingNum(spell.getRequiredNum() - team.getJoinedNum());

                //赋值剩余时间:精确到秒
                long time1 = team.getEndTime().getTime();
                long time2 = new Date().getTime();
                long distanceEndTime = (time1 - time2) / 1000;
                vo.setDistanceEndTime(distanceEndTime < 0 ? 0 : distanceEndTime);

                //查询团队成员信息
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setSpellTeamId(team.getSpellTeamId());
                teamMemberExample.setPayState(SpellConst.PAY_STATE_1);
                List<SpellTeamMember> teamMemberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
                List<SpellTeamMemberVO> teamMemberVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(teamMemberList)) {
                    teamMemberList.forEach(spellTeamMember -> {
                        teamMemberVOS.add(new SpellTeamMemberVO(spellTeamMember));
                    });
                }
                vo.setMemberList(teamMemberVOS);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
