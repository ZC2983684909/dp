// 引入 request 文件
import request from "../../utils/http";

// 用户信息-编辑
export const editUserInfo = (params) => {
    return request({
        url: "/yanjian/tuser/editUserInfo",
        method: "POST",
        data: params,
    });
};

// 个人信息详情
export const getUserInfo = () => {
    return request({
        url: "/yanjian/tuser/info",
        method: "GET",
    });
};

// 设置隐身状态
export const setInvisible = (params) => {
    return request({
        url: "/yanjian/tuser/invisible",
        method: "POST",
        data: params,
    });
};

// 删除用户-账户注销
export const removeUser = () => {
    return request({
        url: "/yanjian/tuser/remove",
        method: "DELETE",
    });
};

// 我的-个人中心-资料编辑-编辑相册
export const photoEdit = (params) => {
    return request({
        url: "/yanjian/tuser/photoEdit",
        method: "PUT",
        data: params,
    });
};

// 分享奖励结果
export const getShare = () => {
    return request({
        url: "/yanjian/tuser/getShare",
        method: "GET",
    });
};

// 我的邀请列表
export const sharePage = (params) => {
    return request({
        url: "/yanjian/tuser/share/page",
        method: "GET",
        data: params,
    });
};

// 提现记录
export const withdrawRecord = (params) => {
    return request({
        url: "/yanjian/distributionamount/withdrawRecord",
        method: "GET",
        params,
    });
};

// 邀请人邀请列表
export const sharePersonPage = (params) => {
    return request({
        url: "/yanjian/tuser/sharePerson/page",
        method: "GET",
        params,
    });
};

// 签到列表
export const signinCount = () => {
    return request({
        url: "/yanjian/signin/getStatus",
        method: "GET",
    });
};

// 签到
export const signin = () => {
    return request({
        url: "/yanjian/signin/add",
        method: "GET",
    });
};

// 兑换
export const exchange = () => {
    return request({
        url: "/yanjian/signin/exchange",
        method: "GET",
    });
};

// 收藏
export const tuserStar = (id) => {
    return request({
        url: "/yanjian/tuser/star/" + id,
        method: "GET",
    });
};

// 取消收藏
export const tuserCancelStar = (id) => {
    return request({
        url: "/yanjian/tuser/cancelStar/" + id,
        method: "GET",
    });
};

// 充值颜币预览
export const amountPre = () => {
    return request({
        url: "/yanjian/useraccount/amount/pre",
        method: "GET",
    });
};

// 访客记录
export const visit = (params) => {
    return request({
        url: "/yanjian/tuser/visit",
        method: "GET",
        data: params,
    });
};

// 浏览记录
export const browse = (params) => {
    return request({
        url: "/yanjian/tuser/browse",
        method: "GET",
        data: params,
    });
};

// 分销明细
export const distributionamount = (params) => {
    return request({
        url: "/yanjian/distributionamount/page",
        method: "GET",
        data: params,
    });
};

// 生成小程序码
export const wechatsceneToCode = (params) => {
    return request({
        url: "/yanjian/wechatscene/wx/code",
        method: "POST",
        data: params,
    });
};

// 颜币记录
export const useraccountPage = (params) => {
    return request({
        url: "/yanjian/useraccount/page",
        method: "GET",
        data: params,
    });
};

