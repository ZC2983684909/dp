package com.wxmblog.yanjian.common.enums.message;

public enum SendUserMessageEnum {

    CHAT("有人向你发起了聊天", "SMS_499265044"),
    APPLY_WECHAT("有人申请解锁你的微信", "SMS_499310042"),
    SYSTEM_REWARD("系统奖励", "SMS_499140046"),
    YANBI_NOT_USE("颜币未使用提醒", "SMS_499195044"),
    APPLY_WECHAT_PASS("你申请解锁微信被通过", "SMS_499180051"),
    UNLOCK_PRIVATE_MESSAGE("有人解锁了你的私信", "SMS_499120060"),
    REPEAT_VIEW_YOU("有人反复查看你", "SMS_499250055"),
    WECHAR_APPLY_NOT_PROCESS("微信申请未处理", "SMS_499090052"),
    //上线通知
    ONLINE_NOTICE("上线通知", "SMS_500685069");
    private String title;


    private String smsCode;

    SendUserMessageEnum(String title, String smsCode) {
        this.title = title;
        this.smsCode = smsCode;
    }

    public String getTitle() {
        return title;
    }


    public String getSmsCode() {
        return smsCode;
    }
}
