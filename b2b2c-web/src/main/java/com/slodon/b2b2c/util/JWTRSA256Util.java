package com.slodon.b2b2c.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.slodon.b2b2c.core.constant.ExpireTimeConst;
import com.slodon.b2b2c.core.constant.ResponseConst;
import com.slodon.b2b2c.core.exception.MallException;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * 非对称加密 RSA256 算法工具
 **/
public class JWTRSA256Util {

    private static RSAKey rsaKey;
    private static RSAKey publicRsaKey;

    static {
        /**
         * 生成公钥，公钥是提供出去，让使用者校验token的签名
         */
        try {
            rsaKey = new RSAKeyGenerator(2048).generate();
            publicRsaKey = rsaKey.toPublicJWK();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成token
     *
     * @param userId
     * @return
     */
    public static String buildToken(String userId, String uuid, String webIdentify) {
        try {
            /**
             * 1. 生成秘钥,秘钥是token的签名方持有，不可对外泄漏
             */
            RSASSASigner rsassaSigner = new RSASSASigner(rsaKey);

            /**
             * 2. 建立payload 载体
             */
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .expirationTime(new Date(System.currentTimeMillis() + ExpireTimeConst.TOKEN_EXPIRE_TIME))
                    .claim("user_id", userId)
                    .claim("uuid", uuid)
                    .claim("webIdentify", webIdentify)
                    .build();

            /**
             * 3. 建立签名
             */
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
            signedJWT.sign(rsassaSigner);

            /**
             * 4. 生成token
             */
            String token = signedJWT.serialize();
            return token;
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成refresh_token
     *
     * @param userId
     * @return
     */
    public static String buildRefreshToken(String userId, String uuid) {
        try {
            /**
             * 1. 生成秘钥,秘钥是token的签名方持有，不可对外泄漏
             */
            RSASSASigner rsassaSigner = new RSASSASigner(rsaKey);

            /**
             * 2. 建立payload 载体
             */
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .expirationTime(new Date(System.currentTimeMillis() + ExpireTimeConst.REFRESH_TOKEN_EXPIRE_TIME))
                    .claim("user_id", userId)
                    .claim("uuid", uuid)
                    .build();

            /**
             * 3. 建立签名
             */
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
            signedJWT.sign(rsassaSigner);

            /**
             * 4. 生成token
             */
            String token = signedJWT.serialize();
            return token;
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static String validToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            //添加私密钥匙 进行解密
            RSASSAVerifier rsassaVerifier = new RSASSAVerifier(publicRsaKey);

            //校验是否有效
//            if (!jwt.verify(rsassaVerifier)) {
//                throw new MallException("Token 无效");
//            }

            //校验超时
            if (new Date().after(jwt.getJWTClaimsSet().getExpirationTime())) {
                throw new MallException("Token 已过期");
            }

            //获取载体中的数据
            Object userId = jwt.getJWTClaimsSet().getClaim("user_id");

            //是否有user_id
            if (Objects.isNull(userId)) {
                throw new MallException("请登录", ResponseConst.STATE_SPECIAL);
            }
            return userId.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return "";
    }

}
