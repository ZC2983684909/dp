package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.entity.RegisterDistributionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户推广
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:08:10
 */
@Mapper
public interface RegisterDistributionDao extends BaseMapper<RegisterDistributionEntity> {

    Long indirectCount(String userId);

    List<String> getRegisterDistribution(String userId);

    List<String> getRegisterDistributionPay(String userId);

    Long distributionCount(String userId);
}
