# 有趣的搭子项目开发文档

本文档用于本地开发、联调和提交代码。项目包含微信小程序客户端、Spring Boot 服务端和 Vue 管理后台。

## 1. 项目结构

```text
yan-jian/
├─ yanjian-client/       # 微信小程序客户端（WXML / WXSS / JavaScript）
├─ yanjian-server/       # Spring Boot 服务端、REST API、WebSocket
├─ yanjian-management/   # Vue 2 管理后台
├─ LICENSE
├─ README.md
└─ DEVELOPMENT.md
```

主要通信关系：

- 小程序 HTTP API：默认端口 `8088`
- 小程序 WebSocket/IM：默认端口 `8087`
- 管理后台开发环境通过 `BACKGROUND_APPLICATION_URL` 由 Vue CLI 代理服务端；生产构建通过 `VUE_APP_BACKEND_APPLICATION_URL` 访问公网 HTTPS 服务端
- 数据库使用 MySQL，缓存和部分实时状态使用 Redis
- 图片和文件使用 MinIO 或腾讯云 COS，二选一

## 2. 基础环境

- JDK 8
- Maven 3.8+
- MySQL 8
- Redis
- Node.js 和 npm
- 微信开发者工具
- （可选）MinIO 或腾讯云 COS

服务端依赖微信小程序 AppID/AppSecret。实名、支付、人脸核身、短信等第三方能力需要在对应平台开通；本地开发可以先关闭支付和非必要的付费能力。

## 3. 初始化数据库

创建数据库并导入项目 SQL：

```powershell
mysql -u root -p -e "CREATE DATABASE yanjian_demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p yanjian_demo < yanjian-server/doc/yanjian_demo.sql
```

然后确认 MySQL 和 Redis 已启动。数据库用户名、密码、Redis 地址等只放在本机配置中，不要提交到 Git。

## 4. 服务端配置和启动

不要直接把真实配置写入 Git 已跟踪的配置文件。建议在项目根目录创建本地配置文件：

`.tools/application-local.yml`

示例：

```yaml
server:
  port: 8088

wxmfast:
  config:
    locationAnalysis:
      enabled: false
    auth:
      dev-mode: true
      wxapplet:
        appId: YOUR_MINIPROGRAM_APPID
        secret: YOUR_MINIPROGRAM_APPSECRET
    pay:
      enabled: false
    file:
      static-path: E:/yanjian/yan-jian/.tools/upload
```

数据库、Redis、对象存储等配置按本机环境补充。`.tools/` 已加入忽略规则，不会被提交。

构建服务端：

```powershell
cd yanjian-server
mvn -pl msfast-modules/msfast-yanjian -am -DskipTests package
```

启动服务端：

```powershell
java -jar msfast-modules/msfast-yanjian/target/msfast-yanjian.jar `
  --spring.profiles.active=local `
  --spring.config.additional-location=file:../.tools/application-local.yml
```

启动后检查：

- HTTP API：`http://127.0.0.1:8088`
- WebSocket：`ws://127.0.0.1:8087/ws`

如果使用本项目自带的 JDK/Maven，可将上面的 `java` 和 `mvn` 替换为本机实际路径。

## 5. 小程序客户端

1. 使用微信开发者工具打开 `yanjian-client/`。
2. 在客户端目录执行 `npm install`。
3. 在开发者工具中执行“工具 → 构建 npm”。
4. 检查 `project.config.json` 中的 AppID。
5. 重新编译项目。

客户端服务地址在 `yanjian-client/env.js` 中配置：

```js
baseURL: "http://127.0.0.1:8088",
wsUrl: "ws://127.0.0.1:8087/ws",
```

`env.js` 中的 `secret` 只用于本地请求签名，必须与服务端签名配置保持一致；请在本机替换 `CHANGE_ME`，不要把真实值提交到仓库。

地址选择规则：

