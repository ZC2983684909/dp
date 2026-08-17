// 就是配置当前小程序项目的环境变量
// 获取当前小程序的账号信息
const { miniProgram } = wx.getAccountInfoSync();
// 获取小程序的版本
const { envVersion } = miniProgram;

let env = {
    isDev: envVersion === "develop",
    //服务端接口地址，需要确保后端服务端启动
    // 手机真机调试必须访问电脑的局域网地址，不能使用 127.0.0.1
    baseURL: "http://192.168.2.105:8088",
    //静态资源文件地址，需要先将静态资源文件上传至该地址，否则部分图标将无法显示
    imgBaseURL:
        "https://yanjian-1326741559.cos.ap-chongqing.myqcloud.com/system",
    // websocket端口，后端启动时配置,用于im聊天
    wsUrl: "ws://192.168.2.105:8087/ws",
    // 参数加密secret 接口请求验签密钥，需要和服务端yml配置(wxmfast.config.sign.secret)保持一致，否则接口无法请求，正式环境需要自行修改
    // 本地联调时替换为服务端 wxmfast.config.sign.secret 的值；不要提交真实密钥。
    secret: "CHANGE_ME",
    // 企业微信客服商户号id
    corpId: "ww123456789",
};

switch (envVersion) {
    case "develop":
        // 开发版
        env.baseURL = "http://192.168.2.105:8088";
        env.imgBaseURL =
            "https://yanjian-1326741559.cos.ap-chongqing.myqcloud.com/system";
        env.wsUrl = "ws://192.168.2.105:8087/ws";

        break;
    case "trial":
        // 体验版
        env.baseURL = "http://192.168.2.105:8088";
        env.imgBaseURL =
            "https://yanjian-1326741559.cos.ap-chongqing.myqcloud.com/system";
        env.wsUrl = "ws://192.168.2.105:8087/ws";
        break;
    case "release":
        // 正式版
        env.baseURL = "http://192.168.2.105:8088";
        env.imgBaseURL =
            "https://yanjian-1326741559.cos.ap-chongqing.myqcloud.com/system";
        env.wsUrl = "ws://192.168.2.105:8087/ws";
        break;
    default:
        break;
}

export { env };
