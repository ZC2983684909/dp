package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.ThreadUtil;
import com.wxmblog.yanjian.entity.UserAccountEntity;
import com.wxmblog.yanjian.entity.UserApplyEntity;
import com.wxmblog.yanjian.service.UserAccountService;
import com.wxmblog.yanjian.service.UserApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserApplyOrderDao;
import com.wxmblog.yanjian.entity.UserApplyOrderEntity;
import com.wxmblog.yanjian.service.UserApplyOrderService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service("userApplyOrderService")
public class UserApplyOrderServiceImpl extends ServiceImpl<UserApplyOrderDao, UserApplyOrderEntity> implements UserApplyOrderService {


    @Autowired
    MsfConfigService msfConfigService;


    @Transactional
    @RedissonLock(lockName = "applyAuditLock:", lockNameAppendField = "userId")
    @Override
    public void updateTimeOut(String applyId, String userId) {
        UserApplyService userApplyService = SpringUtils.getBean(UserApplyService.class);
        UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
        UserApplyEntity userApplyEntity = userApplyService.getById(applyId);
        if (userApplyEntity == null || !"1".equals(userApplyEntity.getStatus())) {
            return;
        }

        userApplyEntity.setStatus("4");
        userApplyService.updateById(userApplyEntity);
        //用户拒绝 返回金额
        if ("1".equals(userApplyEntity.getApplyWay())) {
            String applyAmount = msfConfigService.getValueByCode("applyAmount");
            BigDecimal price = new BigDecimal(applyAmount);
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(userApplyEntity.getUserId());
            userAccountEntity.setAmount(price);
            userAccountEntity.setSource("UserApplyEntity");
            userAccountEntity.setSourceId(userApplyEntity.getId());
            userAccountEntity.setRemarks("申请微信过期");
            userAccountService.save(userAccountEntity);
        }

    }
}
