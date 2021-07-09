package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsLabel;
import com.slodon.b2b2c.model.goods.GoodsLabelModel;
import com.slodon.b2b2c.vo.goods.GoodsLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-商品标签")
@RestController
@RequestMapping("v3/goods/seller/goodsLabel")
public class GoodsSellerLabelController extends BaseController {

    @Resource
    private GoodsLabelModel goodsLabelModel;

    @ApiOperation("商品标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsLabelVO>> getList(HttpServletRequest request, String labelName) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据标签名称查询商品标签集合
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelNameLike(labelName);
        example.setOrderBy("sort asc, create_time desc");
        List<GoodsLabel> list = goodsLabelModel.getGoodsLabelList(example, pager);

        ArrayList<GoodsLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsLabel -> {
                GoodsLabelVO vo = new GoodsLabelVO(goodsLabel);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
