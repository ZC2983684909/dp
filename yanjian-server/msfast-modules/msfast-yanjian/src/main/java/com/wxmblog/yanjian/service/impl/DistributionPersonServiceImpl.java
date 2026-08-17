package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.DistributionPersonDao;
import com.wxmblog.yanjian.entity.DistributionPersonEntity;
import com.wxmblog.yanjian.service.DistributionPersonService;


@Service("distributionPersonService")
public class DistributionPersonServiceImpl extends ServiceImpl<DistributionPersonDao, DistributionPersonEntity> implements DistributionPersonService {

}
