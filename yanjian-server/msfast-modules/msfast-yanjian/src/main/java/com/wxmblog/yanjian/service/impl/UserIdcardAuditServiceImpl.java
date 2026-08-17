package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserIdcardAuditDao;
import com.wxmblog.yanjian.entity.UserIdcardAuditEntity;
import com.wxmblog.yanjian.service.UserIdcardAuditService;


@Service("userIdcardAuditService")
public class UserIdcardAuditServiceImpl extends ServiceImpl<UserIdcardAuditDao, UserIdcardAuditEntity> implements UserIdcardAuditService {

}
