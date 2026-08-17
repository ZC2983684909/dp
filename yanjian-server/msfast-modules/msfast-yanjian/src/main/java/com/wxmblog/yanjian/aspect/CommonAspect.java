package com.wxmblog.yanjian.aspect;

import com.wxmblog.base.common.service.ICommonAspect;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.service.TUserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonAspect implements ICommonAspect {


    @Autowired
    TUserService frUserService;

    @Override
    @Transactional
    public void afterReturning() {

        try {
            String userId = TokenUtils.getOwnerId();
            if (userId != null) {
                this.frUserService.updateLatelyTime(userId);
            }
        } catch (ExpiredJwtException ignored) {
        }

    }
}
