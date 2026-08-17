import { env } from "../env";

import CryptoJS from "crypto-js";

// 加密函数：带盐值的 MD5
function encryptWithSalt(data) {
  const hash = CryptoJS.MD5(data);
  return hash.toString(); // 返回32位小写字符串
}

// 接口参数加密传输
export function filedEncry(params) {
  // 获取当前时间戳
  const timestamp = new Date().getTime();

  // 如果 params 为空对象，直接返回签名
  if (Object.keys(params).length === 0) {
    return {
      Timestamp:timestamp,
      Sign: encryptWithSalt(`timestamp=${timestamp}&secret=${env.secret}`)
    };
  }

  // 过滤 params：仅保留 value 为 string 或 number 的键值对
  const filteredParams = {};
  for (const key in params) {
    if (params.hasOwnProperty(key)) {
      const value = params[key];
      if ((typeof value === 'string' || typeof value === 'number'||typeof value === 'boolean') && value !== null && value !== undefined && value !== '') {
        // 对于字符串，进一步判断是否只包含空白字符
        if (typeof value === 'string' && value.trim() === '') {
          continue; // 跳过纯空白字符串
        }
        filteredParams[key] = value;
      }
    }
  }

  // 对过滤后的键进行排序
  const sortedKeys = Object.keys(filteredParams).sort();
  // 拼接排序后的键值对
  let raw = sortedKeys
    .map(key => `${key}=${filteredParams[key]}`)
    .join("&");

  // 添加 timestamp 和 secret
  raw += `&timestamp=${timestamp}&secret=${env.secret}`;
  // 生成签名
  const sign = encryptWithSalt(raw);

  // 返回结果
  return {
    Timestamp:timestamp,
    Sign: sign
  };
}