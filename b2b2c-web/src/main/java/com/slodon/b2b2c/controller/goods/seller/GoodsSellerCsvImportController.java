package com.slodon.b2b2c.controller.goods.seller;


import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.UploadConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.model.goods.GoodsSellerModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户后台商品管理
 *
 * @author spp
 */
@Api(tags = "seller-商品csv导入")
@RestController
@Slf4j
@RequestMapping("v3/goods/seller/goods")
public class GoodsSellerCsvImportController extends BaseController {

    @Resource
    private GoodsSellerModel goodsSellerModel;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("商品csv导入")
    @VendorLogger(option = "商品csv导入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "csv文件路径", required = true, paramType = "formData"),
            @ApiImplicitParam(name = "categoryId3", value = "3级分类ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "innerLabelId", value = "店铺分类ID", paramType = "query")
    })
    @PostMapping("csvImport")
    public JsonResult<Integer> goodsCsvImport(HttpServletRequest request, MultipartFile file, Integer categoryId3, Integer innerLabelId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(file, "csv文件不能为空");
        AssertUtil.notNullOrZero(categoryId3, "3级分类ID不能为空");

        goodsSellerModel.goodsCsvImport(vendor,file,categoryId3,innerLabelId);
        return SldResponse.success("商品csv导入成功");
    }

    @ApiOperation("商品csv模板下载")
    @GetMapping("csvDownload")
    public JsonResult templateDownload(HttpServletRequest request, HttpServletResponse response) {

        //直接返回一个地址;csvTemplate
        Map<String, String> map = new HashMap<>();
        map.put("csvTemplate", DomainUrlUtil.SLD_IMAGE_RESOURCES+ UploadConst.CSV_TEMPLATE_DOWNLOAD_PATH);
        return SldResponse.success(map);
    }


}
