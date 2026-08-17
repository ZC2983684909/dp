import request from '@/utils/request'

// 查询系统配置值
export function sysconfig(params) {
  return request({
    url: '/api/msfast/sysconfig/value',
    method: 'get',
    params
  })
}
