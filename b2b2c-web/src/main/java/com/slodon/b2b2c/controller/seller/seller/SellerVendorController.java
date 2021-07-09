package com.slodon.b2b2c.controller.seller.seller;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.BizTypeConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.user.UserAuthority;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.model.seller.VendorRolesModel;
import com.slodon.b2b2c.seller.dto.VendorAddDTO;
import com.slodon.b2b2c.seller.dto.VendorRegisterDTO;
import com.slodon.b2b2c.seller.dto.VendorUpdateDTO;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorRoles;
import com.slodon.b2b2c.vo.seller.StoreVendorVO;
import com.slodon.smartid.client.utils.SmartId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

@Api(tags = "seller-操作员管理接口")
@RestController
@RequestMapping("v3/seller/seller/vendor")
@Slf4j
public class SellerVendorController extends BaseController {

    @Resource
    private VendorModel vendorModel;
    @Resource
    private VendorRolesModel vendorRolesModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    @ApiOperation("操作员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorName", value = "账号名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreVendorVO>> getList(HttpServletRequest request,
                                                     @RequestParam(value = "vendorName", required = false) String vendorName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendorDb = UserUtil.getUser(request, Vendor.class);

        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorNameLike(vendorName);
        vendorExample.setStoreId(vendorDb.getStoreId());
        vendorExample.setPager(pager);
        List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, pager);
        List<StoreVendorVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vendorList)) {
            for (Vendor vendor : vendorList) {
                StoreVendorVO vo = new StoreVendorVO(vendor);
                //获取权限组名称
                VendorRoles vendorRoles = vendorRolesModel.getVendorRolesByRolesId(vendor.getRolesId());
                vo.setRolesName(vendorRoles.getRolesName());
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, vendorExample.getPager()));
    }

    @ApiOperation("添加操作员")
    @VendorLogger(option = "添加操作员")
    @PostMapping("add")
    public JsonResult<Integer> addVendor(HttpServletRequest request, VendorAddDTO vendorAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //验证密码是否一致
        AssertUtil.isTrue(!vendorAddDTO.getVendorPassword().equals(vendorAddDTO.getConfirmPassword()), "密码与确认密码不一致");

        //添加vendor
        Vendor vendorInsert = new Vendor();
        BeanUtils.copyProperties(vendorAddDTO, vendorInsert);
        vendorInsert.setVendorId(SmartId.nextId(BizTypeConst.VENDOR));
        vendorInsert.setStoreId(vendor.getStoreId());
        vendorInsert.setVendorPassword(Md5.getMd5String(vendorAddDTO.getVendorPassword()));
        vendorInsert.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_0);
        vendorInsert.setRegisterTime(new Date());
        vendorInsert.setLatestLoginTime(new Date());
        vendorInsert.setIsAllowLogin(VendorConst.IS_ALLOW_LOGIN);
        vendorInsert.setLatestLoginIp(WebUtil.getRealIp(request));
        vendorModel.saveVendor(vendorInsert);

        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑操作员")
    @VendorLogger(option = "编辑操作员")
    @PostMapping("edit")
    public JsonResult<Integer> editVendor(HttpServletRequest request, VendorUpdateDTO vendorUpdateDTO) {
        String logMsg = "商户id:" + vendorUpdateDTO.getVendorId();
        Vendor user = UserUtil.getUser(request, Vendor.class);

        //根据vendorId查询操作员
        Vendor vendor = vendorModel.getVendorByVendorId(vendorUpdateDTO.getVendorId());
        AssertUtil.isTrue(!user.getStoreId().equals(vendor.getStoreId()), "非法操作");
        AssertUtil.isTrue(vendor.getIsStoreAdmin().equals(VendorConst.IS_STORE_ADMIN_1), "不能编辑店铺管理员");

        Vendor vendorUpdate = new Vendor();
        BeanUtils.copyProperties(vendorUpdateDTO, vendorUpdate);

        //删除原来的资源权限
        objectRedisTemplate.delete("seller-" + vendor.getVendorId());
        //定义一个uuid，用做存储redis的key
        String uuid = UUID.randomUUID().toString();
        //获取redis中账号绑定的权限资源，进行清空
        UserAuthority<Vendor> userAuthority = new UserAuthority<>();
        userAuthority.setT(vendor);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("seller-" + vendor.getVendorId(), uuid, userAuthority);

        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除操作员")
    @VendorLogger(option = "删除操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "操作员id", required = true)
    })
    @PostMapping("del")
    public JsonResult<Integer> delVendor(HttpServletRequest request, @RequestParam("vendorId") Long vendorId) {
        String logMsg = "操作员id:" + vendorId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据vendorId获取操作员信息
        Vendor vendorDB = vendorModel.getVendorByVendorId(vendorId);
        AssertUtil.isTrue(!vendor.getStoreId().equals(vendorDB.getStoreId()), "非法操作");
        AssertUtil.isTrue(vendorDB.getIsStoreAdmin().equals(VendorConst.IS_STORE_ADMIN_1), "不能删除店铺管理员");

        //删除原来的资源权限
        objectRedisTemplate.delete("seller-" + vendor.getVendorId());
        //定义一个uuid，用做存储redis的key
        String uuid = UUID.randomUUID().toString();
        //获取redis中账号绑定的权限资源，进行清空
        UserAuthority<Vendor> userAuthority = new UserAuthority<>();
        userAuthority.setT(vendor);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("seller-" + vendor.getVendorId(), uuid, userAuthority);

        vendorModel.deleteVendor(vendorId);
        return SldResponse.success("删除成功", logMsg);
    }

    @ApiOperation("修改密码")
    @VendorLogger(option = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPwd", value = "原密码", required = true),
            @ApiImplicitParam(name = "newPwd", value = "新密码", required = true),
            @ApiImplicitParam(name = "confirmPwd", value = "确认密码", required = true)
    })
    @PostMapping("updatePwd")
    public JsonResult<Integer> updatePwd(HttpServletRequest request,
                                         @RequestParam("oldPwd") String oldPwd,
                                         @RequestParam("newPwd") String newPwd,
                                         @RequestParam("confirmPwd") String confirmPwd) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据vendorId查询数据库
        Vendor vendorDb = vendorModel.getVendorByVendorId(vendor.getVendorId());

        AssertUtil.isTrue(!Md5.getMd5String(oldPwd).equals(vendorDb.getVendorPassword()), "输入原密码不正确");
        AssertUtil.isTrue(!newPwd.equals(confirmPwd), "新密码与确认密码不一致");
        Vendor vendorUpdate = new Vendor();
        vendorUpdate.setVendorId(vendor.getVendorId());
        vendorUpdate.setVendorPassword(Md5.getMd5String(newPwd));
        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("修改成功");

    }

    @ApiOperation("找回密码")
    @VendorLogger(option = "找回密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", required = true),
            @ApiImplicitParam(name = "newPwd", value = "新密码", required = true),
            @ApiImplicitParam(name = "confirmPwd", value = "确认密码", required = true)
    })
    @PostMapping("retrievePwd")
    public JsonResult<Integer> retrievePwd(HttpServletRequest request,
                                           @RequestParam("mobile") String mobile,
                                           @RequestParam("smsCode") String smsCode,
                                           @RequestParam("newPwd") String newPwd,
                                           @RequestParam("confirmPwd") String confirmPwd) {

        //根据手机号查询数据库
        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorMobile(mobile);
        List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
        AssertUtil.isTrue(CollectionUtils.isEmpty(vendorList), "对不起，该手机号未注册");

        //校验短信验证码
        String smsNumber = stringRedisTemplate.opsForValue().get(mobile);
        AssertUtil.isTrue(smsNumber == null
                || !smsNumber.equalsIgnoreCase(smsCode), "短信验证码不正确");

        AssertUtil.isTrue(!newPwd.equals(confirmPwd), "新密码与确认密码不一致");
        Vendor vendorUpdate = new Vendor();
        vendorUpdate.setVendorId(vendorList.get(0).getVendorId());
        vendorUpdate.setVendorPassword(Md5.getMd5String(newPwd));
        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("冻结/解冻操作员")
    @VendorLogger(option = "冻结/解冻操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "操作员id", required = true),
            @ApiImplicitParam(name = "isFreeze", value = "冻结/解冻[true==冻结,false==解冻]", required = true)
    })
    @PostMapping("isFreeze")
    public JsonResult freeze(HttpServletRequest request, Long vendorId, Boolean isFreeze) {
        Vendor vendorUser = UserUtil.getUser(request, Vendor.class);

        Vendor vendorDb = vendorModel.getVendorByVendorId(vendorId);
        AssertUtil.isTrue(null != vendorDb && "1".equals(vendorDb.getIsStoreAdmin()), "店铺管理员账号不可更改");

        Vendor vendorNew = new Vendor();
        vendorNew.setVendorId(vendorId);
        if (isFreeze) {
            AssertUtil.isTrue(vendorDb.getIsAllowLogin() == VendorConst.NOT_ALLOW_LOGIN, "该账号已被冻结");

            vendorNew.setIsAllowLogin(VendorConst.NOT_ALLOW_LOGIN);
            //删除原来的资源权限
            objectRedisTemplate.delete("seller-" + vendorDb.getVendorId());
            //定义一个uuid，用做存储redis的key
            String uuid = UUID.randomUUID().toString();
            //获取redis中账号绑定的权限资源，进行清空
            UserAuthority<Vendor> userAuthority = new UserAuthority<>();
            userAuthority.setT(vendorDb);
            userAuthority.addAuthority(null);
            objectRedisTemplate.opsForHash().put("seller-" + vendorDb.getVendorId(), uuid, userAuthority);
            vendorModel.updateVendor(vendorNew);
            return SldResponse.success("冻结成功");
        } else {
            AssertUtil.isTrue(vendorDb.getIsAllowLogin() == VendorConst.IS_ALLOW_LOGIN, "该账号非冻结状态");

            vendorNew.setIsAllowLogin(VendorConst.IS_ALLOW_LOGIN);
            vendorModel.updateVendor(vendorNew);
            return SldResponse.success("解冻成功");
        }
    }

    @ApiOperation("重置密码")
    @VendorLogger(option = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "商户id", required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true),
            @ApiImplicitParam(name = "newPasswordCfm", value = "确认新密码", required = true)
    })
    @PostMapping("resetPassword")
    public JsonResult resetPassword(HttpServletRequest request, Long vendorId, String newPassword, String newPasswordCfm) {
        AssertUtil.isTrue(!newPassword.equals(newPasswordCfm), "密码输入不一致,请重试!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Vendor vendorNew = new Vendor();
        vendorNew.setVendorId(vendorId);
        vendorNew.setVendorPassword(Md5.getMd5String(newPassword));
        vendorModel.updateVendor(vendorNew);
        return SldResponse.success("重置成功");
    }

    @ApiOperation("商户账号注册")
    @PostMapping("register")
    public JsonResult registerVendor(HttpServletRequest request, VendorRegisterDTO vendorRegisterDTO) throws Exception {
        //查询该手机号是否已注册过
        VendorExample example = new VendorExample();
        example.setVendorMobile(vendorRegisterDTO.getVendorMobile());
        List<Vendor> vendorList = vendorModel.getVendorList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "该手机号已注册");

        //确认密码是否一致
        String vendorPassword = vendorRegisterDTO.getVendorPassword();
        AssertUtil.isTrue(!vendorRegisterDTO.getConfirmPassword().equals(vendorPassword), "密码不一致");

        //获取图像验证码
        //从redis中获取
        String verifyNumber = stringRedisTemplate.opsForValue().get(vendorRegisterDTO.getVerifyKey());
        AssertUtil.isTrue(verifyNumber == null
                || !verifyNumber.equalsIgnoreCase(vendorRegisterDTO.getVerifyCode()), "图形验证码不正确");

        //校验短信验证码
        String smsNumber = stringRedisTemplate.opsForValue().get(vendorRegisterDTO.getVendorMobile());
        AssertUtil.isTrue(smsNumber == null
                || !smsNumber.equalsIgnoreCase(vendorRegisterDTO.getSmsCode()), "短信验证码不正确");

        //获取登陆ip
        String ip = WebUtil.getRealIp(request);
        vendorRegisterDTO.setIp(ip);
        vendorModel.registerVendor(vendorRegisterDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic c2VsbGVyOnNlbGxlcg==");
        headers.put("name", null);

        UrlQuery urlQuery = new UrlQuery();
        urlQuery.add("username", vendorRegisterDTO.getVendorName());
        urlQuery.add("password", vendorRegisterDTO.getVendorPassword());
        urlQuery.add("verifyKey", vendorRegisterDTO.getVerifyKey());
        urlQuery.add("verifyCode", verifyNumber);

        URI uri;
        try {
            uri = new URI(DomainUrlUtil.SLD_API_URL + "/v3/sellerLogin/oauth/token");
        } catch (URISyntaxException e) {
            log.error("登录地址有误", e);
            throw new MallException("登录失败");
        }
        UrlBuilder urlBuilder = UrlBuilder.of(uri, Charset.defaultCharset()).setQuery(urlQuery);

        HttpResponse execute = new HttpRequest(urlBuilder).method(Method.POST).addHeaders(headers).execute();

        return JSONObject.parseObject(execute.body(), JsonResult.class);
    }

}
