package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.PopupDao;
import com.wxmblog.yanjian.entity.PopupEntity;
import com.wxmblog.yanjian.service.PopupService;


@Service("popupService")
public class PopupServiceImpl extends ServiceImpl<PopupDao, PopupEntity> implements PopupService {

}
