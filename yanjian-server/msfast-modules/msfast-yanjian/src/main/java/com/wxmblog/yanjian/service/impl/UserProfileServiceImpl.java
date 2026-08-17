package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserProfileDao;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.UserProfileService;


@Service("userProfileService")
public class UserProfileServiceImpl extends ServiceImpl<UserProfileDao, UserProfileEntity> implements UserProfileService {

}
