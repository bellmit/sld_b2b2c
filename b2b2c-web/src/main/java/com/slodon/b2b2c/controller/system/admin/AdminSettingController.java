package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.system.example.SettingExample;
import com.slodon.b2b2c.system.pojo.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(tags = "admin-配置管理")
@RestController
@RequestMapping("v3/system/admin/setting")
public class AdminSettingController extends BaseController {

    @Resource
    private SettingModel settingModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("配置同步redis")
    @GetMapping("settingInit")
    public JsonResult settingInit() {
        List<Setting> settingList = settingModel.getSettingList(new SettingExample(), null);
        AssertUtil.notEmpty(settingList, "配置信息不存在");
        settingList.forEach(setting -> {
            stringRedisTemplate.opsForValue().set(setting.getName(), setting.getValue());
        });
        return SldResponse.success("同步成功");
    }

    @ApiOperation("批量更新后台参数配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "要修改的name名称", paramType = "query"),
            @ApiImplicitParam(name = "value", value = "要修改的value值", paramType = "query")
    })
    @OperationLogger(option = "编辑后台参数配置")
    @PostMapping("updateSettingList")
    public JsonResult updateSettingList(HttpServletRequest request) {
        Map<String, String> postMap = WebUtil.handlerPostMap(request);
        if (null == postMap) {
            return SldResponse.badArgument();
        }

        for (Map.Entry<String, String> entry : postMap.entrySet()) {
            if (entry.getKey().length() == 0) {
                continue;
            }
            Setting setting = new Setting();
            setting.setName(entry.getKey());
            //优惠券过期提醒
            if (entry.getKey().equals("coupon_expired_reminder") && StringUtil.isEmpty(entry.getValue())) {
                setting.setValue("0");
            } else {
                setting.setValue(entry.getValue());
            }
            settingModel.updateSetting(setting);
            //活动开关关闭后，失效活动
            if ("seckill_is_enable".equals(entry.getKey()) && "0".equals(entry.getValue())) {
                promotionCommonModel.invalidPromotion(PromotionConst.PROMOTION_TYPE_104);
            } else if ("spell_is_enable".equals(entry.getKey()) && "0".equals(entry.getValue())) {
                promotionCommonModel.invalidPromotion(PromotionConst.PROMOTION_TYPE_102);
            } else if ("ladder_group_is_enable".equals(entry.getKey()) && "0".equals(entry.getValue())) {
                promotionCommonModel.invalidPromotion(PromotionConst.PROMOTION_TYPE_105);
            } else if ("presale_is_enable".equals(entry.getKey()) && "0".equals(entry.getValue())) {
                promotionCommonModel.invalidPromotion(PromotionConst.PROMOTION_TYPE_103);
            }
        }
        return SldResponse.success("更新成功");
    }

    @ApiOperation("单条更新后台参数配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "配置信息的键名称，字母数字及‘-’的组合，不能有空格", paramType = "query"),
            @ApiImplicitParam(name = "value", value = "配置信息的键值，不能有空格", paramType = "query")
    })
    @OperationLogger(option = "编辑后台参数配置")
    @PostMapping("updateSetting")
    public JsonResult updateSetting(Setting setting) {
        settingModel.updateSetting(setting);
        return SldResponse.success("更新成功");
    }

    @ApiOperation("获取配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "str", value = "要查询的键组合，以逗号分割", paramType = "query")
    })
    @GetMapping("getSettingList")
    public JsonResult<List<Setting>> getSettingList(String str) {
        AssertUtil.isTrue(StringUtils.isEmpty(str), "请输入正确的参数");

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取PC端支付方式及开启状态")
    @GetMapping("getPcPaymentList")
    public JsonResult getPcPayment() {
        String[] payment = {"alipay_is_enable_pc", "wxpay_is_enable_pc", "balance_pay_is_enable_pc"};
        List<Map> mapList = new LinkedList<>();

        for (String s : payment) {
            Setting setting = settingModel.getSettingByName(s);

            Map<String, Object> map = new LinkedHashMap<>();

            switch (s) {
                case "alipay_is_enable_pc":
                    map.put("payment", "支付宝支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "wxpay_is_enable_pc":
                    map.put("payment", "微信支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "balance_pay_is_enable_pc":
                    map.put("payment", "余额支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
            }
            mapList.add(map);
        }
        return SldResponse.success(mapList);
    }

    @ApiOperation("获取Mobile端支付方式及开启状态")
    @GetMapping("getMobilePayment")
    public JsonResult getMobilePayment() {
        String[] payment = {"alipay_is_enable_h5", "wxpay_is_enable_h5", "wxpay_is_enable_miniapp",
                "wxpay_is_enable_app", "balance_pay_is_enable_h5"};
        List<Map> mapList = new LinkedList<>();

        for (String s : payment) {
            Setting setting = settingModel.getSettingByName(s);

            Map<String, Object> map = new LinkedHashMap<>();

            switch (s) {
                case "alipay_is_enable_h5":
                    map.put("payment", "支付宝移动支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "wxpay_is_enable_h5":
                    map.put("payment", "微信h5支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "wxpay_is_enable_miniapp":
                    map.put("payment", "微信小程序支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "wxpay_is_enable_app":
                    map.put("payment", "微信app支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
                case "balance_pay_is_enable_h5":
                    map.put("payment", "余额移动支付");
                    map.put("name", setting.getName());
                    map.put("value", setting.getValue());
                    break;
            }
            mapList.add(map);
        }
        return SldResponse.success(mapList);
    }

    @ApiOperation("获取站点设置基本信息")
    @GetMapping("getBasicSiteSetting")
    public JsonResult<List<Setting>> getBasicSiteSetting() {

        String str = "basic_site_name,basic_site_icp,basic_site_copyright,basic_site_technical_support,basic_site_phone,basic_site_email,hot_search_words,seller_center_entrance_is_enable";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取积分换算比例")
    @GetMapping("getIntegralConversionRatio")
    public JsonResult<List<Setting>> getIntegralConversionRatio() {

        String str = "integral_conversion_ratio";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取支付宝PC支付配置信息")
    @GetMapping("getAliPayPCSetting")
    public JsonResult<List<Setting>> getAliPayPCSetting() {

        String str = "alipay_config_appid,alipay_config_partnerid,alipay_config_private_key,alipay_config_public_key";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取支付宝Mobile支付配置信息")
    @GetMapping("getAliPayMobileSetting")
    public JsonResult<List<Setting>> getAliPayMobileSetting() {

        String str = "alipay_config_appid,alipay_config_partnerid,alipay_config_private_key,alipay_config_public_key";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取微信PC支付配置信息")
    @GetMapping("getWxPayPCSetting")
    public JsonResult<List<Setting>> getWxPayPCSetting() {

        String str = "wxpay_pc_appid,wxpay_pc_apikey,wxpay_pc_merchantid,wxpay_pc_appsecret";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取微信H5支付配置信息")
    @GetMapping("getWxPayH5Setting")
    public JsonResult<List<Setting>> getWxPayH5Setting() {

        String str = "wxpay_h5_appid,wxpay_h5_apikey,wxpay_h5_merchantid,wxpay_h5_appsecret";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取微信MiniApp支付配置信息")
    @GetMapping("getWxPayMiniAppSetting")
    public JsonResult<List<Setting>> getWxPayMiniAppSetting() {

        String str = "wxpay_miniapp_appid,wxpay_miniapp_apikey,wxpay_miniapp_merchantid,wxpay_miniapp_appsecret";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取微信App支付配置信息")
    @GetMapping("getWxPayAppSetting")
    public JsonResult<List<Setting>> getWxPayAppSetting() {

        String str = "wxpay_app_appid,wxpay_app_apikey,wxpay_app_merchantid,wxpay_app_appsecret";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("PC端手机验证码类型")
    @GetMapping("getPcRandCode")
    public JsonResult<List<Setting>> getPcRandCode() {

        String str = "randCode_pc";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("mobile端手机验证码类型")
    @GetMapping("getMobileRandCode")
    public JsonResult<List<Setting>> getMobileRandCode() {

        String str = "randCode_mobile";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取PC默认图片")
    @GetMapping("getPcDefaultImage")
    public JsonResult<List<Setting>> getPcDefaultImage() {

        String str = "default_goods_image,default_image_store_logo,default_image_user_portrait,default_pc_mall_top_wx_image,default_pc_user_center_logo,default_image_store_banner_pc";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取PC基础图片")
    @GetMapping("getPcMainImage")
    public JsonResult<List<Setting>> getPcMainImage() {

        String str = "main_site_logo,main_user_center_logo,main_user_logon_bg,main_user_register_bg,pc_home_bottom_adv,main_user_forget_password_bg," +
                "main_user_register_logo,main_seller_center_logo,vendor_login_left_bg,vendor_login_bg," +
                "main_admin_top_logo,admin_login_left_bg,admin_login_bg,xcx_home_share_img";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取SMTP邮件配置信息")
    @GetMapping("getSMTPSetting")
    public JsonResult<List<Setting>> getSMTPSetting() {

        String str = "notification_email_sender_address,notification_email_sender_name,notification_email_sender_password,notification_email_smtp_host,notification_email_smtp_port,notification_email_test_address";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取SMS配置信息")
    @GetMapping("getSMSSetting")
    public JsonResult<List<Setting>> getSMSSetting() {

        String str = "notification_sms_key,notification_sms_provider,notification_sms_tpl_content_vc,notification_sms_signature";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取上传配置信息")
    @GetMapping("getUploadSetting")
    public JsonResult<List<Setting>> getUploadSetting() {
        String str = "img_location,qiniu_accessKey,qiniu_secretKey,qiniu_bucketName,sld_image_url";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取域名配置信息")
    @GetMapping("getDomainSetting")
    public JsonResult<List<Setting>> getDomainSetting() {
        String str = "sld_pc_url,sld_h5_url,sld_mobile_url,sld_seller_url,sld_cookie_domain,sld_cookie_name,sld_solr_url,sld_solr_coreName,sld_admin_url";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取微信互联登录信息")
    @GetMapping("getLoginWXSetting")
    public JsonResult<List<Setting>> getLoginWXSetting() {
        String str = "";

        if ("1".equals(settingModel.getSettingByName("module_is_enable_app").getValue())) {
            str += "login_wx_app_is_enable,login_wx_app_appid,login_wx_app_appsecret,";
        }
        if ("1".equals(settingModel.getSettingByName("module_is_enable_pc").getValue())) {
            str += "login_wx_pc_is_enable,login_wx_pc_appid,login_wx_pc_appsecret,";
        }
        if ("1".equals(settingModel.getSettingByName("module_is_enable_wx_xcx").getValue())) {
            str += "login_wx_mini_appid,login_wx_mini_appsecret,login_wx_dev_appid,login_wx_dev_appsecret";
        }
        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取平台收款账号信息")
    @GetMapping("getPaymentSetting")
    public JsonResult<List<Setting>> getPaymentSetting() {
        String str = "admin_bank_account_name,admin_bank_account_number,admin_bank_location,admin_bank_name";
        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取登录图片")
    @GetMapping("getLoginImage")
    public JsonResult<List<Setting>> getLoginImage() {
        String str = "admin_login_left_bg,admin_login_bg";
        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    /**
     * 获取Setting参数
     *
     * @param str 以逗号分割要查询的键值name
     * @return
     */
    private List<Setting> getSetting(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        //不需要返回描述信息的配置,直接往后追加
        List<String> emptyDes = Arrays.asList("refund_setting_switch", "coupon_is_enable");
        List<Setting> list = new LinkedList<>();
        String[] split = str.split(",");
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            Setting setting = settingModel.getSettingByName(s);
            if (setting == null) {
                continue;
            }
            //类型，1-字符串，2-图片，3-固定不能修改；4-开关配置, 如果是图片需要返回图片的完整连接地址
            if (2 == setting.getType()) {
                setting.setImageUrl(StringUtil.isEmpty(setting.getValue()) ? null : FileUrlUtil.getFileUrl(setting.getValue(), null));
            }
            if (emptyDes.contains(s)) {
                //不需要返回描述信息的配置
                setting.setDescription(null);
            }
            list.add(setting);
        }
        return list;
    }

    @ApiOperation("订单列表字段")
    @GetMapping("getOrderListCode")
    public JsonResult getOrderListCode() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getMapData().entrySet()) {
            if (entry.getKey().length() == 0 || entry.getValue().length() == 0) {
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }
        return SldResponse.success(map);
    }

    private Map<String, String> getMapData() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("orderSn", "订单号");
        map.put("orderState", "订单状态");
        map.put("orderAmount", "订单总额");
        map.put("expressFee", "运费");
        map.put("finishTime", "完成时间");
        map.put("memberId", "买家id");
        map.put("memberName", "买家名称");
        map.put("storeId", "店铺id");
        map.put("storeName", "店铺名称");
        map.put("createTime", "下单时间");
        map.put("payTime", "支付时间");
        map.put("paymentName", "支付方式");
        map.put("receiverName", "收货人姓名");
        map.put("receiverMobile", "收货人电话");
        map.put("receiverAreaInfo", "收货人地址");
        map.put("expressNumber", "发货单号");
        map.put("deliverName", "发货人姓名");
        map.put("deliverMobile", "发货人电话");
        map.put("deliverAreaInfo", "发货人地址");
        map.put("invoice", "发票信息");
        map.put("goodsName", "商品名称");
        map.put("specValues", "商品规格");
        map.put("goodsNum", "商品数量");
        map.put("goodsPrice", "商品单价(元)");
        return map;
    }

    @ApiOperation("会员列表字段")
    @GetMapping("getMemberListCode")
    public JsonResult getMemberListCode() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getMemberMapData().entrySet()) {
            if (entry.getKey().length() == 0 || entry.getValue().length() == 0) {
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }
        return SldResponse.success(map);
    }

    private Map<String, String> getMemberMapData() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("memberId", "会员id");
        map.put("memberName", "会员名称");
        map.put("memberNickName", "会员昵称");
        map.put("memberTrueName", "真实姓名");
        map.put("memberEmail", "会员邮箱");
        map.put("memberMobile", "手机号");
        map.put("memberAvatar", "会员头像");
        map.put("gender", "会员性别");
        map.put("registerTime", "会员注册时间");
        map.put("memberIntegral", "会员积分");
        map.put("balanceAvailable", "预存款可用金额");
        map.put("balanceFrozen", "预存款冻结金额");
//        map.put("", "是否允许举报(可以/不可以)");
        map.put("isAllowBuy", "会员是否有购买权限 开启/关闭");
        map.put("experienceValue", "会员经验值");
        return map;
    }

}
