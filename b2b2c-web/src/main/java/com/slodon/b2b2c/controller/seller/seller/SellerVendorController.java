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

@Api(tags = "seller-?????????????????????")
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

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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
                //?????????????????????
                VendorRoles vendorRoles = vendorRolesModel.getVendorRolesByRolesId(vendor.getRolesId());
                vo.setRolesName(vendorRoles.getRolesName());
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, vendorExample.getPager()));
    }

    @ApiOperation("???????????????")
    @VendorLogger(option = "???????????????")
    @PostMapping("add")
    public JsonResult<Integer> addVendor(HttpServletRequest request, VendorAddDTO vendorAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //????????????????????????
        AssertUtil.isTrue(!vendorAddDTO.getVendorPassword().equals(vendorAddDTO.getConfirmPassword()), "??????????????????????????????");

        //??????vendor
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

        return SldResponse.success("????????????");
    }

    @ApiOperation("???????????????")
    @VendorLogger(option = "???????????????")
    @PostMapping("edit")
    public JsonResult<Integer> editVendor(HttpServletRequest request, VendorUpdateDTO vendorUpdateDTO) {
        String logMsg = "??????id:" + vendorUpdateDTO.getVendorId();
        Vendor user = UserUtil.getUser(request, Vendor.class);

        //??????vendorId???????????????
        Vendor vendor = vendorModel.getVendorByVendorId(vendorUpdateDTO.getVendorId());
        AssertUtil.isTrue(!user.getStoreId().equals(vendor.getStoreId()), "????????????");
        AssertUtil.isTrue(vendor.getIsStoreAdmin().equals(VendorConst.IS_STORE_ADMIN_1), "???????????????????????????");

        Vendor vendorUpdate = new Vendor();
        BeanUtils.copyProperties(vendorUpdateDTO, vendorUpdate);

        //???????????????????????????
        objectRedisTemplate.delete("seller-" + vendor.getVendorId());
        //????????????uuid???????????????redis???key
        String uuid = UUID.randomUUID().toString();
        //??????redis?????????????????????????????????????????????
        UserAuthority<Vendor> userAuthority = new UserAuthority<>();
        userAuthority.setT(vendor);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("seller-" + vendor.getVendorId(), uuid, userAuthority);

        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("????????????", logMsg);
    }

    @ApiOperation("???????????????")
    @VendorLogger(option = "???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "?????????id", required = true)
    })
    @PostMapping("del")
    public JsonResult<Integer> delVendor(HttpServletRequest request, @RequestParam("vendorId") Long vendorId) {
        String logMsg = "?????????id:" + vendorId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //??????vendorId?????????????????????
        Vendor vendorDB = vendorModel.getVendorByVendorId(vendorId);
        AssertUtil.isTrue(!vendor.getStoreId().equals(vendorDB.getStoreId()), "????????????");
        AssertUtil.isTrue(vendorDB.getIsStoreAdmin().equals(VendorConst.IS_STORE_ADMIN_1), "???????????????????????????");

        //???????????????????????????
        objectRedisTemplate.delete("seller-" + vendor.getVendorId());
        //????????????uuid???????????????redis???key
        String uuid = UUID.randomUUID().toString();
        //??????redis?????????????????????????????????????????????
        UserAuthority<Vendor> userAuthority = new UserAuthority<>();
        userAuthority.setT(vendor);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("seller-" + vendor.getVendorId(), uuid, userAuthority);

        vendorModel.deleteVendor(vendorId);
        return SldResponse.success("????????????", logMsg);
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPwd", value = "?????????", required = true),
            @ApiImplicitParam(name = "newPwd", value = "?????????", required = true),
            @ApiImplicitParam(name = "confirmPwd", value = "????????????", required = true)
    })
    @PostMapping("updatePwd")
    public JsonResult<Integer> updatePwd(HttpServletRequest request,
                                         @RequestParam("oldPwd") String oldPwd,
                                         @RequestParam("newPwd") String newPwd,
                                         @RequestParam("confirmPwd") String confirmPwd) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //??????vendorId???????????????
        Vendor vendorDb = vendorModel.getVendorByVendorId(vendor.getVendorId());

        AssertUtil.isTrue(!Md5.getMd5String(oldPwd).equals(vendorDb.getVendorPassword()), "????????????????????????");
        AssertUtil.isTrue(!newPwd.equals(confirmPwd), "?????????????????????????????????");
        Vendor vendorUpdate = new Vendor();
        vendorUpdate.setVendorId(vendor.getVendorId());
        vendorUpdate.setVendorPassword(Md5.getMd5String(newPwd));
        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("????????????");

    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "?????????", required = true),
            @ApiImplicitParam(name = "smsCode", value = "???????????????", required = true),
            @ApiImplicitParam(name = "newPwd", value = "?????????", required = true),
            @ApiImplicitParam(name = "confirmPwd", value = "????????????", required = true)
    })
    @PostMapping("retrievePwd")
    public JsonResult<Integer> retrievePwd(HttpServletRequest request,
                                           @RequestParam("mobile") String mobile,
                                           @RequestParam("smsCode") String smsCode,
                                           @RequestParam("newPwd") String newPwd,
                                           @RequestParam("confirmPwd") String confirmPwd) {

        //??????????????????????????????
        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorMobile(mobile);
        List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
        AssertUtil.isTrue(CollectionUtils.isEmpty(vendorList), "?????????????????????????????????");

        //?????????????????????
        String smsNumber = stringRedisTemplate.opsForValue().get(mobile);
        AssertUtil.isTrue(smsNumber == null
                || !smsNumber.equalsIgnoreCase(smsCode), "????????????????????????");

        AssertUtil.isTrue(!newPwd.equals(confirmPwd), "?????????????????????????????????");
        Vendor vendorUpdate = new Vendor();
        vendorUpdate.setVendorId(vendorList.get(0).getVendorId());
        vendorUpdate.setVendorPassword(Md5.getMd5String(newPwd));
        vendorModel.updateVendor(vendorUpdate);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????/???????????????")
    @VendorLogger(option = "??????/???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "?????????id", required = true),
            @ApiImplicitParam(name = "isFreeze", value = "??????/??????[true==??????,false==??????]", required = true)
    })
    @PostMapping("isFreeze")
    public JsonResult freeze(HttpServletRequest request, Long vendorId, Boolean isFreeze) {
        Vendor vendorUser = UserUtil.getUser(request, Vendor.class);

        Vendor vendorDb = vendorModel.getVendorByVendorId(vendorId);
        AssertUtil.isTrue(null != vendorDb && "1".equals(vendorDb.getIsStoreAdmin()), "?????????????????????????????????");

        Vendor vendorNew = new Vendor();
        vendorNew.setVendorId(vendorId);
        if (isFreeze) {
            AssertUtil.isTrue(vendorDb.getIsAllowLogin() == VendorConst.NOT_ALLOW_LOGIN, "?????????????????????");

            vendorNew.setIsAllowLogin(VendorConst.NOT_ALLOW_LOGIN);
            //???????????????????????????
            objectRedisTemplate.delete("seller-" + vendorDb.getVendorId());
            //????????????uuid???????????????redis???key
            String uuid = UUID.randomUUID().toString();
            //??????redis?????????????????????????????????????????????
            UserAuthority<Vendor> userAuthority = new UserAuthority<>();
            userAuthority.setT(vendorDb);
            userAuthority.addAuthority(null);
            objectRedisTemplate.opsForHash().put("seller-" + vendorDb.getVendorId(), uuid, userAuthority);
            vendorModel.updateVendor(vendorNew);
            return SldResponse.success("????????????");
        } else {
            AssertUtil.isTrue(vendorDb.getIsAllowLogin() == VendorConst.IS_ALLOW_LOGIN, "????????????????????????");

            vendorNew.setIsAllowLogin(VendorConst.IS_ALLOW_LOGIN);
            vendorModel.updateVendor(vendorNew);
            return SldResponse.success("????????????");
        }
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorId", value = "??????id", required = true),
            @ApiImplicitParam(name = "newPassword", value = "?????????", required = true),
            @ApiImplicitParam(name = "newPasswordCfm", value = "???????????????", required = true)
    })
    @PostMapping("resetPassword")
    public JsonResult resetPassword(HttpServletRequest request, Long vendorId, String newPassword, String newPasswordCfm) {
        AssertUtil.isTrue(!newPassword.equals(newPasswordCfm), "?????????????????????,?????????!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Vendor vendorNew = new Vendor();
        vendorNew.setVendorId(vendorId);
        vendorNew.setVendorPassword(Md5.getMd5String(newPassword));
        vendorModel.updateVendor(vendorNew);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @PostMapping("register")
    public JsonResult registerVendor(HttpServletRequest request, VendorRegisterDTO vendorRegisterDTO) throws Exception {
        //????????????????????????????????????
        VendorExample example = new VendorExample();
        example.setVendorMobile(vendorRegisterDTO.getVendorMobile());
        List<Vendor> vendorList = vendorModel.getVendorList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "?????????????????????");

        //????????????????????????
        String vendorPassword = vendorRegisterDTO.getVendorPassword();
        AssertUtil.isTrue(!vendorRegisterDTO.getConfirmPassword().equals(vendorPassword), "???????????????");

        //?????????????????????
        //???redis?????????
        String verifyNumber = stringRedisTemplate.opsForValue().get(vendorRegisterDTO.getVerifyKey());
        AssertUtil.isTrue(verifyNumber == null
                || !verifyNumber.equalsIgnoreCase(vendorRegisterDTO.getVerifyCode()), "????????????????????????");

        //?????????????????????
        String smsNumber = stringRedisTemplate.opsForValue().get(vendorRegisterDTO.getVendorMobile());
        AssertUtil.isTrue(smsNumber == null
                || !smsNumber.equalsIgnoreCase(vendorRegisterDTO.getSmsCode()), "????????????????????????");

        //????????????ip
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
            log.error("??????????????????", e);
            throw new MallException("????????????");
        }
        UrlBuilder urlBuilder = UrlBuilder.of(uri, Charset.defaultCharset()).setQuery(urlQuery);

        HttpResponse execute = new HttpRequest(urlBuilder).method(Method.POST).addHeaders(headers).execute();

        return JSONObject.parseObject(execute.body(), JsonResult.class);
    }

}
