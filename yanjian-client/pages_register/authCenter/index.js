// pages_register/register/authCenter/index.js
import {
    wxAppletRegister,
    getAuthStatus,
    getEidToken,
    idApply,
    photoAuth,
} from "../api/index";
import { wxAppletLogin, getUserInfo } from "../../pages/api/index";
import { removeStorageItem } from "../../utils/utils";
import { env } from "../../env";
import { startEid } from "../../mp_ecard_sdk/main";
import { showFn } from "../../utils/myHttpMessage";
Page({
    data: {
        imgBaseURL: "",
        userInfo: {},
        showMsg: false,
        status: {
            idAuth: "",
            eduAuth: "",
            photoAuth: "",
        },
        verifyDone: false,
        show: false,
        actions: [
            {
                name: "资料认证",
            },
            {
                name: "学信网认证（推荐）",
            },
        ],
        top: 0,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        this.backBtnTop();
    },
    onShow() {
        this.getStatus();
    },
    showAction() {
        if (this.data.status.idAuth != 3) {
            showFn({
                message: "请先完成实名认证",
            });
            return;
        }
        this.setData({
            show: true,
        });
    },
    onClose() {
        this.setData({
            show: false,
        });
    },
    onSelect(e) {
        let name = e.detail.name;
        if (name == "资料认证") {
            wx.navigateTo({
                url:
                    "/pages_register/education_certification/index?type=" +
                    this.data.type,
            });
        } else {
            wx.navigateTo({
                url: "/pages_register/auto_edu/index?type=" + this.data.type,
            });
        }
    },
    getStatus() {
        getAuthStatus().then((res) => {
            this.setData({
                status: res.data,
            });
        });
    },
    back() {
        wx.navigateBack();
    },
    goPage(event) {
        let path = event.currentTarget.dataset.path;
        path = path + "?type=" + this.data.type;
        wx.navigateTo({
            url: path,
        });
    },
    photoAuthFn() {
        if (this.data.status.idAuth != 3) {
            showFn({
                message: "请先完成实名认证",
            });
            return;
        }
        wx.navigateTo({
            url: "/pages_my/data_edit/index",
        });
    },
    async idAuth() {
        let self = this;
        const res = await getEidToken();
        startEid({
            data: {
                token: res.data,
            },
            verifyDoneCallback(res) {
                const { token, verifyDone } = res;
                if (verifyDone) {
                    if (self.data.type == 1) {
                        idApply({
                            eidToken: token,
                        }).then((rt) => {
                            self.setData({
                                showMsg2: true,
                            });
                            self.getStatus();
                        });
                    } else {
                        self.setData({
                            userInfo: {
                                ...self.data.userInfo,
                                eidToken: token,
                                verifyDone,
                            },
                        });
                    }
                }
            },
        });
    },

    submit() {
        if (!this.data.userInfo?.eidToken) {
            wx.showToast({
                title: "请完成身份认证",
                icon: "none",
            });
            return;
        }
        // if (!this.data.userInfo?.eduRequest?.school&&!this.data.userInfo?.eduCode) {
        //   wx.showToast({
        //     title: '请完成学历认证',
        //     icon:'none'
        //   })
        //   return
        // }

        wx.login({
            success: (res) => {
                let data = {
                    code: res.code,
                    ...this.data.userInfo,
                };
                wxAppletRegister(data).then((res) => {
                    wx.login({
                        success: (res) => {
                            wxAppletLogin({
                                code: res.code,
                            }).then((res) => {
                                removeStorageItem();
                                wx.setStorageSync("token", res.data.token);
                                getUserInfo().then((rt2) => {
                                    wx.setStorageSync("userInfo", rt2.data);
                                    wx.setStorageSync(
                                        "city",
                                        rt2.data.residentialCity
                                    );
                                });
                                this.setData({
                                    showMsg: true,
                                });
                            });
                        },
                    });
                });
            },
        });
    },
    onConfirmMsg() {
        wx.reLaunch({
            url: "/pages/index/index",
        });
    },
    onCloseMsg() {
        this.setData({
            showMsg: false,
        });
    },
    // 获取返回按钮距离顶部位置
    backBtnTop() {
        const systemInfo = wx.getWindowInfo();
        const statusBarHeight = systemInfo.statusBarHeight; // 状态栏高度
        const menuButtonInfo = wx.getMenuButtonBoundingClientRect(); // 胶囊按钮信息
        // 计算导航栏高度
        const navBarHeight =
            menuButtonInfo.top - statusBarHeight + menuButtonInfo.top;
        this.setData({
            top: navBarHeight,
        });
    },
});
