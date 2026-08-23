# 有趣的搭子：今晚部署清单

本文档对应“Vercel 管理后台 + 电脑运行 Java/MySQL/Redis/WebSocket + 小程序通过 HTTPS/WSS 访问”的部署方式。

## 当前已完成

- [x] MySQL、Redis、Java HTTP、WebSocket、管理后台端口已启动并可连通：`3306`、`6379`、`8088`、`8087`、`9527`
- [x] 服务端已完成构建
- [x] 管理后台已完成生产构建，产物目录为 `yanjian-management/dist`
- [x] 管理后台生产配置已移除 `localhost`，改为读取 `VUE_APP_BACKEND_APPLICATION_URL`
- [x] Vercel SPA 路由回退配置已加入 `yanjian-management/vercel.json`
- [x] MySQL 和 Redis 没有要求对公网开放

## 还需要外部配置

### 1. 公网 HTTPS/WSS 地址

必须准备一个可以长期使用的公网域名，例如：

```text
https://api.example.com       # HTTP API
wss://api.example.com/ws      # WebSocket
https://admin.example.com     # Vercel 管理后台，可使用 vercel.app 域名
```

电脑不能关机或睡眠。域名通过 Cloudflare Tunnel、FRP 或其他反向代理转发到：

```text
api.example.com       -> 127.0.0.1:8088
api.example.com/ws    -> 127.0.0.1:8087
```

不要将 `3306` 或 `6379` 配置到公网入口。

### 2. 微信公众平台合法域名

在小程序后台配置：

- request 合法域名：`https://api.example.com`
- socket 合法域名：`wss://api.example.com`
- 如使用上传图片或文件的独立域名，也加入 download/upload 合法域名

配置后，在微信开发者工具中刷新项目配置并重新编译。

### 3. Vercel 环境变量

Vercel 项目根目录选择 `yanjian-management`，配置：

```text
Build Command: npm run build:prod
Output Directory: dist
VUE_APP_BACKEND_APPLICATION_URL=https://api.example.com
```

然后重新部署。若使用 Vercel 自带域名，管理后台可直接通过该域名访问。

## 本地启动命令

### 服务端

```powershell
cd E:\yanjian\yan-jian\yanjian-server
java -jar msfast-modules\msfast-yanjian\target\msfast-yanjian.jar `
  --spring.profiles.active=local `
  --spring.config.additional-location=file:E:/yanjian/yan-jian/.tools/application-local.yml
```

### 管理后台本地预览

```powershell
cd E:\yanjian\yan-jian\yanjian-management
npm run dev
```

### 生成管理后台部署包

```powershell
cd E:\yanjian\yan-jian\yanjian-management
npm run build:prod
```

## 公网配置完成后修改小程序地址

只修改 `yanjian-client/env.js` 的开发、体验和正式环境地址，示例：

```js
env.baseURL = "https://api.example.com";
env.wsUrl = "wss://api.example.com/ws";
```

不要把 AppSecret、数据库密码、Redis 密码、支付密钥提交到仓库。之前在聊天或截图中出现过的微信 AppSecret 应在正式上线前重置。

## 上线前冒烟测试

- [ ] 手机可以访问公网 HTTPS API
- [ ] 小程序能完成微信登录/注册
- [ ] 首页、动态、个人中心正常加载
- [ ] 两个测试用户可以互相申请并聊天
- [ ] WebSocket 重连、历史消息、未读数正常
- [ ] 管理后台可以登录、查看用户、审核资料和投诉
- [ ] 关闭支付/短信等未配置能力时，页面不会阻塞主流程
- [ ] Windows 防火墙、自动启动、日志轮转和数据库备份已配置
