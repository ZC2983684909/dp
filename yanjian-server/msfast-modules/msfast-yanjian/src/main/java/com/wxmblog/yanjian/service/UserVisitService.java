package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.common.rest.vo.AddNatureVisitVo;
import com.wxmblog.yanjian.entity.UserVisitEntity;
import org.springframework.scheduling.annotation.Async;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-21 22:16:00
 */
public interface UserVisitService extends IService<UserVisitEntity> {

    @Async
    void addVisit(String userId, String visitUserId);

    @Async
    void addNatureVisit(AddNatureVisitVo addNatureVisitVo);
}

