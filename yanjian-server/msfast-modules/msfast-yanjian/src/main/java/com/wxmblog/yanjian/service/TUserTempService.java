package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.yanjian.entity.TUserTempEntity;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-28 22:47:49
 */
public interface TUserTempService extends IService<TUserTempEntity> {

    void saveUserTemp();
}

