// pages/login/index.js
import { wxAppletLogin, getUserInfo } from "../api/index";
import { env } from "../../env";
import socketManager from "../../utils/socket";
Page({
    data: {
        checked: false,
        imgBaseURL: "",
        isDev: env.isDev,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        socketManager.close();
    },
    back(){
      wx.navigateBack()
    },
    goPage(event) {
        wx.navigateTo({
            url: event.currentTarget.dataset.path,
        });
    },
    onChange(event) {
        this.setData({
            checked: event.detail,
        });
    },
    tapLogin() {
        if (!this.data.checked) {
            wx.showToast({
                title: "请同意服务和隐私协议",
                icon: "none",
                duration: 2000,
            });
            return;
        }
        wx.login({
            success: (res) => {
                wxAppletLogin({
                    code: res.code,
                })
                    .then((rt) => {
                        wx.setStorageSync("token", rt.data.token);
                        wx.setStorageSync("refreshToken", rt.data.refreshToken);
                        getUserInfo().then((rt2) => {
                            wx.setStorageSync("userInfo", rt2.data);
                            wx.setStorageSync("sex", rt2.data.sex);
                            const app = getApp();
                            app.contactWs();
                            wx.reLaunch({
                                url: "/pages/index/index",
                            });
                        });
                    })
                    .catch((err) => {
                        console.log(err);
                        if (env.isDev) {
                            wx.navigateTo({
                                url: "/pages_register/register1/index",
                            });
                        }
                    });
            },
        });
    },
});
