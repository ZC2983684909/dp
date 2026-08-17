package com.wxmblog.yanjian.service.impl;

import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.yanjian.common.rest.request.front.user.ComplaintRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.ComplaintDao;
import com.wxmblog.yanjian.entity.ComplaintEntity;
import com.wxmblog.yanjian.service.ComplaintService;
import org.springframework.transaction.annotation.Transactional;


@Service("complaintService")
public class ComplaintServiceImpl extends ServiceImpl<ComplaintDao, ComplaintEntity> implements ComplaintService {

    @Transactional
    @Override
    public void add(ComplaintRequest request) {

     /*   Wrapper<ComplaintEntity> queryWrapper = new QueryWrapper<ComplaintEntity>().lambda()
                .eq(ComplaintEntity::getUserId, TokenUtils.getOwnerId())
                .eq(ComplaintEntity::getComplaintId, request.getComplaintId())
                .eq(ComplaintEntity::getStatus, "1");

        if (this.count(queryWrapper) > 0) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("你已经投诉过了还在处理中");
        }*/
        ComplaintEntity entity = new ComplaintEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setUserId(TokenUtils.getOwnerId());
        entity.setStatus("1");
        this.save(entity);
    }
}
