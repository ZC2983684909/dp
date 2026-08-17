import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/token/admin/login',
    method: 'post',
    headers:{'Content-Type':'application/json'},
    data
  })
}

export function userUpdate(params) {
  return request({
    url: '/api/admin/user/update',
    method: 'post',
    data:params
  })
}

export function userCheck(params) {
  return request({
    url: '/api/admin/user/check',
    method: 'post',
    data:params
  })
}

export function getuserInfo(id) {
  return request({
    url: '/api/admin/user/userInfo/'+id,
    method: 'get'
  })
}


export function getUserList(params) {
  return request({
    url: '/api/admin/user/user/page',
    method: 'get',
    params
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: '/api/admin/user/delete/user/'+id,
    method: 'DELETE'
  })
}


//用户概要统计
export function outline() {
  return request({
    url: '/api/admin/statistic/outline',
    method: 'get',
  })
}

//用户注册折线
export function line(params) {
  return request({
    url: '/api/admin/statistic/user/line',
    method: 'get',
    params
  })
}

export function logout() {
  return request({
    url: '/api/token/logout',
    method: 'delete'
  })
}

export function userPay(params) {
  return request({
    url: '/api/admin/user/pay',
    method: 'post',
    data:params
  })
}
