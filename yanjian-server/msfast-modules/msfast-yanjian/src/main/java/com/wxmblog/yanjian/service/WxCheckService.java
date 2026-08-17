package com.wxmblog.yanjian.service;

import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.vo.WxCheckResultVo;

public interface WxCheckService {

    //场景枚举值（1 资料；2 评论；3 论坛；4 社交日志）
    ServiceR<WxCheckResultVo> msgSecCheck(String content,Integer scene,String code);
}
