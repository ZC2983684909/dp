import { env } from "../env";
import { showFn } from "./myHttpMessage";
import {
    removeStorageItem,
    getRefreshToken,
    setToken,
    setRefreshToken,
    getLocationByStorage,
} from "./utils";
import { filedEncry } from "./filedEncry";
import { refreshTokenApi } from "../pages/api/index";

// 请求队列和状态锁
let isRefreshing = false;
let failedRequestsQueue = [];

/**
 * 将失败的请求推入队列
 */
function addFailedRequest(request) {
    failedRequestsQueue.push(request);
}

/**
 * 清空队列并重试所有失败请求
 */
function retryFailedRequests(newToken) {
    failedRequestsQueue.forEach((request) => {
        request.onRetry(newToken);
    });
    failedRequestsQueue = [];
}

export default function request(params) {
    const url = params.url;
    const timeout = 60000
    const method = params.method || "GET";
    const data = params.data || {};
    const isMessage =
        typeof params.isMessage === "boolean" ? params.isMessage : true;
    const isLoading =
        typeof params.isLoading === "boolean" ? params.isLoading : true;

    // 每次请求都创建一个新的 header，避免引用问题
    let header = {
        ...filedEncry(data),
    };

    if (method === "POST") {
        header["content-type"] = "application/json";
    }

    // 获取本地 token
    const token = wx.getStorageSync("token");
    if (token) {
        header["Authorization"] = token;
    }
    // 获取本地经纬度
    const location = getLocationByStorage();
    if (location.length) {
        header["Coordinates"] = location.join(",");
    }

    if ((method === "POST" || method === "PUT") && isLoading) {
        wx.showLoading({});
    }

    return new Promise((resolve, reject) => {
        const sendRequest = (retryToken = null) => {
            // 每次发送前都深拷贝 header，防止 token 没有更新
            let requestHeader = {
                ...header,
            };
            if (retryToken) {
                requestHeader["Authorization"] = retryToken;
            }

            wx.request({
                url: env.baseURL + url,
                method,
                header: requestHeader,
                data,
                timeout,
                success(response) {
                    wx.hideLoading();
                    const res = response.data;

                    if (res.code === 200) {
                        resolve(res);
                    } else {
                        // 登录失效或未登录
                        const loginInvalidCodes = {
                            // 10009: "登录已过期", // token过期,刷新token
                            10031: "登录已过期", // refreshToken过期
                            10006: "未登录，请先登录", //必须登录的接口，未登录会返回
                            12030: "未登录，请先登录", //首页列表接口，在未登录时候可以访问第一页数据，然后就提示未登录
                        };

                        // 特殊跳转页面的 code
                        const redirectCodes = {
                            11002: "/pages_register/register1/index",
                        };

                        const errorMessage = loginInvalidCodes[res.code];

                        const redirectPath = redirectCodes[res.code];

                        // 先处理 token 过期的情况，无论 isMessage 是 true 还是 false
                        if (res.code === 10009) {
                            // 如果正在刷新 token，先把当前请求加入队列
                            if (isRefreshing) {
                                addFailedRequest({
                                    onRetry: (newToken) =>
                                        sendRequest(newToken),
                                });
                                return;
                            }

                            isRefreshing = true;
                            const refreshToken = getRefreshToken();
                            if (!refreshToken) {
                                // 没有 refresh token，直接跳登录
                                handleLogout();
                                return reject(res);
                            }

                            refreshTokenApi({
                                refreshToken,
                            })
                                .then((refreshRes) => {
                                    const {
                                        token: newToken,
                                        refreshToken: newRefreshToken,
                                    } = refreshRes.data;
                                    setToken(newToken); // 存储新的 token
                                    setRefreshToken(newRefreshToken); // 存储新的 refreshToken
                                    retryFailedRequests(newToken); // 重试之前失败的请求
                                    sendRequest(newToken); // 重发当前请求
                                })
                                .finally(() => {
                                    isRefreshing = false;
                                });

                            return; // 注意这里 return，防止继续执行后续逻辑
                        }

                        if (!isMessage) {
                            reject(res);
                        } else {
                            // 其他登录失效情况
                            if (errorMessage) {
                                console.log(res.code, "res.code");
                                if (res.code === 10031) {
                                    handleLogout();
                                } else {
                                    handleUnLogin();
                                }
                                return reject(res);
                            }
                            // 跳转逻辑
                            if (redirectPath) {
                                if (isMessage) {
                                    showFn(
                                        {
                                            message: res.msg,
                                        },
                                        () => {
                                            wx.removeStorageSync(
                                                "tempUserInfo"
                                            );
                                            wx.navigateTo({
                                                url: redirectPath,
                                            });
                                        }
                                    );
                                }
                                return reject(res);
                            }
                            // 不需要使用默认提示的错误码
                            const noMsgCode = [12029,12034];
                            // 错误提示逻辑
                            if (isMessage && !noMsgCode.includes(res.code)) {
                                wx.showToast({
                                    title: res.msg || "未知错误",
                                    icon: "none",
                                });
                                reject(res);
                            } else {
                                reject(res);
                            }
                        }
                    }
                },
                fail(err) {
                    wx.hideLoading();
                    if (err.errMsg.indexOf("request:fail") !== -1) {
                        wx.showToast({
                            title: "网络异常",
                            icon: "error",
                            duration: 2000,
                        });
                    } else {
                        wx.showToast({
                            title: "未知异常",
                            duration: 2000,
                        });
                    }
                    reject(err);
                },
            });
        };

        sendRequest();
    }).catch((e) => {
        console.error("请求失败:", e);
        return Promise.reject(e);
    });
}

/**
 * 统一登出处理
 */
let isHandlingLogout = false;

function handleLogout() {
    if (isHandlingLogout) return;
    isHandlingLogout = true;
    wx.showToast({
        title: "登录过期，请重新登录",
        icon: "none",
    });
    removeStorageItem();
    setTimeout(() => {
        wx.navigateTo({
            url: "/pages/login/index",
        });
        isHandlingLogout = false; // 跳转完成释放锁
    }, 1000);
}

/**
 * 未登录处理
 */
function handleUnLogin() {
    showFn(
        {
            message: "未登录，请先登录",
            showCancelBtn: true,
            btnStr: "立即登录",
        },
        () => {
            removeStorageItem();
            wx.navigateTo({
                url: "/pages/login/index",
            });
        }
    );
}
