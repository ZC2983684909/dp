// 引入 request 文件
import request from "../../utils/http";

// 登陆
export const wxAppletLogin = (params) => {
    return request({
        url: "/token/wxAppletLogin",
        method: "POST",
        data: params,
    });
};

// 使用refreshToken换token
export const refreshTokenApi = (params) => {
    return request({
        url: "/token/refreshToken",
        method: "GET",
        data: params,
    });
};

// 我的-个人中心详情
export const personalCenter = () => {
    return request({
        url: "/yanjian/tuser/personalCenter",
        method: "GET",
    });
};
// 我的-个人中心详情-不提示错误在首页用
export const personalCenterNoMsg = () => {
    return request({
        url: "/yanjian/tuser/personalCenter",
        method: "GET",
        isMessage: false,
    });
};

// 首页-最新加入
export const userList = (params) => {
    return request({
        url: "/yanjian/tuser/page",
        method: "GET",
        data: params,
    });
};

// 消息-关注我的
export const userstar = (params) => {
    return request({
        url: "/yanjian/userstar/page",
        method: "GET",
        data: params,
        isMessage: false,
    });
};
// 消息-我的关注
export const mystar = (params) => {
    return request({
        url: "/yanjian/userstar/mystar/page",
        method: "GET",
        data: params,
        isMessage: false,
    });
};
// 消息-申请我的
export const userapply = (params) => {
    return request({
        url: "/yanjian/userapply/page",
        method: "GET",
        data: params,
    });
};
// 消息-我的申请
export const myApply = (params) => {
    return request({
        url: "/yanjian/userapply/apply/page",
        method: "GET",
        data: params,
    });
};
// 消息-申请审核
export const audit = (params) => {
    return request({
        url: "/yanjian/userapply/audit",
        method: "POST",
        data: params,
    });
};

// 消息-收藏已读
export const readStar = (id) => {
    return request({
        url: "/yanjian/userstar/read/" + id,
        method: "GET",
    });
};

// 消息-申请我的已读
export const readApply = (id) => {
    return request({
        url: "/yanjian/userapply/read/" + id,
        method: "GET",
    });
};

// 消息-我的申请已读
export const readwaitApply = (id) => {
    return request({
        url: "/yanjian/userapply/readwait/" + id,
        method: "GET",
    });
};

// 个人信息详情
export const getUserInfo = () => {
    return request({
        url: "/yanjian/tuser/info",
        method: "GET",
    });
};

// 朋友圈列表
export const getDynamicList = (params) => {
    return request({
        url: "/yanjian/article/page",
        method: "GET",
        data: params,
    });
};

// 点赞
export const praiseArticle = (params) => {
    return request({
        url: "/yanjian/article/praise",
        method: "PUT",
        data: params,
        isLoading: false,
    });
};

// 取消点赞
export const cancelPraiseArticle = (params) => {
    return request({
        url: "/yanjian/article/cancel/praise",
        method: "PUT",
        data: params,
        isLoading: false,
    });
};

// 删除
export const delArticle = (params) => {
    return request({
        url: "/yanjian/article/article/" + params.id,
        method: "DELETE",
    });
};

// 校验用户信息
export const hasInvalidInfo = () => {
    return request({
        url: "/yanjian/tuser/hasInvalidInfo",
        method: "GET",
        isMessage: false,
    });
};

// 首页话题列表轮播
export const homeSubject = () => {
    return request({
        url: "/yanjian/article/homeSubject",
        method: "GET",
    });
};

// 动态话题列表
export const subjectList = (params) => {
    return request({
        url: "/yanjian/article/subject/page",
        method: "GET",
        data: params,
    });
};

// 用户详情
export const tuserDetail = (id, params) => {
    return request({
        url: "/yanjian/tuser/detail/" + id,
        method: "GET",
        data: params,
    });
};

// 未读消息数
export const unreadCount = () => {
    return request({
        url: "/yanjian/message/unreadCount",
        method: "GET",
    });
};

// 用户个人中心访客未读消息
export const getCenterMessageResponse = () => {
    return request({
        url: "/yanjian/tuser/getCenterMessageResponse",
        method: "GET",
        isMessage: false,
    });
};

// 首页检测弹窗
export const getpopup = (params) => {
    return request({
        url: "/yanjian/popuprecords/popup",
        method: "GET",
        data: params,
    });
};

// IM消息列表
export const getImList = (params) => {
    return request({
        url: "/imMessage/list",
        method: "GET",
        data: params,
        isMessage:false
    });
};

// 删除IM消息
export const deleteIm = (sendUserId) => {
    return request({
        url: `/imMessage/list/delete/${sendUserId}`,
        method: "DELETE",
    });
};


// 获取是否关注公众号
export const getUseSubscribe = (params) => {
  return request({
      url: "/yanjian/tuser/useSubscribe",
      method: "GET",
      data: params,
  });
};