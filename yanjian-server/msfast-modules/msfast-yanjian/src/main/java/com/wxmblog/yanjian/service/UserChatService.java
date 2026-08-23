package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.user.ApplyWxRequest;
import com.wxmblog.yanjian.common.rest.response.front.chat.ApplyChatPreResponse;
import com.wxmblog.yanjian.entity.UserChatEntity;


/**
 * 用户聊天解锁
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-01 17:38:50
 */
public interface UserChatService extends IService<UserChatEntity> {

    ServiceR<ApplyChatPreResponse> chatPre(String userId);

    ServiceR<Void> applyChat(ApplyWxRequest request);

    /**
     * 校验两个用户是否已解锁聊天且未互相拉黑。
     */
    boolean canChat(String sendUserId, String acceptUserId);
}

