package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.entity.UserNatureVisitEntity;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-03 16:57:08
 */
public interface UserNatureVisitService extends IService<UserNatureVisitEntity> {

    void executeDeleteNatureVisit();
}

