package com.moon.tinybilibili.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.moon.tinybilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Chanmoey
 * @date 2022年07月07日
 */
public class TokenUtil {

    private TokenUtil(){}

    private static final String ISSUER = "Moon Moon";

    public static String generateToken(Long userId) throws Exception {

        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        return JWT.create()
                .withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.parseLong(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "token过期!");
        } catch (Exception e) {
            throw new ConditionException("非法用户Token!");
        }

    }
}
