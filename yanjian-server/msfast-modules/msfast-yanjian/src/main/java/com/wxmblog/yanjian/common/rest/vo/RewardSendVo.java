package com.wxmblog.yanjian.common.rest.vo;

import lombok.Data;

@Data
public class RewardSendVo {

    private String applyId;

    //申请付费用户id
    private String payUserId;

    private String applyUserId;

    //1-申请微信 2-申请聊天
    private String type;
}
