// 引入 request 文件
import request from '../../utils/http';

// 注册
export const wxAppletRegister = (params) => {
  return request({
    url: '/token/wxAppletRegister',
    method: 'POST',
    data: params
  });
};

// 用户信息-编辑
export const editUserInfo = (params) => {
  return request({
    url: '/yanjian/tuser/editUserInfo',
    method: 'POST',
    data: params
  });
};

// 身份证认证申请查看
export const idInfo = () => {
  return request({
    url: '/yanjian/tuser/idInfo',
    method: 'GET',
  });
};

// 身份证认证申请
export const idApply = (params) => {
  return request({
    url: '/yanjian/tuser/idApply',
    method: 'POST',
    data:params
  });
};

// 学历认证申请查看
export const eduInfo = () => {
  return request({
    url: '/yanjian/tuser/eduInfo',
    method: 'GET',
  });
};

// 学历认证申请
export const eduAuth = (params) => {
  return request({
    url: '/yanjian/tuser/eduAuth',
    method: 'POST',
    data:params
  });
};

// 实名认证校验
export const idAuthRule = (params) => {
  return request({
    url: '/yanjian/tuser/idAuth',
    method: 'POST',
    data:params
  });
};

// 获取用户认证状态查询
export const getAuthStatus = () => {
  return request({
    url: '/yanjian/tuser/getAuthStatus',
    method: 'GET'
  });
};

// 获取E证通身份认证所需的token
export const getEidToken = (params) => {
  return request({
    url: '/yanjian/tuser/getEidToken',
    method: 'POST',
    data:params
  });
};

// 通过学历验证码进行学历校验
export const eduCheck = (params) => {
  return request({
    url: '/yanjian/usereduaudit/eduCheck',
    method: 'GET',
    data:params
  });
};

// 用户身份证认证检测
export const idAuthCheck = (params) => {
  return request({
    url: '/yanjian/tuser/idAuthCheck',
    method: 'GET',
    data:params
  });
};


// 相册认证申请
export const photoAuth = (params) => {
  return request({
    url: '/yanjian/tuser/photoAuth',
    method: 'POST',
    data:params
  });
};


