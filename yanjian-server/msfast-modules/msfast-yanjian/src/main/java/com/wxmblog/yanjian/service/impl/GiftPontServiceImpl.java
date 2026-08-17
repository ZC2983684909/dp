package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.GiftPontDao;
import com.wxmblog.yanjian.entity.GiftPontEntity;
import com.wxmblog.yanjian.service.GiftPontService;


@Service("giftPontService")
public class GiftPontServiceImpl extends ServiceImpl<GiftPontDao, GiftPontEntity> implements GiftPontService {

}
