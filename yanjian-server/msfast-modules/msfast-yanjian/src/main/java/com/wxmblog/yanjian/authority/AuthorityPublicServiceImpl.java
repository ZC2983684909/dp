package com.wxmblog.yanjian.authority;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.authority.service.IAuthorityServiceImpl;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.auth.common.enums.LoginType;
import com.wxmblog.base.auth.common.rest.request.LoginRequest;
import com.wxmblog.base.common.entity.LoginUser;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.service.BaseCommonService;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.file.service.MsfFileService;
import com.wxmblog.base.role.common.rest.response.LoginResponse;
import com.wxmblog.yanjian.common.rest.request.front.user.UserRegisterRequest;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserEduAuditService;
import com.wxmblog.yanjian.service.UserIdcardAuditService;
import com.wxmblog.yanjian.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-16 18:05
 **/
@Service
@Slf4j
public class AuthorityPublicServiceImpl extends IAuthorityServiceImpl<LoginRequest, UserRegisterRequest> {

    @Autowired
    MsfFileService fileService;

    @Autowired
    WxAppletService wxAppletService;

    @Autowired
    TUserService tUserService;

    @Autowired
    BaseCommonService baseCommonService;

    @Autowired
    UserIdcardAuditService userIdcardAuditService;

    @Autowired
    UserEduAuditService userEduAuditService;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private UserProfileService userProfileService;


    public AuthorityPublicServiceImpl() {
        this.setLoginType(LoginType.WX_Public);
    }

    @Transactional
    @Override
    public LoginUser login(LoginRequest loginRequest) {

        LoginUser loginUser = new LoginUser();
        TUserEntity frUserEntity = this.tUserService.getFrUserByUnionId(loginRequest.getUnionId());

        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if (FrUserStatusEnum.LOGOFF.equals(frUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if (FrUserStatusEnum.DISABLE.equals(frUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_STATUS_ERROR_EXCEPTION);
        }

        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());

        userProfileEntity.setPublicOpenId(loginRequest.getOpenId());
        loginUser.setId(frUserEntity.getId());
        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(frUserEntity, loginResponse);
        loginUser.setInfo(loginResponse);
        userProfileService.updateById(userProfileEntity);
        return loginUser;
    }

    @Override
    @Transactional
    public void wxAppletRegister(UserRegisterRequest request) {
    }
}
