package com.wxmblog.yanjian.service;

import com.wxmblog.base.websocket.common.rest.request.BaseMessageInfo;
import com.wxmblog.yanjian.common.rest.vo.RewardSendVo;
import com.wxmblog.yanjian.entity.ArticleEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AsyncService {
    @Async
    void meApply(List<String> ids);

    @Async
    void starMeRead(List<String> ids);

    @Async
    void readVist(List<String> idList);

    @Async
    void reward(RewardSendVo rewardSendVo);

    @Async
    void reNewVip(String userId);

    @Async
    void addAmount(String userId);

    @Async
    void addMessageList(String userId);

    @Async
    void setArticleRecommend(ArticleEntity articleEntity);
}
