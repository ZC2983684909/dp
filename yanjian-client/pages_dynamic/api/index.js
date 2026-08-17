// 引入 request 文件
import request from "../../utils/http";

// 话题详情
export const subjectInfo = (id) => {
  return request({
      url: `/yanjian/article/subject/info/${id}`,
      method: "GET",
  });
};

// 话题关注
export const subjectStar = (data) => {
  return request({
      url: `/yanjian/article/subject/star`,
      method: "PUT",
      data:data
  });
};

// 话题取消关注
export const subjectCancelStar = (data) => {
  return request({
      url: `/yanjian/article/cancel/subject/star`,
      method: "PUT",
      data:data
  });
};

// 消息列表
export const messagePage = (data) => {
  return request({
      url: `/yanjian/message/page`,
      method: "GET",
      data:data
  });
};

// 动态评论
export const commentAdd = (params) => {
  return request({
    url: `/yanjian/article/comment/add`,
    method: 'post',
    data:params
  });
};
