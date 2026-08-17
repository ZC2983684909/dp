package com.wxmblog.yanjian.authority;

import com.wxmblog.base.auth.authority.service.IAuthorityServiceImpl;
import com.wxmblog.base.auth.common.enums.LoginType;
import com.wxmblog.base.auth.common.rest.request.LoginRequest;
import com.wxmblog.base.auth.common.rest.request.RegisterRequest;
import org.springframework.stereotype.Service;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-16 18:05
 **/
@Service
public class AuthorityAdminServiceImpl extends IAuthorityServiceImpl<LoginRequest, RegisterRequest> {

    public AuthorityAdminServiceImpl() {
        this.setLoginType(LoginType.ADMIN);
    }
}
