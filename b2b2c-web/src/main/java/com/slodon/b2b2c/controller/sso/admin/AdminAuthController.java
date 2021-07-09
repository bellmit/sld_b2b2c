package com.slodon.b2b2c.controller.sso.admin;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.constant.ExpireTimeConst;
import com.slodon.b2b2c.core.constant.WebConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.user.UserAuthority;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.system.AdminModel;
import com.slodon.b2b2c.model.system.SystemResourceModel;
import com.slodon.b2b2c.system.example.AdminExample;
import com.slodon.b2b2c.system.example.SystemResourceExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.SystemResource;
import com.slodon.b2b2c.util.JWTRSA256Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;
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

/**
 * 自定义Oauth2获取令牌接口
 */
@Api(tags = "admin登录")
@RestController
@RequestMapping("v3/adminLogin/oauth")
public class AdminAuthController {

    @Resource
    private AdminModel adminModel;
    @Resource
    private SystemResourceModel systemResourceModel;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "图形验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyKey", value = "图形验证码key", required = true, paramType = "query"),
            @ApiImplicitParam(name = "refresh_token", paramType = "query")
    })
    @PostMapping("token")
    public JsonResult doLogin(HttpServletRequest request, String username, String password, String verifyCode, String verifyKey, String refresh_token) {
        Admin admin;
        if (!StringUtil.isEmpty(refresh_token)) {
            //刷新token
            String adminId = JWTRSA256Util.validToken(refresh_token);
            admin = adminModel.getAdminByAdminId(Integer.parseInt(adminId));
        } else {
            //登录获取token
            String verifyNumber = stringRedisTemplate.opsForValue().get(verifyKey);
            AssertUtil.isTrue(verifyNumber == null || !verifyNumber.equalsIgnoreCase(verifyCode), "图形验证码不正确");

            // 登录验证
            AdminExample adminExample = new AdminExample();
            adminExample.setAdminName(username);
            List<Admin> adminList = adminModel.getAdminList(adminExample, null);
            AssertUtil.notEmpty(adminList, "用户名或密码错误");
            admin = adminList.get(0);
            AssertUtil.isTrue(!admin.getPassword().equals(Md5.getMd5String(password)), "用户名或密码错误！");
            AssertUtil.isTrue(!admin.getState().equals(AdminConst.ADMIN_STATE_NORM), "账号停用不能登录！");
        }


        //定义一个uuid，用做存储redis的key，并且设置到token中
        String uuid = UUID.randomUUID().toString();
        //生成access_token和refresh_token
        ModelMap modelMap = new ModelMap();
        modelMap.put("access_token", JWTRSA256Util.buildToken(admin.getAdminId().toString(), uuid, WebConst.WEB_IDENTIFY_ADMIN));
        modelMap.put("refresh_token", JWTRSA256Util.buildRefreshToken(admin.getAdminId().toString(), uuid));

        //修改最后登录时间
        Admin adminNew = new Admin();
        adminNew.setAdminId(admin.getAdminId());
        adminNew.setLoginTime(new Date());
        adminModel.updateAdmin(adminNew);

        //登录调用，查询权限信息，构造二级三级权限资源
        SystemResourceExample example = new SystemResourceExample();
        example.setGrade(AdminConst.RESOURCE_GRADE_2);
        example.setRoleId(admin.getRoleId());
        example.setOrderBy("resource_id asc");
        List<SystemResource> systemResourcesList = systemResourceModel.getSystemResourceList(example, null);
        systemResourcesList.forEach(systemResources -> {
            example.setGrade(AdminConst.RESOURCE_GRADE_3);
            example.setPid(systemResources.getResourceId());
            systemResources.setChildren(systemResourceModel.getSystemResourceList(example, null));
        });
        modelMap.put("resourceList", systemResourcesList);
        if (CollectionUtils.isEmpty(systemResourcesList)) {
            return SldResponse.unAuth();
        }

        //构造存储对象
        UserAuthority<Admin> userAuthority = new UserAuthority<>();
        userAuthority.setT(admin);
        //查询角色权限
        SystemResourceExample systemResourcesExample = new SystemResourceExample();
        systemResourcesExample.setGrade(AdminConst.RESOURCE_GRADE_4);//4级资源
        systemResourcesExample.setRoleId(admin.getRoleId());
        List<SystemResource> resourceList = systemResourceModel.getSystemResourceList(systemResourcesExample, null);
        resourceList.forEach(systemResources -> {
            userAuthority.addAuthority(systemResources.getUrl());
        });
        objectRedisTemplate.opsForHash().put("admin-" + admin.getAdminId().toString(), uuid, userAuthority);
        objectRedisTemplate.expire("admin-" + admin.getAdminId().toString(), ExpireTimeConst.EXPIRE_SECOND_1_HOUR + 5, TimeUnit.SECONDS);//用户信息比access_token多5秒，防止token未过期时提示未授权
        return SldResponse.success(modelMap);
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refresh_token", value = "刷新token", required = true, paramType = "query")
    })
    @PostMapping("logout")
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
