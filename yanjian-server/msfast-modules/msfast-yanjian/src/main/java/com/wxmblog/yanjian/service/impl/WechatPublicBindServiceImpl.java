package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.WechatPublicBindDao;
import com.wxmblog.yanjian.entity.WechatPublicBindEntity;
import com.wxmblog.yanjian.service.WechatPublicBindService;


@Service("wechatPublicBindService")
public class WechatPublicBindServiceImpl extends ServiceImpl<WechatPublicBindDao, WechatPublicBindEntity> implements WechatPublicBindService {

}
