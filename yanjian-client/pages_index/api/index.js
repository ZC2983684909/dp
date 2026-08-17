// 引入 request 文件
import request from "../../utils/http";

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

// 用户动态列表
export const userArticle = (params) => {
    return request({
        url: "/yanjian/article/user/article/" + params.userId,
        method: "GET",
        data: params,
    });
};

// 发布朋友圈
export const addArticle = (params) => {
    return request({
        url: "/yanjian/article/add",
        method: "POST",
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

// 动态详情
export const articleInfo = (id) => {
    return request({
        url: `/yanjian/article/info/${id}`,
        method: "get",
    });
};

// 动态评论
export const commentAdd = (params) => {
    return request({
        url: `/yanjian/article/comment/add`,
        method: "post",
        data: params,
    });
};

// 动态评论列表
export const articleComment = (params) => {
    return request({
        url: `/yanjian/article/comment/page/${params.articleId}`,
        method: "get",
        data: params,
    });
};

// 删除
export const delArticle = (params) => {
    return request({
        url: "/yanjian/article/article/" + params.id,
        method: "DELETE",
    });
};

// 删除评论
export const delComment = (id) => {
    return request({
        url: "/yanjian/article/comment/" + id,
        method: "DELETE",
    });
};

// 修改动态公开
export const putArticle = (params) => {
    return request({
        url: "/yanjian/article/open",
        method: "PUT",
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

// 申请记录
export const applyPage = (params) => {
    return request({
        url: "/yanjian/tuser/applyPage",
        method: "GET",
        data: params,
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

// 屏蔽列表
export const usershieldList = (params) => {
    return request({
        url: "/yanjian/usershield/page",
        method: "GET",
        data: params,
    });
};

// 屏蔽用户
export const usershield = (id) => {
    return request({
        url: "/yanjian/usershield/shield/" + id,
        method: "GET",
    });
};
// 取消屏蔽
export const usershieldCancel = (id) => {
    return request({
        url: "/yanjian/usershield/cancel/" + id,
        method: "GET",
    });
};

// 用户投诉
export const complaint = (params) => {
    return request({
        url: "/yanjian/complaint/add",
        method: "POST",
        data: params,
    });
};

// 申请微信预览
export const wechatPre = (params) => {
    return request({
        url: "/yanjian/userapply/wechat/pre",
        method: "GET",
        data: params,
    });
};

// 申请私信预览
export const userchatPre = (params) => {
    return request({
        url: "/yanjian/userchat/pre",
        method: "GET",
        data: params,
    });
};

// 解锁用户私信
export const applyUserChat = (params) => {
    return request({
        url: "/yanjian/userchat/applyChat",
        method: "POST",
        data: params,
    });
};
