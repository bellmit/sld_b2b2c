package com.slodon.b2b2c.controller.system.front;

import com.slodon.b2b2c.core.constant.ResponseConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.system.TplPcMallDataModel;
import com.slodon.b2b2c.model.system.TplPcMallDecoModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.system.example.TplPcMallDecoExample;
import com.slodon.b2b2c.system.pojo.TplPcMallData;
import com.slodon.b2b2c.system.pojo.TplPcMallDeco;
import io.swagger.annotations.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-pc首页装修")
@RestController
@RequestMapping("/v3/system/front/pcDeco")
public class FrontPcDecoController {

    @Resource
    private TplPcMallDecoModel tplPcMallDecoModel;
    @Resource
    private TplPcMallDataModel tplPcMallDataModel;
    @Resource
    private StoreModel storeModel;

    @ApiOperation("pc首页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoType", value = "装修页类型(默认index-首页装修，topic-专题装修)", paramType = "query"),
            @ApiImplicitParam(name = "decoId", value = "商城装修页面组装信息id(topic类型必传)", paramType = "query")
    })
    @ApiResponses(
            @ApiResponse(code = 200, message = "data==装修数据")
    )
    @GetMapping("index")
    public JsonResult indexPage(@RequestParam(value = "decoType", required = false, defaultValue = "index") String decoType,
                                @RequestParam(value = "decoId", required = false) Integer decoId) {
        TplPcMallDeco deco = null;
        TplPcMallDecoExample example = new TplPcMallDecoExample();
        example.setIsEnable(TplPcConst.IS_ENABLE_YES);
        if ("index".equals(decoType)) {
            example.setDecoType("index");
            example.setStoreId(0L);
            List<TplPcMallDeco> tplPcMallDecoList = tplPcMallDecoModel.getTplPcMallDecoList(example, null);
            if (!CollectionUtils.isEmpty(tplPcMallDecoList)) {
                deco = tplPcMallDecoList.get(0);
            }
        } else {
            deco = tplPcMallDecoModel.getTplPcMallDecoByDecoId(decoId);
        }

        if (deco == null) {
            return SldResponse.success();
        }

        List<TplPcMallData> dataList = new ArrayList<>();
        if (!StringUtil.isNullOrZero(deco.getMasterBannerId())) {
            TplPcMallData data = tplPcMallDataModel.getTplPcMallDataByDataId(deco.getMasterBannerId());
            dataList.add(data);
        }
        if (!StringUtils.isEmpty(deco.getRankedTplDataIds())) {
            for (String s : deco.getRankedTplDataIds().split(",")) {
                TplPcMallData data = tplPcMallDataModel.getTplPcMallDataByDataId(Integer.valueOf(s));
                dataList.add(data);
            }
        }
        return SldResponse.success(dataList);
    }

    @ApiOperation("pc店铺首页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query")
    })
    @ApiResponses(
            @ApiResponse(code = 200, message = "data==装修数据")
    )
    @GetMapping("sellerIndex")
    public JsonResult<List<TplPcMallData>> sellerIndexPage(Long storeId) {
        //查询店铺信息
        Store storeDb = storeModel.getStoreByStoreId(storeId);
        if (storeDb.getState().equals(StoreConst.STORE_STATE_CLOSE)) {
            JsonResult jsonResult = SldResponse.fail("店铺已关闭");
            jsonResult.setState(ResponseConst.STATE_STORE_CLOSE);
            return jsonResult;
        }
        List<TplPcMallData> dataList = new ArrayList<>();
        TplPcMallDecoExample example = new TplPcMallDecoExample();
        example.setStoreId(storeId);
        example.setIsEnable(TplPcConst.IS_ENABLE_YES);
        example.setDecoType("index");
        List<TplPcMallDeco> tplPcMallDecoList = tplPcMallDecoModel.getTplPcMallDecoList(example, null);
        if (!CollectionUtils.isEmpty(tplPcMallDecoList)) {
            TplPcMallDeco deco = tplPcMallDecoList.get(0);
            if (!StringUtil.isNullOrZero(deco.getMasterBannerId())) {
                TplPcMallData data = tplPcMallDataModel.getTplPcMallDataByDataId(deco.getMasterBannerId());
                dataList.add(data);
            }
            if (!StringUtils.isEmpty(deco.getRankedTplDataIds())) {
                for (String s : deco.getRankedTplDataIds().split(",")) {
                    TplPcMallData data = tplPcMallDataModel.getTplPcMallDataByDataId(Integer.valueOf(s));
                    dataList.add(data);
                }
            }
        }
        return SldResponse.success(dataList);
    }

}
