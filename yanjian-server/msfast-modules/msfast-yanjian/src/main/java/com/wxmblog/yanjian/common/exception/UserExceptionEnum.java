package com.wxmblog.yanjian.common.exception;

import com.wxmblog.base.common.interfaces.BaseExceptionEnumInterface;

public enum UserExceptionEnum implements BaseExceptionEnumInterface {

    SEARCH_PARAM_EMPTY_EXCEPTION(12001, "查询条件不可为空"),
    MATCHING_BEYOND_LIMIT_EXCEPTION(12002, "匹配数限额"),
    MIN_AGE_GREATER_EXCEPTION(12003, "最小年龄大于最大年龄"),
    FIRST_PHOTO_NOT_DELETE_EXCEPTION(12004, "头像不能删除"),
    NEW_URL_NOT_EMPTY_EXCEPTION(12005, "新图片地址不可为空"),
    OLD_URL_NOT_EMPTY_EXCEPTION(12006, "旧图片地址不可为空"),
    USER_VERSION_DIFFERENT_EXCEPTION(12007, "用户信息已变更"),
    USER_AUTH_NOT_PASS_EXCEPTION(12008, "用户资料审核暂未通过"),
    USER_NOT_DUMMY_EXCEPTION(12009, "用户不是虚拟用户"),
    BALANCE_LESS(12009, "余额不足"),
    LIKE_ME_LESS_PRICE(12010, "喜欢你的数量不够全部解锁"),

    ARTICLE_IS_EMPTY(12011, "内容不能为空"),
    USER_IS_INVISIBLE(12012, "需解除隐身状态"),
    APPLY_COUNT_ERROR_EXCEPTION(12013, "申请次数已用完"),
    SHIELD_USER_EXCEPTION(12014, "对方已屏蔽你"),
    ALREADY_COLLECTION(12015, "已达收藏上线"),
    APPLY_LIMIT_ERROR_EXCEPTION(12016, "已达每日申请上限"),
    VISIT_LIMIT_ERROR_EXCEPTION(12017, "已达每日访问上限"),
    APPLY_SELF_EXCEPTION(12018, "不能申请自己"),
    ID_AUTH_NOT_PASS_EXCEPTION(12019, "实名认证未通过"),
    EDU_AUTH_EXCEPTION(12020, "学历认证未通过"),
    PHOTO_AUTH_NOT_PASS_EXCEPTION(12021, "相册认证未通过"),
    NOT_ID_AUTH_EXCEPTION(12022, "未实名认证"),
    PHOTO_IS_EMPTY_EXCEPTION(12023, "相册不能为空"),
    USER_DETAIL_NO_ID_AUTH(12024, "查看对方主页需要您完成实名认证"),
    USER_DETAIL_NO_PHOTO_AUTH(12025, "查看对方主页需要完成相册认证"),
    APPLY_AT_LEAST_TWO_AUTH(12026, "申请微信号需要完成至少两种验证"),
    COMMENT_LEAST_TWO_AUTH(12027, "评论需要完成至少两种验证"),
    ARTICLE_LEAST_TWO_AUTH(12028, "发动态需要完成至少两种验证"),
    NOT_VIP_USER_EXCEPTION(12029, "非VIP用户"),
    NOT_SEX_EXCEPTION(12031, "请选择性别"),
    IMG_VIDEO_NOT_EXIST(12032, "图片和视频不能同时存在"),
    STAR_SELF_EXCEPTION(12033, "不能收藏自己"),
    CONTENT_CONTAIN_ILLEGAL_CHARACTER(12034, "内容包含非法字符"),
    USER_IS_INVISIBLE_STATUS(12035, "用户已隐身"),
    ;

    private Integer code;
    private String msg;

    UserExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
