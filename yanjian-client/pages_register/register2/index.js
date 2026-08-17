// pages_register/register/register2/index.js
import uploadFilePromise from "../../utils/upload.js";
import { env } from "../../env";
Page({
    data: {
        imgBaseURL: "",
        userInfo: {
            personalPhoto: [],
        },
        show: false,
        // 用于替换图片，临时存放index
        tempIdx: -1,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        let userInfo = wx.getStorageSync("tempUserInfo");
        if (userInfo) {
            this.setData({
                userInfo: {
                    personalPhoto: [],
                    ...userInfo,
                },
            });
        }
    },
    back() {
        wx.navigateBack();
    },
    showFn() {
        this.setData({
            show: true,
        });
    },
    clickOverlay() {
        this.setData({
            show: false,
        });
    },
    chooseImg(e) {
        let self = this;
        let count =
            e.currentTarget.dataset.count ||
            9 - self.data.userInfo.personalPhoto.length;
        if (e.currentTarget.dataset.index >= 0) {
            self.setData({
                tempIdx: e.currentTarget.dataset.index,
            });
        } else {
            self.setData({
                tempIdx: -1,
            });
        }
        self.clickOverlay();
        let personalPhoto = self.data.userInfo.personalPhoto || [];
        wx.chooseMedia({
            count: count,
            mediaType: ["image"],
            sourceType: ["album", "camera"],
            success: async (res) => {
                wx.showLoading({
                    title: "上传中...",
                    mask: true,
                });
                try {
                    for (const item of res.tempFiles) {
                        if (personalPhoto.length >= 9) break;
                        try {
                            // 上传文件
                            const result = await uploadFilePromise(item);
                            if (self.data.tempIdx != -1) {
                                personalPhoto.splice(
                                    self.data.tempIdx,
                                    1,
                                    result
                                );
                            } else {
                                personalPhoto.push(result);
                            }
                            self.setData({
                                userInfo: {
                                    ...self.data.userInfo,
                                    personalPhoto,
                                },
                            });
                        } catch (error) {
                            console.error("文件上传失败:", error);
                            wx.showToast({
                                title: "上传失败：图片过大，需小于6MB",
                                icon: "none",
                            });
                        }
                    }
                } finally {
                    wx.hideLoading();
                }
            },
        });
    },
    chooseVideo() {
        let self = this;
        wx.chooseMedia({
            count: 1,
            mediaType: ["video"],
            sourceType: ["camera"],
            camera: "front",
            maxDuration: 60,
            success: async (res) => {
                wx.openVideoEditor({
                    filePath: res.tempFiles[0].tempFilePath,
                    maxDuration: 60,
                    success: async (res) => {
                        let temp = {
                            fileType: "video",
                            size: res.size,
                            tempFilePath: res.tempFilePath,
                        };
                        wx.showLoading({
                            title: "上传中...",
                            mask: true,
                        });
                        try {
                            // 上传文件
                            const result = await temp;
                            self.setData({
                                userInfo: {
                                    ...self.data.userInfo,
                                    cameraImg: result,
                                },
                            });
                        } catch (error) {
                            console.error("文件上传失败:", error);
                        } finally {
                            wx.hideLoading();
                        }
                    },
                    fail: (err) => {
                        console.log(err);
                    },
                });
            },
        });
    },
    removeImg(e) {
        let personalPhoto = this.data.userInfo.personalPhoto;
        personalPhoto.splice(e.currentTarget.dataset.index, 1);
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                personalPhoto,
            },
        });
    },
    removeVideo() {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                cameraImg: null,
            },
        });
    },
    nextStep() {
        if (this.validate()) {
            wx.setStorageSync("tempUserInfo", this.data.userInfo);
            wx.navigateTo({
                url: "/pages_register/register3/index",
            });
        }
    },
    validate() {
        let msg = "";
        let userInfo = this.data.userInfo;
        console.log(userInfo);
        if (userInfo.personalPhoto.length < 1) {
            msg = "至少上传1张照片";
        }
        if (msg) {
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return false;
        } else {
            return true;
        }
    },
});
