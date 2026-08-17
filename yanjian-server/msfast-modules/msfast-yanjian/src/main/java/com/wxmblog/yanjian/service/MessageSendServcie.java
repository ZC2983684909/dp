package com.wxmblog.yanjian.service;

import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import org.springframework.scheduling.annotation.Async;

public interface MessageSendServcie {

    @Async
    void sendMessage(SendUserMessageVo sendUserMessageVo);
}
