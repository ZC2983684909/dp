// pages_register/register/register3/index.js
import {
    editUserInfo,
    wxAppletRegister,
    getEidToken,
    idAuthCheck,
    idApply,
    idInfo,
} from "../api/index";
import { removeStorageItem } from "../../utils/utils";
import { getAddress } from "../../utils/getAddress";
import { wxAppletLogin, getUserInfo } from "../../pages/api/index";
import { env } from "../../env";
import { startEid } from "../../mp_ecard_sdk/main";
Page({
    data: {
        imgBaseURL: "",
        userInfo: {
            name: "",
            idCard: "",
        },
        // 身份认证状态，-1：未认证，1：成功，0：失败
        cradStatus: -1,
        // type=身份认证的时候，就是单纯的认证个人信息
        type: "",
        errMsg: "",
        // 打个标记，如果已经认证成功了，进来应该先显示重新认证，重新认证再次成功就显示认证成功
        flag: 0,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
            type: options.type || "",
        });
        if (!options.type) {
            let userInfo = wx.getStorageSync("tempUserInfo");
            if (userInfo) {
                this.setData({
                    userInfo: {
                        ...userInfo,
                        name: "",
                        idCard: "",
                    },
                });
            }
        } else {
            this.getIdInfo();
        }
    },
    onShow() {},
    back() {
        wx.navigateBack();
    },
    getIdInfo() {
        idInfo().then((res) => {
            const { name, idCard } = res.data;
            if (name && idCard) {
                this.setData({
                    userInfo: {
                        ...this.data.userInfo,
                        name,
                        idCard,
                    },
                    cradStatus: 1,
                });
            }
        });
    },
    onChange1(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                name: e.detail,
            },
        });
    },
    onChange2(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                idCard: e.detail,
            },
        });
    },
    async skipRegister() {
        wx.login({
            success: (res) => {
                const params = {
                    ...this.data.userInfo,
                    code: res.code,
                };
                const sex = wx.getStorageSync("sex");
                if (sex) {
                    params.sex = sex;
                }
                // 如果存在邀请人id就把邀请人id传进去
                let inviterId = wx.getStorageSync("inviterId");
                if (inviterId) {
                    params.inviterId = inviterId;
                }
                console.log(params,"params");
                wxAppletRegister(params).then((rt1) => {
                    wx.removeStorageSync("tempUserInfo");
                    this.loginFn();
                });
            },
        });
    },
    validate() {
        let msg = "";
        if (!this.data.userInfo.name) {
            msg = "请输入姓名";
        } else if (
            !this.data.userInfo.idCard ||
            this.data.userInfo.idCard.length != 18
        ) {
            msg = "请正确输入身份证号码";
        }
        if (msg) {
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return false;
        }
        return true;
    },
    async nextStep() {
        let self = this;
        if (!self.validate()) {
            return;
        }
        if (self.data.cradStatus == 1) {
            self.skipRegister();
        } else {
            self.idAuth();
        }
    },
    loginFn() {
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
                            wx.reLaunch({
                                url: "/pages/index/index",
                            });
                        });
                    })
                    .catch((err) => {
                        console.log(err);
                    });
            },
        });
    },
    async idAuth() {
      try {
        const res = await getEidToken();
        startEid({
          data: {
            token: res.data,
          },
          verifyDoneCallback: ({ token, verifyDone }) => {
            if (verifyDone) {
              this.setData({
                cradStatus: 1,
                userInfo: {
                  ...this.data.userInfo,
                  eidToken: token,
                  verifyDone,
                },
              });
              wx.showToast({
                title: "认证完成，请继续提交",
                icon: "none",
              });
            }
          },
        });
      } catch (error) {
        wx.showToast({
          title: error.msg || "身份认证服务暂不可用",
          icon: "none",
        });
      }
    },
});
