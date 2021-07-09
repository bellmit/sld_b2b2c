package com.slodon.b2b2c.controller.sso.seller;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.user.UserAuthority;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.model.seller.VendorResourcesModel;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.example.VendorResourcesExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorResources;
import com.slodon.b2b2c.util.JWTRSA256Util;
import com.slodon.b2b2c.vo.seller.VendorLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(tags = "商户登录")
@RestController
@RequestMapping("v3/sellerLogin/oauth")
public class SellerAuthController {

    @Resource
    private VendorResourcesModel vendorResourcesModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "图形验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyKey", value = "图形验证码key", required = true, paramType = "query"),
            @ApiImplicitParam(name = "refresh_token", paramType = "query")
    })
    @PostMapping("token")
    public JsonResult<VendorLoginVO> doLogin(HttpServletRequest request, String username, String password, String verifyCode, String verifyKey, String refresh_token) {
        Vendor vendor;
        if (!StringUtil.isEmpty(refresh_token)) {
            //刷新token
            String vendorId = JWTRSA256Util.validToken(refresh_token);
            vendor = vendorModel.getVendorByVendorId(Long.parseLong(vendorId));
        } else {
            String verifyNumber = stringRedisTemplate.opsForValue().get(verifyKey);
            AssertUtil.isTrue(verifyNumber == null || !verifyNumber.equalsIgnoreCase(verifyCode), "图形验证码不正确");

            // 登录验证
            VendorExample vendorExample = new VendorExample();
            vendorExample.setVendorName(username);
            List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
            AssertUtil.notEmpty(vendorList, "用户名或密码错误");
            vendor = vendorList.get(0);
            AssertUtil.isTrue(!vendor.getVendorPassword().equals(Md5.getMd5String(password)), "用户名或密码错误！");
            if (vendor.getStoreId() != 0) {
                Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
                vendor.setStore(store);
            }
        }

        //定义一个uuid，用做存储redis的key，并且设置到token中
        String uuid = UUID.randomUUID().toString();
        //生成access_token和refresh_token
        VendorLoginVO vo = new VendorLoginVO();
        vo.setAccess_token(JWTRSA256Util.buildToken(vendor.getVendorId().toString(), uuid, WebConst.WEB_IDENTIFY_SELLER));
        vo.setRefresh_token(JWTRSA256Util.buildRefreshToken(vendor.getVendorId().toString(), uuid));
        vo.setVendorId(vendor.getVendorId());
        vo.setStoreId(vendor.getStoreId());
        vo.setIsStoreAdmin(vendor.getIsStoreAdmin());

        //构造存储对象
        UserAuthority<Vendor> userAuthority = new UserAuthority<>();
        userAuthority.setT(vendor);

        //修改最后登录时间
        Vendor vendorNew = new Vendor();
        vendorNew.setVendorId(vendor.getVendorId());
        vendorNew.setLatestLoginTime(new Date());
        vendorModel.updateVendor(vendorNew);

        //查询角色权限
        if (vendor.getStoreId() == 0L) {
            //未入驻的角色权限
            VendorResourcesExample vendorResourcesExample = new VendorResourcesExample();
            vendorResourcesExample.setGrade(VendorConst.RESOURCE_GRADE_4);//4级资源
            vendorResourcesExample.setPidIn("-1,-2");
            List<VendorResources> vendorResourcesList = vendorResourcesModel.getVendorResourcesList(vendorResourcesExample, null);
            vendorResourcesList.forEach(vendorResources -> {
                userAuthority.addAuthority(vendorResources.getUrl());
            });
        } else {
            //已入驻的角色权限
            VendorResourcesExample vendorResourcesExample = new VendorResourcesExample();
            vendorResourcesExample.setGrade(VendorConst.RESOURCE_GRADE_4);//4级资源
            vendorResourcesExample.setRoleId(vendor.getRolesId());
            vendorResourcesExample.setPidNotEquals(-2);
            List<VendorResources> vendorResourcesList = vendorResourcesModel.getVendorResourcesList(vendorResourcesExample, null);
            vendorResourcesList.forEach(vendorResources -> {
                userAuthority.addAuthority(vendorResources.getUrl());
            });
        }
        objectRedisTemplate.opsForHash().put("seller-" + vendor.getVendorId().toString(), uuid, userAuthority);
        objectRedisTemplate.expire("seller-" + vendor.getVendorId().toString(), ExpireTimeConst.EXPIRE_SECOND_1_HOUR + 5, TimeUnit.SECONDS);//用户信息比access_token多5秒，防止token未过期时提示未授权

        //登录调用，查询权限信息，构造二级三级权限资源
        if (vendor.getStoreId() == 0L) {
            //未入驻成功
            StoreApplyExample storeApplyExample = new StoreApplyExample();
            storeApplyExample.setVendorId(vendor.getVendorId());
            List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(storeApplyExample, null);
            Integer state;
            if (CollectionUtils.isEmpty(storeApplyList)) {
                state = 0;
            } else {
                state = storeApplyList.get(0).getState();
            }
            vo.setApplyState(state);
            if (state == 1) {
                vo.setMessage("您的入驻申请已提交等待审核");
            } else if (state == 3) {
                vo.setMessage("不符合入驻资格");
                vo.setReason(storeApplyList.get(0).getRefuseReason());
                vo.setRemark(storeApplyList.get(0).getAuditInfo());
            } else if (state == 0) {
                vo.setMessage("未入驻");
            }

            //查询角色权限资源
            VendorResourcesExample example = new VendorResourcesExample();
            example.setFrontPath("/apply");
            List<VendorResources> vendorResourcesList = vendorResourcesModel.getVendorResourceByFrontPath(example);
            vo.setResourceList(vendorResourcesList);
            return SldResponse.failSpecial(vo);
        } else {
            //已有店铺信息
            Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());
            if (storeDb.getState().equals(StoreConst.STORE_STATE_CLOSE)) {
                JsonResult<VendorLoginVO> jsonResult = SldResponse.fail("店铺已关闭");
                jsonResult.setState(ResponseConst.STATE_STORE_CLOSE);
                return jsonResult;
            }
            //登录调用，查询权限信息，构造二级三级权限资源
            VendorResourcesExample example = new VendorResourcesExample();
            example.setGrade(VendorConst.RESOURCE_GRADE_2);
            example.setFrontPathNotEquals("/apply");
            example.setRoleId(vendor.getRolesId());
            example.setOrderBy("resources_id asc");
            List<VendorResources> vendorResourcesList = vendorResourcesModel.getVendorResourcesList(example, null);
            vendorResourcesList.forEach(vendorResources -> {
                example.setGrade(VendorConst.RESOURCE_GRADE_3);
                example.setPid(vendorResources.getResourcesId());
                if (storeDb.getIsOwnStore() == StoreConst.IS_OWN_STORE) {
                    example.setFrontPathNotEquals("/store/info");
                }
                vendorResources.setChildren(vendorResourcesModel.getVendorResourcesList(example, null));
            });
            vo.setResourceList(vendorResourcesList);
            return SldResponse.success(vo);
        }
    }

    /**
     * 检测refreshToken是否有效
     *
     * @param refreshToken
     * @return refreshToken中存放用户信息的uuid
     */
    private String checkRefreshToken(String refreshToken) {
        AssertUtil.notEmpty(refreshToken, "未登录");
        try {
            //解析 refreshToken
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(refreshToken).getJWTClaimsSet();
            //获取其中的uuid
            String uuid = jwtClaimsSet.getStringClaim("uuid");
            //获取其中的user_id
            String userId = jwtClaimsSet.getStringClaim("user_id");
            //查询redis中是否有用户信息
            Object user = objectRedisTemplate.opsForHash().get(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);
            return user != null ? uuid : null;
        } catch (ParseException e) {
            return null;
        }
    }

    @ApiOperation("用户登出接口")
    @PostMapping("logout")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refresh_token", value = "刷新token", required = true, paramType = "query")
    })
    public JsonResult logout(HttpServletRequest request, String refresh_token) throws ParseException {


        //检测refresh_token,获取token中存放用户信息的uuid
        String uuid = this.checkRefreshToken(refresh_token);
        if (uuid == null) {
            //refresh_token已失效，不做处理
            return SldResponse.success("退出成功");
        }
        //解析 refreshToken
        JWTClaimsSet jwtClaimsSet = JWTParser.parse(refresh_token).getJWTClaimsSet();
        //获取其中的user_id
        String userId = jwtClaimsSet.getStringClaim("user_id");
        //清除redis中的用户信息
        objectRedisTemplate.opsForHash().delete(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);

        return SldResponse.success("退出成功");
    }

}
