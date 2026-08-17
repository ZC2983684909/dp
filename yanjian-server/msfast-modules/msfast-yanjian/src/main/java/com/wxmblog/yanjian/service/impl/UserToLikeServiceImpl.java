package com.wxmblog.yanjian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserToLikeDao;
import com.wxmblog.yanjian.entity.UserToLikeEntity;
import com.wxmblog.yanjian.service.UserToLikeService;
import org.springframework.stereotype.Service;


@Service("userToLikeService")
public class UserToLikeServiceImpl extends ServiceImpl<UserToLikeDao, UserToLikeEntity> implements UserToLikeService {

}