- 开发者工具模拟器：可以使用 `127.0.0.1`。
- 手机真机且与电脑同一 Wi-Fi：使用电脑局域网 IP。
- 手机真机且不在同一物理局域网：使用 Tailscale/ZeroTier 虚拟 IP。
- 正式发布：使用 HTTPS 域名和 WSS，不能依赖本机 IP。

开发者工具本地调试可以在“详情 → 本地设置”中关闭合法域名校验。体验版和正式版仍需配置微信公众平台的 request 合法域名、socket 合法域名，并使用 HTTPS/WSS。

## 6. 管理后台

```powershell
cd yanjian-management
npm install
npm run dev
```

本地 API 地址配置在 `yanjian-management/.env.development`：

```dotenv
BACKGROUND_APPLICATION_URL=http://127.0.0.1:8088
```

管理后台部署到 Vercel 时，将项目根目录设置为 `yanjian-management`，构建命令设置为 `npm run build:prod`，输出目录设置为 `dist`，并配置：

```dotenv
VUE_APP_BACKEND_APPLICATION_URL=https://你的公网后端域名
```

`yanjian-management/vercel.json` 已包含前端路由回退配置。公网后端必须使用 HTTPS，不能填写 `localhost` 或局域网地址。

生产构建：

```powershell
npm run build:prod
```

## 7. 真机联调流程

1. 启动 MySQL、Redis 和服务端。
2. 确认服务端监听 `8088` 和 `8087`。
3. 将 `env.js` 中的地址改为手机可访问的电脑地址。
4. 手机和电脑网络可达后，在开发者工具点击“真机调试”。
5. 清除小程序缓存并重新编译。
6. 首次进入先完成登录/注册，再测试首页、动态、消息和个人中心。

如果使用虚拟局域网，电脑和手机都安装并登录同一个 Tailscale 或 ZeroTier 网络，然后使用电脑显示的虚拟 IP。虚拟网络只解决设备互通，后端进程和端口仍需正常运行。

## 8. 常见问题

### `request:fail url not in domain list`

这是微信合法域名校验失败。开发阶段检查开发者工具“详情 → 本地设置 → 不校验合法域名”，并重新编译；正式环境必须配置 HTTPS 合法域名。

### 手机提示网络异常

通常是客户端仍使用了 `127.0.0.1`、手机和电脑不在同一网络、Windows 防火墙未放行端口，或者服务端只启动了 HTTP 而没有启动 WebSocket。先用手机浏览器访问电脑地址确认网络可达。

### `10006 未登录`

表示请求没有有效 token。清除小程序缓存后重新登录；不要在未登录状态直接调用需要登录的接口。

### `ContextUtil` 或 Sentinel 初始化失败

确认服务端运行用户对日志目录有写权限。当前启动类会将 Sentinel 日志放到服务端目录下的 `logs/csp`，不要把该目录提交到 Git。

### 页面空白

先看微信开发者工具 Console 和 Network：

1. 是否存在 WXML/JS 语法错误；
2. 是否完成“构建 npm”；
3. 是否配置了页面依赖组件；
4. API 是否返回 404、500 或未登录；
5. 是否清理缓存并重新编译。

## 9. 提交前检查

```powershell
git status --short
git diff --check
node --check yanjian-client/app.js
```

提交前重点确认：

- 没有 `AppSecret`、数据库密码、Redis 密码、支付私钥、Token；
- 没有 `logs/`、`target/`、`dist/`、`node_modules/`；
- 没有微信开发者工具的私有配置文件；
- 没有把本机局域网地址误当成正式服务地址；
- 真实密钥如果曾经在聊天、截图或公开仓库中出现，应立即重置。

## 10. GitHub 推送

当前仓库原远程地址是 Gitee。建议保留它，并额外添加 GitHub remote：

```powershell
git remote add github https://github.com/<OWNER>/<REPOSITORY>.git
git add .
git status
git commit -m "完善小程序联调和开发配置"
git push -u github master
```

如果 GitHub 默认分支是 `main`，请先确认目标仓库分支策略，再执行推送。不要把访问令牌写入 remote URL 或提交到代码中。
