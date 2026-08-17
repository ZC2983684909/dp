// pages_register/register/register1/index.js
import { formatDate } from "../../utils/utils";
import { env } from "../../env";
import { getWechatPhone } from "../../pages/api/common";
import { wxAppletRegister } from "../api/index";
import { wxAppletLogin, getUserInfo } from "../../pages/api/index";
import { showFn } from "../../utils/myHttpMessage";
Page({
    data: {
        imgBaseURL: "",
        userInfo: {
            wechatOpen: false,
            birthDate: "",
        },
        show: false,
        currentDate: new Date(new Date().getFullYear(), 0, 1).getTime(),
        minDate: new Date(1970, 0, 1).getTime(),
        formatter(type, value) {
            if (type === "year") {
                return `${value}年`;
            }
            if (type === "month") {
                return `${value}月`;
            }
            if (type === "day") {
                return `${value}日`;
            }
            return value;
        },
        isValidate: env.isDev,
        isGetPhone: env.isDev,
        year: new Date().getFullYear(),
        month: "01",
        day: "01",
        showTip: false,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
            userInfo: {
                ...this.data.userInfo,
                birthDate: formatDate("1990-01-01", "yyyy-MM-dd"),
                nickName: this.data.userInfo.nickName || (env.isDev ? `测试用户${Date.now().toString().slice(-4)}` : ""),
                wechat: this.data.userInfo.wechat || (env.isDev ? `dev_${Date.now().toString().slice(-6)}` : ""),
            },
        });
    },
    onShow() {},
    showTip() {
        this.setData({
            showTip: true,
        });
    },
    onClickLeft() {
        wx.navigateBack();
    },
    goPage(event) {
        wx.navigateTo({
            url: event.currentTarget.dataset.path,
        });
    },
    changeshow() {
        this.setData({
            show: true,
        });
    },
    onClose() {
        this.setData({
            show: false,
        });
    },
    onConfirm(event) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                birthDate: formatDate(event.detail, "yyyy-MM-dd"),
            },
            year: formatDate(event.detail, "yyyy"),
            month: formatDate(event.detail, "MM"),
            day: formatDate(event.detail, "dd"),
            show: false,
        });
        // 触发验证逻辑
        this.setIsValidate();
    },
    onChangeSwitch(e) {
        this.setData({
            form: {
                ...this.data.userInfo,
                wechatOpen: e.detail,
            },
        });
    },
    onChange1(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                nickName: e.detail,
            },
        });
        // 触发验证逻辑
        this.setIsValidate();
    },
    onChange2(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                wechat: e.detail,
            },
        });
        // 触发验证逻辑
        this.setIsValidate();
    },
    onChange3(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                registrationNo: e.detail,
            },
        });
        // 触发验证逻辑
        this.setIsValidate();
    },
    getPhoneNumber(value) {
        console.log(value);
        let self = this;
        const code = value.detail.detail.code;
        if (code) {
            getWechatPhone({
                code,
            }).then((res) => {
                self.setData({
                    userInfo: {
                        ...self.data.userInfo,
                        phone: res.data,
                    },
                    isGetPhone: true,
                });
                self.nextStep();
            });
        }
    },
    nextStep() {
        if (this.validate()) {
            if (env.isDev) {
                this.devRegister();
                return;
            }
            wx.setStorageSync("tempUserInfo", this.data.userInfo);
            wx.navigateTo({
                url: "/pages_register/register2/index",
            });
        }
    },
    devRegister() {
        const userInfo = {
            ...this.data.userInfo,
            sex: wx.getStorageSync("registerSex") || "\u7537",
            wechatOpen: Boolean(this.data.userInfo.wechatOpen),
            personalPhoto: ["local-dev-placeholder"],
        };
        wx.showLoading({ title: "正在创建测试账号", mask: true });
        wx.login({
            success: (loginResult) => {
                wxAppletRegister({
                    ...userInfo,
                    code: loginResult.code,
                })
                    .then(() => this.devLogin())
                    .catch(() => {
                        // 已注册过的开发账号直接登录，便于反复测试。
                        this.devLogin();
                    });
            },
            fail: () => {
                wx.hideLoading();
                wx.showToast({ title: "微信登录失败", icon: "none" });
            },
        });
    },
    devLogin() {
        wx.login({
            success: (loginResult) => {
                wxAppletLogin({ code: loginResult.code })
                    .then((result) => {
                        wx.setStorageSync("token", result.data.token);
                        wx.setStorageSync("refreshToken", result.data.refreshToken);
                        wx.setStorageSync("sex", "\u7537");
                        return getUserInfo();
                    })
                    .then((result) => {
                        wx.setStorageSync("userInfo", result.data);
                        wx.hideLoading();
                        wx.reLaunch({ url: "/pages/index/index" });
                    })
                    .catch(() => {
                        wx.hideLoading();
                        wx.showToast({ title: "测试账号登录失败", icon: "none" });
                    });
            },
            fail: () => {
                wx.hideLoading();
                wx.showToast({ title: "微信登录失败", icon: "none" });
            },
        });
    },
    setIsValidate() {
        if (this.validate(false)) {
            this.setData({
                isValidate: true,
            });
        } else {
            this.setData({
                isValidate: false,
            });
        }
    },
    validate(isMessage = true) {
        let msg = "";
        let userInfo = this.data.userInfo;
        if (!userInfo.nickName || userInfo.nickName.length > 8) {
            msg = "请输入昵称且小于8个字";
        } else if (!userInfo.birthDate) {
            msg = "请选择出生年月";
        } else if (!userInfo.wechat) {
            msg = "请输入微信号";
        }
        if (msg) {
            if (isMessage) {
                wx.showToast({
                    title: msg,
                    icon: "none",
                });
            }
            return false;
        } else {
            return true;
        }
    },
});
