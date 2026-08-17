package com.wxmblog.yanjian.common.enums.message;

public enum SendUserMessageTypeEnum {
    //系统通知 好友通知 动态通知 消息通知

    SYSTEM("系统通知"),
    FRIEND("好友通知"),
    DYNAMIC("动态通知"),
    MESSAGE("私信通知");
    private String desc;

    SendUserMessageTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
