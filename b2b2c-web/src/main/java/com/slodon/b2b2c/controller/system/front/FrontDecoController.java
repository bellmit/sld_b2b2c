package com.slodon.b2b2c.controller.system.front;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.ResponseConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.decoration.DecoUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.system.PcFirstAdvModel;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.model.system.TplMobileDecoModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.system.example.TplMobileDecoExample;
import com.slodon.b2b2c.system.pojo.PcFirstAdv;
import com.slodon.b2b2c.system.pojo.TplMobileDeco;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(tags = "front-首页装修")
@RestController
@RequestMapping("v3/system/front/deco")
public class FrontDecoController {

    @Resource
    private TplMobileDecoModel tplMobileDecoModel;
    @Resource
    private SettingModel settingModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private PcFirstAdvModel pcFirstAdvModel;
    @Resource
    private StoreModel storeModel;

    @ApiOperation("移动端首页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "os", value = "当前设备标识 可选值：android，ios，h5，weixinXcx，alipayXcx", paramType = "query"),
            @ApiImplicitParam(name = "homeId", value = "首页id（预览时必传）", paramType = "query"),
            @ApiImplicitParam(name = "preview", value = "预览标识", paramType = "query")
    })
    @GetMapping("index")
    public JsonResult indexPage(String os, String homeId, String preview) {
        TplMobileDecoExample tplMobileDecoExample = new TplMobileDecoExample();

        if (!StringUtils.isEmpty(preview)) {
            if (DecoUtil.validatePreview(preview, homeId)) {
                tplMobileDecoExample.setDecoId(Integer.valueOf(homeId));
                tplMobileDecoExample.setStoreId(0L);
                tplMobileDecoExample.setType("home");
            } else {
                return SldResponse.unAuth();
            }
        } else {
            if (!StringUtil.isEmpty(os)) {
                switch (os) {
                    case "android":
                        tplMobileDecoExample.setAndroid(1);
                        break;
                    case "ios":
                        tplMobileDecoExample.setIos(1);
                        break;
                    case "h5":
                        tplMobileDecoExample.setH5(1);
                        break;
                    case "weixinXcx":
                        tplMobileDecoExample.setWeixinXcx(1);
                        break;
                    default:
                        break;
                }
            }
            tplMobileDecoExample.setStoreId(0L);
            tplMobileDecoExample.setType("home");
        }

        List<TplMobileDeco> list = tplMobileDecoModel.getTplMobileDecoList(tplMobileDecoExample, null);

        ModelMap modelMap = new ModelMap();
        if (!CollectionUtils.isEmpty(list)) {
            String data = list.get(0).getData();
            if (!StringUtil.isEmpty(data)) {
                //首页装修实例，更新统计数据
                String goodsRegex = "(\"goodsId\":)(\\d+)([\\s\\S]*?\"actualSales\":)(\\d+)";//正则匹配所有商品数据
                Pattern pattern = Pattern.compile(goodsRegex);
                Matcher matcher = pattern.matcher(data);

                while (matcher.find()) {
                    //获取商品id
                    Long goodsId = Long.valueOf(matcher.group(2));//1-固定字符，2-商品id，3-商品信息，4-销量
                    //查询商品信息
                    Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
                    int salesVolume = goods != null ? goods.getActualSales() + goods.getVirtualSales() : 0;//销量

                    //替换商品销量
                    String replaceRegex = "(\"goodsId\":)(" + goodsId + ")([\\s\\S]*?\"actualSales\":)(\\d+)";//替换当前商品销量用的正则
                    data = data.replaceAll(replaceRegex, "$1$2$3" + salesVolume);
                }
            }
            if (!StringUtil.isEmpty(os) && os.equals("weixinXcx")) {
                modelMap.put("siteName", settingModel.getSettingByName("basic_site_name").getValue());
                modelMap.put("xcxImage", DomainUrlUtil.SLD_IMAGE_RESOURCES + settingModel.getSettingByName("xcx_home_share_img").getValue());
            }
            modelMap.put("data", data);
            modelMap.put("showTip", list.get(0).getShowTip());
        }
        return SldResponse.success(modelMap);
    }


    @ApiOperation("移动端发现数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "os", value = "当前设备标识 可选值：android，ios，h5，weixinXcx，alipayXcx", paramType = "query")
    })
    @GetMapping("information")
    public JsonResult information(String os) {
        TplMobileDecoExample tplMobileDecoExample = new TplMobileDecoExample();

        if (!StringUtil.isEmpty(os)) {
            switch (os) {
                case "android":
                    tplMobileDecoExample.setAndroid(1);
                    break;
                case "ios":
                    tplMobileDecoExample.setIos(1);
                    break;
                case "h5":
                    tplMobileDecoExample.setH5(1);
                    break;
                case "weixinXcx":
                    tplMobileDecoExample.setWeixinXcx(1);
                    break;
                default:
                    break;
            }
        }
        tplMobileDecoExample.setStoreId(0L);
        tplMobileDecoExample.setType("information");


        List<TplMobileDeco> list = tplMobileDecoModel.getTplMobileDecoList(tplMobileDecoExample, null);

        ModelMap modelMap = new ModelMap();
        if (!CollectionUtils.isEmpty(list)) {
            String data = list.get(0).getData();
            if (!StringUtil.isEmpty(data)) {
                //首页装修实例，更新统计数据
                String goodsRegex = "(\"goodsId\":)(\\d+)([\\s\\S]*?\"actualSales\":)(\\d+)";//正则匹配所有商品数据
                Pattern pattern = Pattern.compile(goodsRegex);
                Matcher matcher = pattern.matcher(data);

                while (matcher.find()) {
                    //获取商品id
                    Long goodsId = Long.valueOf(matcher.group(2));//1-固定字符，2-商品id，3-商品信息，4-销量
                    //查询商品信息
                    Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
                    int salesVolume = goods != null ? goods.getActualSales() + goods.getVirtualSales() : 0;//销量

                    //替换商品销量
                    String replaceRegex = "(\"goodsId\":)(" + goodsId + ")([\\s\\S]*?\"actualSales\":)(\\d+)";//替换当前商品销量用的正则
                    data = data.replaceAll(replaceRegex, "$1$2$3" + salesVolume);
                }
            }
            if (!StringUtil.isEmpty(os) && os.equals("weixinXcx")) {
                modelMap.put("siteName", settingModel.getSettingByName("basic_site_name").getValue());
                modelMap.put("xcxImage", DomainUrlUtil.SLD_IMAGE_RESOURCES + settingModel.getSettingByName("xcx_home_share_img").getValue());
            }
            modelMap.put("data", data);
            modelMap.put("showTip", list.get(0).getShowTip());
        }
        return SldResponse.success(modelMap);
    }


    @ApiOperation("移动端活动或专题页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoId", value = "活动或专题id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "页面类型：topic==专题，activity==活动", required = true, paramType = "query"),
            @ApiImplicitParam(name = "preview", value = "预览标识", paramType = "query")
    })
    @GetMapping("special")
    public JsonResult special(Integer decoId, String type, String preview) {
        TplMobileDeco tplMobileDeco;
        if (!StringUtils.isEmpty(preview)) {
            if (DecoUtil.validatePreview(preview, decoId.toString())) {
                tplMobileDeco = tplMobileDecoModel.getTplMobileDecoByDecoId(decoId);
            } else {
                return SldResponse.unAuth();
            }
        } else {
            tplMobileDeco = tplMobileDecoModel.getTplMobileDecoByDecoId(decoId);
        }
        if (null != tplMobileDeco) {
            if (tplMobileDeco.getType().equals(type)) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", tplMobileDeco.getName());
                map.put("data", tplMobileDeco.getData());
                return SldResponse.success(map);
            }
        }
        return SldResponse.success();
    }

    @ApiOperation("移动端店铺首页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "os", value = "当前设备标识 可选值：android，ios，h5，weixinXcx，alipayXcx", paramType = "query"),
            @ApiImplicitParam(name = "homeId", value = "首页id（预览时必传）", paramType = "query"),
            @ApiImplicitParam(name = "preview", value = "预览标识", paramType = "query")
    })
    @GetMapping("storeIndex")
    public JsonResult storeIndex(String os, Long storeId, String homeId, String preview) {
        //查询店铺信息
        Store storeDb = storeModel.getStoreByStoreId(storeId);
        if (storeDb.getState().equals(StoreConst.STORE_STATE_CLOSE)) {
            JsonResult jsonResult = SldResponse.fail("店铺已关闭");
            jsonResult.setState(ResponseConst.STATE_STORE_CLOSE);
            return jsonResult;
        }
        TplMobileDecoExample tplMobileDecoExample = new TplMobileDecoExample();

        if (!StringUtils.isEmpty(preview)) {
            if (DecoUtil.validatePreview(preview, homeId)) {
                tplMobileDecoExample.setDecoId(Integer.valueOf(homeId));
                tplMobileDecoExample.setStoreId(storeId);
                tplMobileDecoExample.setType("home");
            } else {
                return SldResponse.unAuth();
            }
        } else {
            if (!StringUtil.isEmpty(os)) {
                switch (os) {
                    case "android":
                        tplMobileDecoExample.setAndroid(1);
                        break;
                    case "ios":
                        tplMobileDecoExample.setIos(1);
                        break;
                    case "h5":
                        tplMobileDecoExample.setH5(1);
                        break;
                    case "weixinXcx":
                        tplMobileDecoExample.setWeixinXcx(1);
                        break;
                    default:
                        break;
                }
            }
            tplMobileDecoExample.setStoreId(storeId);
            tplMobileDecoExample.setType("home");
        }

        List<TplMobileDeco> list = tplMobileDecoModel.getTplMobileDecoList(tplMobileDecoExample, null);

        ModelMap modelMap = new ModelMap();
        if (!CollectionUtils.isEmpty(list)) {
            String data = list.get(0).getData();
            if (!StringUtil.isEmpty(data)) {
                //首页装修实例，更新统计数据
                String goodsRegex = "(\"goodsId\":)(\\d+)([\\s\\S]*?\"actualSales\":)(\\d+)";//正则匹配所有商品数据
                Pattern pattern = Pattern.compile(goodsRegex);
                Matcher matcher = pattern.matcher(data);

                while (matcher.find()) {
                    //获取商品id
                    Long goodsId = Long.valueOf(matcher.group(2));//1-固定字符，2-商品id，3-商品信息，4-销量
                    //查询商品信息
                    Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
                    int salesVolume = goods != null ? goods.getActualSales() + goods.getVirtualSales() : 0;//销量

                    //替换商品销量
                    String replaceRegex = "(\"goodsId\":)(" + goodsId + ")([\\s\\S]*?\"actualSales\":)(\\d+)";//替换当前商品销量用的正则
                    data = data.replaceAll(replaceRegex, "$1$2$3" + salesVolume);
                }
            }
            if (!StringUtil.isEmpty(os) && os.equals("weixinXcx")) {
                modelMap.put("siteName", settingModel.getSettingByName("basic_site_name").getValue());
                modelMap.put("xcxImage", DomainUrlUtil.SLD_IMAGE_RESOURCES + settingModel.getSettingByName("xcx_home_share_img").getValue());
            }
            modelMap.put("data", data);
            modelMap.put("showTip", list.get(0).getShowTip());
        }
        return SldResponse.success(modelMap);
    }

    @ApiOperation("首页开屏图")
    @GetMapping("firstAdv")
    public JsonResult<PcFirstAdv> getFirstAdv() {
        PcFirstAdv pcFirstAdv = pcFirstAdvModel.getPcFirstAdvByAdvId(1);
        return SldResponse.success(pcFirstAdv);
    }
}
