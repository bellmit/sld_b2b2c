package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.LadderGroupGoodsModel;
import com.slodon.b2b2c.model.promotion.LadderGroupModel;
import com.slodon.b2b2c.model.promotion.LadderGroupOrderExtendModel;
import com.slodon.b2b2c.model.promotion.LadderGroupRuleModel;
import com.slodon.b2b2c.promotion.example.LadderGroupExample;
import com.slodon.b2b2c.promotion.example.LadderGroupGoodsExample;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.example.LadderGroupRuleExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import com.slodon.b2b2c.promotion.pojo.LadderGroupRule;
import com.slodon.b2b2c.vo.promotion.LadderGroupDetailVO;
import com.slodon.b2b2c.vo.promotion.LadderGroupOrderExtendVO;
import com.slodon.b2b2c.vo.promotion.LadderGroupProductVO;
import com.slodon.b2b2c.vo.promotion.LadderGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-?????????")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/ladder/group")
public class AdminLadderGroupController extends BaseController {

    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private LadderGroupGoodsModel ladderGroupGoodsModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private LadderGroupRuleModel ladderGroupRuleModel;
    @Resource
    private ProductModel productModel;

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "??????(1-????????????2-????????????3-????????????4-????????????5-?????????)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<LadderGroupVO>> list(HttpServletRequest request, String groupName, String goodsName, String storeName,
                                                  Date startTime, Date endTime, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        LadderGroupExample example = new LadderGroupExample();
        example.setGroupNameLike(groupName);
        example.setGoodsNameLike(goodsName);
        example.setStoreNameLike(storeName);
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

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "?????????id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<LadderGroupDetailVO> detail(HttpServletRequest request, Integer groupId) {
        AssertUtil.notNullOrZero(groupId, "?????????id????????????");

        LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(groupId);
        AssertUtil.notNull(ladderGroup, "????????????????????????????????????????????????");
        LadderGroupDetailVO detailVO = new LadderGroupDetailVO(ladderGroup);

        //???????????????????????????
        LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
        ruleExample.setGroupId(groupId);
        ruleExample.setOrderBy("rule_id asc");
        List<LadderGroupRule> ruleList = ladderGroupRuleModel.getLadderGroupRuleList(ruleExample, null);
        detailVO.setRuleList(ruleList);

        //?????????????????????????????????
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
                //??????sku??????
                Product product = productModel.getProductByProductId(ladderGroupGoods.getProductId());
                AssertUtil.notNull(product, "???????????????????????????????????????");
                productVO.setStock(product.getProductStock());
                productVOS.add(productVO);
            });
            goodsInfo.setProductList(productVOS);
        }
        detailVO.setGoodsInfo(goodsInfo);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "?????????id", required = true)
    })
    @OperationLogger(option = "???????????????")
    @PostMapping("del")
    public JsonResult delLadderGroup(HttpServletRequest request, Integer groupId) {
        ladderGroupModel.deleteLadderGroup(groupId, null);

        return SldResponse.success("????????????", "?????????id:" + groupId);
    }

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "?????????id", required = true)
    })
    @OperationLogger(option = "???????????????")
    @PostMapping("invalid")
    public JsonResult invalidLadderGroup(HttpServletRequest request, Integer groupId) {
        LadderGroup ladderGroup = new LadderGroup();
        ladderGroup.setGroupId(groupId);
        ladderGroup.setState(LadderGroupConst.STATE_3);
        ladderGroupModel.updateLadderGroup(ladderGroup);

        return SldResponse.success("????????????", "?????????id:" + groupId);
    }

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "???????????????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "??????????????????102-???????????????103-????????????", paramType = "query")
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
