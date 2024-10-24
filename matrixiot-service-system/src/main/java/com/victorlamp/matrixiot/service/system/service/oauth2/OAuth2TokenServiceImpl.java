package com.victorlamp.matrixiot.service.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.victorlamp.matrixiot.common.exception.enums.GlobalErrorCodeConstants;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.date.DateUtils;
import com.victorlamp.matrixiot.service.framework.tenant.core.context.TenantContextHolder;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2ClientService;
import com.victorlamp.matrixiot.service.system.api.oauth2.OAuth2TokenService;
import com.victorlamp.matrixiot.service.system.controller.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.victorlamp.matrixiot.service.system.dao.oauth2.OAuth2AccessTokenMapper;
import com.victorlamp.matrixiot.service.system.dao.oauth2.OAuth2RefreshTokenMapper;
import com.victorlamp.matrixiot.service.system.dao.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2AccessTokenDO;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2ClientDO;
import com.victorlamp.matrixiot.service.system.entity.oauth2.OAuth2RefreshTokenDO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception0;
import static com.victorlamp.matrixiot.common.util.collection.CollectionUtils.convertSet;

/**
 * OAuth2.0 Token Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    private static final JWSHeader jwsHeader = new JWSHeader
            .Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .build();
    private static final JWSSigner signer = new RSASSASigner(keyPair().getPrivate());
    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;
    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;
    @Resource
    private OAuth2ClientService oauth2ClientService;

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    @SneakyThrows
    private static String generateAccessToken(OAuth2AccessTokenDO accessTokenDO) {
        Date expiredTime = Date.from(accessTokenDO.getExpiresTime().toInstant(ZoneOffset.of("+08:00")));
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("userId", accessTokenDO.getUserId())
                .claim("userType", accessTokenDO.getUserType())
                .claim("tenantId", TenantContextHolder.getTenantId())
                .expirationTime(expiredTime).build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(signer);
        String token = jwsObject.serialize();

        log.debug("生成AccessToken[{}]:{}", token.length(), token);
        return token;
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

    @SneakyThrows
    private static String generateRefreshToken(OAuth2RefreshTokenDO refreshTokenDO) {
        Date expiredTime = Date.from(refreshTokenDO.getExpiresTime().toInstant(ZoneOffset.of("+08:00")));
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("userId", refreshTokenDO.getUserId())
                .claim("userType", refreshTokenDO.getUserType())
                .claim("tenantId", TenantContextHolder.getTenantId())
                .expirationTime(expiredTime).build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(signer);
        String token = jwsObject.serialize();

        log.debug("生成RefreshToken[{}]:{}", token.length(), token);
        return token;
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    private static KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "hviot_test".toCharArray());
        return factory.getKeyPair("jwt", "hviot_test".toCharArray());
    }

    @Override
    @Transactional(transactionManager = "mysqlTransactionManager")
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        // 查询访问令牌
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }

        // 校验 Client 匹配
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        if (ObjectUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 移除相关的访问令牌
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteBatchIds(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
        }

        // 已过期的情况下，删除刷新令牌
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }

        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // 获取不到，从 MySQL 中获取
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // 删除刷新令牌
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public PageResult<OAuth2AccessTokenDO> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO) {
        return oauth2AccessTokenMapper.selectPage(reqVO);
    }

    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO();
        accessTokenDO.setUserId(refreshTokenDO.getUserId());
        accessTokenDO.setUserType(refreshTokenDO.getUserType());
        accessTokenDO.setClientId(clientDO.getClientId());
        accessTokenDO.setScopes(refreshTokenDO.getScopes());
        accessTokenDO.setRefreshToken(refreshTokenDO.getRefreshToken());
        accessTokenDO.setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
        accessTokenDO.setTenantId(TenantContextHolder.getTenantId());

        accessTokenDO.setAccessToken(generateAccessToken(accessTokenDO));

        oauth2AccessTokenMapper.insert(accessTokenDO);

        oauth2AccessTokenRedisDAO.set(accessTokenDO);

        return accessTokenDO;

    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(Long userId, Integer userType, OAuth2ClientDO clientDO, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO()
                .setUserId(userId).setUserType(userType)
                .setClientId(clientDO.getClientId()).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));

        refreshToken.setRefreshToken(generateRefreshToken(refreshToken));

        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

}
