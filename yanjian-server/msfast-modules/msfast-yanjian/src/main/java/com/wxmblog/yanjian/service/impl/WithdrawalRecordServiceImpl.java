package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.WithdrawalRecordDao;
import com.wxmblog.yanjian.entity.WithdrawalRecordEntity;
import com.wxmblog.yanjian.service.WithdrawalRecordService;


@Service("withdrawalRecordService")
public class WithdrawalRecordServiceImpl extends ServiceImpl<WithdrawalRecordDao, WithdrawalRecordEntity> implements WithdrawalRecordService {

}
