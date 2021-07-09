package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.seller.example.StoreGradeExample;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.vo.seller.StoreGradeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "seller-店铺等级")
@RestController
@Slf4j
@RequestMapping("v3/seller/seller/storeGrade")
public class SellerGradeController extends BaseController {

    @Resource
    private StoreGradeModel storeGradeModel;

    @ApiOperation("店铺等级列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gradeName", value = "等级名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreGradeVO>> getList(HttpServletRequest request,
                                                    @RequestParam(value = "gradeName", required = false) String gradeName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreGradeExample storeGradeExample = new StoreGradeExample();
        storeGradeExample.setGradeNameLike(gradeName);
        storeGradeExample.setOrderBy("sort asc");
        storeGradeExample.setPager(pager);
        List<StoreGrade> storeGradeList = storeGradeModel.getStoreGradeList(storeGradeExample, pager);
        List<StoreGradeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeGradeList)) {
            for (StoreGrade storeGrade : storeGradeList) {
                StoreGradeVO vo = new StoreGradeVO(storeGrade);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, storeGradeExample.getPager()));
    }

}
