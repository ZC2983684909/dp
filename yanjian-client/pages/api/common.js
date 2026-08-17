// 引入 request 文件
import request from "../../utils/http";

// 获取省数据
export const provinceList = () => {
    return request({
        url: "/yanjian/area/province",
        method: "GET",
    });
};

// 查询下级地区
export const provinceSonList = (params) => {
    return request({
        url: "/yanjian/area/son",
        method: "GET",
        data: params,
    });
};

//查询所有城市
export const allCity = () => {
    return request({
        url: "/yanjian/area/allCity",
        method: "GET",
    });
};

//查询标签，职业，收入，配置文件,提现微信二维码
export const sysconfigValue = (params) => {
    return request({
        url: "/msfast/sysconfig/value",
        method: "GET",
        data: params,
    });
};

//发起支付
export const basePay = (params) => {
    return request({
        url: "/base/pay",
        method: "POST",
        data: params,
    });
};

//申请用户微信-用申请次数
export const applyWX = (params) => {
    return request({
        url: "/yanjian/tuser/applyWX",
        method: "POST",
        data: params,
    });
};

// 获取手机号
export const getWechatPhone = (params) => {
    return request({
        url: "/yanjian/tuser/getWechatPhone",
        method: "GET",
        data: params,
    });
};

//用户是否注册
export const isRegister = (params) => {
    return request({
        url: "/yanjian/tuser/isRegister",
        method: "GET",
        data: params,
    });
};

//用户是否注册
export const locationAnalysis = (params) => {
    return request({
        url: "/yanjian/area/locationAnalysis",
        method: "GET",
        data: params,
    });
};

//查询参数
export const getScene = (id) => {
    return request({
        url: `/yanjian/wechatscene/getScene/${id}`,
        method: "GET",
    });
};

// 用户vip
export const priceList = () => {
    return request({
        url: "/yanjian/uservip/priceList",
        method: "GET",
    });
};

// 判断是否是vip
export const isVipFn = () => {
    return request({
        url: "/yanjian/uservip/isVip",
        method: "GET",
    });
};

// 判断是否登录，
// 1.目前主要是用来刚进入app的时候，判断是否登录来进行开启ws
export const isLoginFn = () => {
    return request({
        url: "/yanjian/uservip/isLogin",
        method: "GET",
        isMessage: false,
    });
};

// 消息未读总数
export const getUnread = () => {
    return request({
        url: "/imMessage/un/read",
        method: "GET",
        isMessage: false,
    });
};

//获取邀请好友的海报图片
export const getPoster = (params) => {
    return request({
        url: "/yanjian/area/getPoster",
        method: "GET",
        isMessage:false
    });
};
