// pages_my/data_edit/index.js
import { formatDate, previewMedia } from "../../utils/utils";
import { sysconfigValue, getWechatPhone } from "../../pages/api/common";
import { getUserInfo, editUserInfo } from "../api/index";
import uploadFilePromise from "../../utils/upload.js";
import { env } from "../../env";
import { showFn } from "../../utils/myHttpMessage";
Page({
    data: {
        imgBaseURL: "",
        userInfo: {},
        show1: false,
        actions1: [
            {
                name: "男",
            },
            {
                name: "女",
            },
        ],
        show2: false,
        currentDate: new Date("2000-01").getTime(),
        minDate: new Date(1970).getTime(),
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
        show3: false,
        actions3: [],
        show4: false,
        actions4: [],
        show7: false,
        actions7: [],
        showMsg: false,
        photoListFM: [],
        photoList: [
            {
                url: "",
            },
        ],
        showTip: false,
        show: false,
        // 用于替换图片，临时存放index
        tempIdx: -1,
        // 上传类型：'cover' 封面，'photo' 生活照
        uploadType: "photo",
        // 生活照数量
        photoCount: 0,
        // 是否显示添加生活照按钮
        showAddPhotoBtn: true,
        // 相册上传选项
        photoActions: [
            {
                name: "拍摄",
            },
            {
                name: "从手机相册选择",
            },
        ],
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        this.setActions3();
        this.setActions4();
        this.getSysValue();
        this.getInfo();
    },
    setActions3() {
        let tempArr = [];
        for (let index = 150; index <= 200; index++) {
            tempArr.push(index + "cm");
        }
        this.setData({
            actions3: tempArr,
        });
    },
    setActions4() {
        let tempArr = [];
        for (let index = 40; index <= 120; index++) {
            tempArr.push(index + "kg");
        }
        this.setData({
            actions4: tempArr,
        });
    },
    back() {
        wx.navigateBack();
    },
    // 更新生活照数量和显示状态
    updatePhotoCount() {
        const photoCount = this.data.photoList.filter(
            (p, idx) => idx > 1 && p.url
        ).length;
        this.setData({
            photoCount: photoCount,
            showAddPhotoBtn: photoCount < 9,
        });
    },
    // 获取用户详情
    getInfo() {
        getUserInfo().then((res) => {
            let tempFM = [];
            let temp = res.data.personalPhoto.map((item) => ({
                url: item.url,
            }));
            if (temp.length >= 1) {
                tempFM[0] = {
                    url: temp[0].url,
                };
            }
            temp.unshift({
                url: "",
            });

            this.setData({
                userInfo: res.data,
                photoList: temp,
                photoListFM: tempFM,
            });
            this.updatePhotoCount();
        });
    },
    getSysValue() {
        sysconfigValue({
            code: "label_json",
        }).then((res) => {
            let temp = JSON.parse(res.data);
            let profession = temp.profession.map((item) => ({
                name: item,
                value: item,
            }));
            let annualSalary = temp.annualSalary.map((item) => ({
                name: item,
                value: item,
            }));
            this.setData({
                actions7: profession.map((item) => item.value),
                actions8: annualSalary.map((item) => item.value),
            });
        });
    },

    // 点击上传原相机视频
    async uploadVideo() {
        let self = this;
        wx.chooseMedia({
            count: 1,
            mediaType: ["video"],
            sourceType: ["camera"],
            camera: "front",
            maxDuration: 60,
            success: async (res1) => {
                if (!res1.tempFiles || res1.tempFiles.length === 0) {
                    return;
                }
                wx.openVideoEditor({
                    filePath: res1.tempFiles[0].tempFilePath,
                    maxDuration: 60,
                    success: async (res2) => {
                        let temp = {
                            fileType: "video",
                            size: res2.size,
                            tempFilePath: res2.tempFilePath,
                        };
                        wx.showLoading({
                            title: "上传中...",
                            mask: true,
                        });
                        try {
                            // 调用文件上传方法
                            const result = await uploadFilePromise(temp);
                            // 更新用户信息中的原相机视频
                            self.setData({
                                userInfo: {
                                    ...self.data.userInfo,
                                    cameraImg: result,
                                },
                            });
                        } catch (error) {
                            // 捕获错误并打印日志
                            console.error("上传失败:", error);
                            // 提示用户上传失败
                            wx.showToast({
                                title: "上传文件发生错误",
                                icon: "none",
                            });
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
    preview() {
        let urls = [
            {
                url: this.data.userInfo.cameraImg,
                type: "video",
            },
        ];
        previewMedia(urls, 0);
    },
    deleteVideo() {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                cameraImg: null,
            },
        });
    },
    // 上传头像
    async afterRead(event) {
        const { file } = event.detail;
        wx.showLoading({
            title: "上传中...",
            mask: true,
        });
        try {
            // 调用文件上传方法
            const result = await uploadFilePromise(file);
            // 更新用户信息中的头像字段
            this.setData({
                userInfo: {
                    ...this.data.userInfo,
                    avatar: result,
                },
            });
        } catch (error) {
            // 捕获错误并打印日志
            console.error("头像上传失败:", error);
            // 提示用户上传失败
            wx.showToast({
                title: "上传文件发生错误",
                icon: "none",
            });
        } finally {
            wx.hideLoading();
        }
    },
    showFn(e) {
        // 获取上传类型和索引
        const uploadType = e.currentTarget.dataset.type || "photo";
        const index = e.currentTarget.dataset.index;
        this.setData({
            show: true,
            uploadType: uploadType,
            tempIdx: index !== undefined && index >= 0 ? index : -1,
        });
    },
    clickOverlay() {
        this.setData({
            show: false,
        });
        // 重置标志
        this._isSelectingPhoto = false;
    },
    // 选择照片来源
    onSelectPhotoSource(e) {
        // 防止重复调用 - 使用同步检查
        if (this._isSelectingPhoto) {
            return;
        }
        this._isSelectingPhoto = true;

        this.setData({
            show: false,
        });

        const index = e.detail.index;
        let sourceType = [];
        if (index === 0) {
            // 拍摄
            sourceType = ["camera"];
        } else if (index === 1) {
            // 从手机相册选择
            sourceType = ["album"];
        }

        // 延迟调用，确保弹窗已关闭
        setTimeout(() => {
            this.chooseImg({
                sourceType: sourceType,
            });
            // 重置标志
            this._isSelectingPhoto = false;
        }, 100);
    },
    // 选择图片（使用 register2 的方式）
    chooseImg(options) {
        let self = this;
        // options 可能是事件对象或配置对象
        let uploadType, count, index, sourceType;

        if (options && options.sourceType) {
            // 从 onSelectPhotoSource 调用
            uploadType = self.data.uploadType || "photo";
            sourceType = options.sourceType;
            count = 9;
            index = self.data.tempIdx;
        } else {
            // 从事件调用（替换图片）- 此分支现在不会被执行，因为所有按钮都调用 showFn
            // 保留此分支是为了代码的健壮性，防止未来有直接调用的情况
            const e = options;
            uploadType =
                e.currentTarget.dataset.type || self.data.uploadType || "photo";
            count = e.currentTarget.dataset.count || 9;
            index = e.currentTarget.dataset.index;
            sourceType = ["album", "camera"]; // 默认两个都支持
        }

        if (uploadType === "cover") {
            count = 1;
        } else {
            // 生活照：计算还能上传多少张（排除占位和封面）
            const currentCount = self.data.photoList.filter(
                (p, idx) => idx > 1 && p.url
            ).length;
            count = count - currentCount;
        }

        if (index !== undefined && index >= 0) {
            self.setData({
                tempIdx: index,
                uploadType: uploadType,
            });
        } else {
            self.setData({
                tempIdx: -1,
                uploadType: uploadType,
            });
        }
        // 弹窗已在 onSelectPhotoSource 中关闭，这里不需要再次关闭

        // 确保 sourceType 只包含一个选项，避免 wx.chooseMedia 再次弹出选择框
        if (!sourceType || sourceType.length === 0) {
            sourceType = ["album"]; // 默认使用相册
        } else if (sourceType.length > 1) {
            // 如果包含多个选项，只取第一个，避免重复弹窗
            sourceType = [sourceType[0]];
        }

        wx.chooseMedia({
            count: count,
            mediaType: ["image"],
            sourceType: sourceType,
            success: async (res) => {
                wx.showLoading({
                    title: "上传中...",
                    mask: true,
                });
                try {
                    if (uploadType === "cover") {
                        // 上传封面
                        let photoListFM = [...self.data.photoListFM];
                        let photoList = [...self.data.photoList];

                        for (const item of res.tempFiles) {
                            try {
                                const result = await uploadFilePromise(item);
                                if (
                                    self.data.tempIdx != -1 &&
                                    self.data.tempIdx < photoListFM.length
                                ) {
                                    photoListFM.splice(self.data.tempIdx, 1, {
                                        url: result,
                                    });
                                } else {
                                    photoListFM = [{ url: result }];
                                }
                                // 更新 photoList 的封面（索引1，因为索引0是空占位）
                                // 确保 photoList 至少有2个元素（占位 + 封面）
                                if (photoList.length <= 1) {
                                    photoList.push({ url: result });
                                } else {
                                    photoList[1] = { url: result };
                                }
                                self.setData({
                                    photoListFM: photoListFM,
                                    photoList: photoList,
                                });
                                self.updatePhotoCount(); // 保持一致性，虽然封面不影响生活照数量
                            } catch (error) {
                                console.error("文件上传失败:", error);
                                wx.showToast({
                                    title: "上传失败：图片过大，需小于6MB",
                                    icon: "none",
                                });
                            }
                        }
                    } else {
                        // 上传生活照
                        let photoList = [...self.data.photoList];
                        // 计算当前生活照数量（排除占位和封面）
                        const currentPhotoCount = photoList.filter(
                            (p, idx) => idx > 1 && p.url
                        ).length;

                        for (const item of res.tempFiles) {
                            if (currentPhotoCount >= 9) break;
                            try {
                                const result = await uploadFilePromise(item);
                                if (
                                    self.data.tempIdx != -1 &&
                                    self.data.tempIdx >= 0
                                ) {
                                    // 替换指定位置的图片，实际索引需要 +2（跳过占位和封面）
                                    const actualIdx = self.data.tempIdx + 2;
                                    if (actualIdx < photoList.length) {
                                        photoList.splice(actualIdx, 1, {
                                            url: result,
                                        });
                                    } else {
                                        photoList.push({ url: result });
                                    }
                                } else {
                                    photoList.push({ url: result });
                                }
                                self.setData({
                                    photoList: photoList,
                                });
                                self.updatePhotoCount();
                            } catch (error) {
                                console.error("文件上传失败:", error);
                                wx.showToast({
                                    title: "上传失败：图片过大，需小于6MB",
                                    icon: "none",
                                });
                            }
                        }
                    }
                } finally {
                    wx.hideLoading();
                }
            },
        });
    },
    // 删除图片
    removeImg(e) {
        const index = e.currentTarget.dataset.index;
        const uploadType = e.currentTarget.dataset.type || "photo";

        if (uploadType === "cover") {
            let photoListFM = [...this.data.photoListFM];
            let photoList = [...this.data.photoList];
            photoListFM.splice(index, 1);
            // 清空 photoList 中对应的封面（索引1）
            if (photoList.length > 1) {
                photoList[1] = { url: "" };
            }
            this.setData({
                photoListFM: photoListFM,
                photoList: photoList,
            });
        } else {
            // 删除生活照，实际索引需要 +2（跳过占位和封面）
            let photoList = [...this.data.photoList];
            const actualIdx = index + 2;
            if (actualIdx < photoList.length) {
                photoList.splice(actualIdx, 1);
            }
            this.setData({
                photoList: photoList,
            });
            this.updatePhotoCount();
        }
    },
    goPage(event) {
        let dataset = event.currentTarget.dataset;
        let path = dataset.path;
        if (dataset.type) {
            path = path + "?type=" + dataset.type;
        }
        wx.navigateTo({
            url: path,
        });
    },
    goPage2(event) {
        if (this.submit() !== 1) {
            setTimeout(() => {
                let dataset = event.currentTarget.dataset;
                let path = dataset.path;
                if (dataset.type) {
                    path = path + "?type=" + dataset.type;
                }
                wx.navigateTo({
                    url: path,
                });
            }, 300);
        }
    },
    changeShow1() {
        // 性别不能修改，此方法保留但不执行任何操作
    },
    onClose1() {
        this.setData({
            show1: false,
        });
    },
    onSelect1(value) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                sex: value.detail.name,
            },
        });
    },
    changeshow2() {
        // 出生年月不能修改，此方法保留但不执行任何操作
    },
    onConfirm2(event) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                birthDate: formatDate(event.detail, "yyyy-MM"),
            },
            show2: false,
            currentDate: event.detail,
        });
    },
    onClose2() {
        this.setData({
            show2: false,
        });
    },
    changeshow3() {
        this.setData({
            show3: true,
        });
    },
    onClose3() {
        this.setData({
            show3: false,
        });
    },
    onSelect3() {
        let picker = this.selectComponent(".picker3");
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                height: picker.getValues()[0].replace(/[^0-9]/g, ""),
            },
        });
        this.onClose3();
    },
    changeshow4() {
        this.setData({
            show4: true,
        });
    },
    onClose4() {
        this.setData({
            show4: false,
        });
    },
    onSelect4() {
        let picker = this.selectComponent(".picker4");
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                weight: picker.getValues()[0].replace(/[^0-9]/g, ""),
            },
        });
        this.onClose4();
    },
    changeshow7() {
        this.setData({
            show7: true,
        });
    },
    onClose7() {
        this.setData({
            show7: false,
        });
    },
    onSelect7(value) {
        let picker = this.selectComponent(".picker7");
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                jobMes: picker.getValues()[0],
            },
        });
        this.onClose7();
    },
    iptChange1(value) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                nickName: value.detail,
            },
        });
    },
    iptChange2(value) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                wechat: value.detail,
            },
        });
    },
    iptChange4(value) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                registrationNo: value.detail,
            },
        });
    },
    onChangeSwitch(e) {
        this.setData({
            userInfo: {
                ...this.data.userInfo,
                wechatOpen: e.detail,
            },
        });
    },
    showTipFn() {
        this.setData({
            showTip: true,
        });
    },
    submit() {
        let temp = {
            ...this.data.userInfo,
        };
        if (!temp.nickName || temp.nickName.length > 8) {
            let msg = "请输入昵称且不超过8个字";
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return 1;
        }
        temp.personalPhoto = this.data.photoList
            .filter((item) => item.url)
            .map((item) => item.url);
        if (temp.personalPhoto.length < 1) {
            let msg = "至少上传一张照片";
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return 1;
        }
        if (!this.data.photoListFM.length) {
            let msg = "请上传封面照片";
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return 1;
        }
        editUserInfo(temp).then((res) => {
            wx.showToast({
                title: "保存成功",
                icon: "none",
            });
            setTimeout(() => {
                this.getInfo();
            }, 1500);
        });
    },
    onCloseMsg() {
        this.setData({
            showMsg: false,
        });
    },
    getPhoneNumber(value) {
        let self = this;
        const code = value.detail.code;
        if (code) {
            getWechatPhone({
                code,
            }).then((res) => {
                self.setData({
                    userInfo: {
                        ...self.data.userInfo,
                        phone: res.data,
                    },
                });
            });
        }
    },
    preventTouchMove() {
        return false; // 阻止触摸事件
    },
    // 复制邀请码
    copy() {
        wx.setClipboardData({
            data: this.data.userInfo.distributionCode,
        });
    },
    goIdAuth() {
        wx.navigateTo({
            url: "/pages_register/register3/index?type=身份认证",
        });
    },
});
