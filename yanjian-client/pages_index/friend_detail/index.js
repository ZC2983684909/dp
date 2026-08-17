// pages_index/friend_detail/index.js
import {
    tuserStar,
    tuserCancelStar,
    usershield,
    wechatPre,
    userchatPre,
    applyUserChat,
} from "../api/index";
import { personalCenter, tuserDetail } from "../../pages/api/index";
import { applyWX } from "../../pages/api/common.js";
import { env } from "../../env";
import { previewMedia } from "../../utils/utils.js";
Page({
    data: {
        imgBaseURL: "",
        isMe: 2, //1.个人中心看自己，2，看别人的
        id: "",
        tuserInfo: {
            personalPhoto: [],
        },
        isShow1: false,
        wechatPreData: {},
        isShow2: false,
        actions: [
            {
                name: "举报",
            },
            {
                name: "屏蔽",
            },
        ],
        isShow3: false,
        isShow4: false,
        userChatPreData: {},
        isAllTag: true,
        shareImg: "",
        showVipDialog: false,
        showWechatDialog: false,
        showTip: false,
        showTip2: false,
    },
    onLoad(options) {
        this.setData({
            id: options.id,
            isMe: options.isMe || 2,
            imgBaseURL: env.imgBaseURL,
        });
    },
    onShow() {
        this.getData();
    },
    /* 分享到好友 */
    async onShareAppMessage() {
        let temp = wx.getStorageSync("userInfo");
        let shareTit = this.transShareTitle();
        await this.cutShareImg(this.data.tuserInfo.personalPhoto[0].url);
        return {
            title: shareTit, //标题
            path: `pages_index/friend_detail/index?id=${this.data.tuserInfo.id}&inviterId=${temp.id}`, //路由地址以及参数
            imageUrl: this.data.shareImg, //封面图片
            // imageUrl: this.data.tuserInfo.personalPhoto[0].url, //封面图片
        };
    },
    /* 分享到朋友圈 */
    async onShareTimeline() {
        let temp = wx.getStorageSync("userInfo");
        let shareTit = this.transShareTitle();
        await this.cutShareImg(this.data.tuserInfo.personalPhoto[0].url);
        return {
            title: shareTit, //标题
            path: `pages_index/friend_detail/index?id=${this.data.tuserInfo.id}&inviterId=${temp.id}`, //路由地址以及参数
            imageUrl: this.data.shareImg, //封面图片
        };
    },
    transShareTitle() {
        let txt = [];
        let temp = ["city", "jobMes", "age", "height", "weight"];
        temp.forEach((item) => {
            let t = this.data.tuserInfo[item];
            console.log(t, "====");
            if (t && t !== null && t !== "null") {
                if (item == "age") {
                    txt.push(t + "岁");
                } else if (item == "height") {
                    txt.push(t + "cm");
                } else if (item == "weight") {
                    txt.push(t + "kg");
                } else {
                    txt.push(t);
                }
            }
        });
        return txt.join(" · ");
    },
    getData() {
        tuserDetail(this.data.id)
            .then((res) => {
                const { fondTagsList } = res.data;
                let temp = {
                    ...res.data,
                    myFondTagsFormat: fondTagsList !== null ? fondTagsList : [],
                };
                if (
                    temp.userArticleViewResponse &&
                    temp.userArticleViewResponse.articleImg.length
                ) {
                    let arr = Array.from(
                        temp.userArticleViewResponse.articleImg
                    );
                    temp.articleImg = arr.splice(0, 4);
                }
                temp.selfDescription = JSON.stringify(temp.selfDescription)
                    .replace(/\\n/g, "<br/>")
                    .replace(/"/g, "");
                temp.idealFriend = JSON.stringify(temp.idealFriend)
                    .replace(/\\n/g, "<br/>")
                    .replace(/"/g, "");
                if (temp.cameraImg !== null) {
                    let arr = [
                        {
                            url: temp.cameraImg,
                            type: "video",
                        },
                    ];
                    if (temp.personalPhoto.length > 1) {
                        arr.push(temp.personalPhoto[1]);
                    }
                    temp.personalPhoto.splice(1, 1, ...arr);
                }
                this.setData({
                    tuserInfo: temp,
                });
            })
            .catch((err) => {
                wx.showToast({
                    title: err.msg,
                    icon: "none",
                    duration: 2600,
                    mask: true,
                });
                setTimeout(() => {
                    wx.navigateBack();
                }, 2500);
            });
    },
    copyFn() {
        if (this.data.tuserInfo.applyStatus != 2) {
            wx.showToast({
                title: "申请通过后可查看",
                icon: "none",
            });
            return;
        }
        this.setData({
            showWechatDialog: true,
        });
    },
    closeWechat() {
        this.setData({
            showWechatDialog: false,
        });
    },
    showAll() {
        let temp = [...this.data.tuserInfo.fondTagsFormat];
        this.setData({
            isAllTag: !this.data.isAllTag,
            tuserInfo: {
                ...this.data.tuserInfo,
                myFondTagsFormat: this.data.isAllTag ? temp.splice(0, 5) : temp,
            },
        });
    },
    onClickLeft() {
        wx.navigateBack({
            delta: 1,
            fail: (err) => {
                wx.reLaunch({
                    url: "/pages/index/index",
                });
            },
        });
    },
    goPage(event) {
        let dataset = event.currentTarget.dataset;
        let path = dataset.path;
        path += `${path.includes("?") ? "&" : "?"}userId=${this.data.tuserInfo.id}`;
        wx.navigateTo({
            url: path,
        });
      
    },
    goIm() {
        const { isChat, id, nickName } = this.data.tuserInfo;
        if (isChat) {
          wx.navigateTo({
            url: `/pages_message/IM/index?userId=${id}&nickName=${nickName}`,
          });
        } else {
            userchatPre({
                userId: this.data.tuserInfo.id,
            }).then((res) => {
                this.setData({
                    userChatPreData: res.data,
                    isShow4: true,
                });
            });
        }
    },
    preview(event) {
        const dataset = event.currentTarget.dataset;
        if (dataset.type == "image") {
            let index = dataset.index;
            if (this.data.tuserInfo.cameraImg !== null) {
                if (index > 1) {
                    index--;
                }
                let temp = [...this.data.tuserInfo.personalPhoto];
                temp.splice(1, 1);
                let imgs = temp.map((item) => {
                    return {
                        url: item.url,
                        type: "image",
                    };
                });
                previewMedia(imgs, index);
            } else {
                let imgs = this.data.tuserInfo.personalPhoto.map((item) => {
                    return {
                        url: item.url,
                        type: "image",
                    };
                });
                previewMedia(imgs, index);
            }
        } else {
            personalCenter().then(() => {
                if (!this.data.tuserInfo.seeCameraImg) {
                    this.setData({
                        showVipDialog: true,
                    });
                    return;
                }
                let imgs = [
                    {
                        url: this.data.tuserInfo.cameraImg,
                        type: "video",
                    },
                ];
                previewMedia(imgs, 0);
            });
        }
    },
    closeVipDialog() {
        this.setData({
            showVipDialog: false,
        });
    },

    onChangeShow2() {
        this.setData({
            isShow2: true,
        });
    },
    onClose2() {
        this.setData({
            isShow2: false,
        });
    },
    onSelect2(event) {
        if (event.detail.name == "举报") {
            wx.navigateTo({
                url: `/pages_index/report/index?id=${this.data.tuserInfo.id}&type=1`,
            });
        } else {
            this.shield();
        }
    },
    showTipFn() {
        this.setData({
            showTip: true,
        });
    },
    shield() {
        this.setData({
            isShow3: true,
        });
    },
    close3() {
        this.setData({
            isShow3: false,
        });
    },
    confirm3() {
        usershield(this.data.tuserInfo.id).then((res) => {
            const pages = getCurrentPages();
            const prePage = pages[pages.length - 2];
            if (prePage.getList && prePage.data.params.pageIndex) {
                prePage.setData({
                    params: {
                        ...prePage.data.params,
                        pageIndex: 1,
                    },
                });
                prePage.getList();
            }
            wx.navigateBack();
        });
    },
    isAllTagFn() {
        let isAllTag = this.data.isAllTag;
        this.setData({
            isAllTag: !isAllTag,
        });
    },
    // 关注
    star() {
        personalCenter().then((res) => {
            let isStar = this.data.tuserInfo.isStar;
            if (isStar) {
                tuserCancelStar(this.data.id).then((res) => {
                    this.setData({
                        tuserInfo: {
                            ...this.data.tuserInfo,
                            isStar: !isStar,
                        },
                    });
                    this.getData();
                });
            } else {
                tuserStar(this.data.id).then((res) => {
                    this.setData({
                        tuserInfo: {
                            ...this.data.tuserInfo,
                            isStar: !isStar,
                        },
                    });
                    this.getData();
                });
            }
        });
    },
    // 添加微信
    addWx() {
        let applyStatus = this.data.tuserInfo.applyStatus;
        if (applyStatus == 2 || applyStatus == 1) {
            return;
        }
        wechatPre({
            userId: this.data.tuserInfo.id,
        }).then((res) => {
            this.setData({
                wechatPreData: res.data,
                isShow1: true,
            });
        });
    },
    wxBtn1() {
        // 等后续跳转到vip开通页面
        const { isVip, vipCount, wechatOpen } = this.data.wechatPreData;
        if (!isVip) {
            wx.navigateTo({
                url: "/pages_my/open_vip/index",
            });
            return;
        }
        if (vipCount > 1) {
            if (wechatOpen) {
                this.applyWxFn(2);
                } else {
                this.requestWechatApplication(2);
                this.setData({
                    isShow1: false,
                });
            }
        } else {
            wx.showToast({
                title: "vip免费解锁次数已用完，请使用单次解锁",
                icon: "none",
            });
        }
    },
    wxBtn2() {
        const { wechatOpen, balance, price } = this.data.wechatPreData;
        if (balance < price) {
            wx.navigateTo({
                url: "/pages_my/applay_num/index",
            });
            return;
        } else {
            if (wechatOpen) {
                this.applyWxFn(1);
            } else {
                this.requestWechatApplication(1);
                this.setData({
                    isShow1: false,
                });
            }
        }
    },
    requestWechatApplication(way) {
        wx.showModal({
            title: "申请微信",
            editable: true,
            placeholderText: "请输入申请说明",
            success: (modalRes) => {
                if (modalRes.confirm) {
                    this.applyWxFn(way, modalRes.content || "很想认识你，希望通过申请");
                }
            },
        });
    },
    applyWxFn(way, applyDesc) {
        let data = {
            applyUserId: this.data.tuserInfo.id,
            applyWay: way,
            applyDesc,
        };
        applyWX(data).then((res) => {
            this.getData();
            this.setData({
                isShow1: false,
            });
            wx.showToast({
                title: "解锁成功",
                icon: "none",
            });
        });
    },
    onClose1() {
        this.setData({
            isShow1: false,
        });
    },
    confirm1() {
        this.close1();
    },

    userBtn1() {
        // 等后续跳转到vip开通页面
        const { isVip, vipCount } = this.data.userChatPreData;
        if (!isVip) {
            wx.navigateTo({
                url: "/pages_my/open_vip/index",
            });
            return;
        }
        if (vipCount > 1) {
            this.postApplyUserChat(2);
        } else {
            wx.showToast({
                title: "vip免费解锁次数已用完，请使用单次解锁",
                icon: "none",
            });
        }
    },
    userBtn2() {
        const { balance, price } = this.data.userChatPreData;
        if (balance < price) {
            wx.navigateTo({
                url: "/pages_my/applay_num/index",
            });
            return;
        } else {
            this.postApplyUserChat(1);
        }
    },
    postApplyUserChat(type) {
        applyUserChat({
            applyUserId: this.data.tuserInfo.id,
            applyWay: type,
        }).then((res) => {
            this.onClose4();
            const { id, nickName } = this.data.tuserInfo;
            wx.navigateTo({
                url:
                    "/pages_message/IM/index?userId=" +
                    id +
                    "&nickName=" +
                    nickName,
            });
        });
    },

    onClose4() {
        this.setData({
            isShow4: false,
        });
    },

    showMsgFn() {
        this.setData({
            showTip2: true,
        });
    },

    cutShareImg(doctorImg) {
        let that = this;
        return new Promise((resolve, reject) => {
            console.log("[cutShareImg] 开始处理图片:", doctorImg);

            if (!doctorImg) {
                const err = new Error("图片地址为空");
                console.error("[cutShareImg] 错误: 图片地址为空");
                wx.showToast({ title: "分享图片无效", icon: "none" });
                return reject(err);
            }

            wx.getImageInfo({
                src: doctorImg,
                success: (res) => {
                    console.log("[getImageInfo] 成功:", res);
                    const {
                        width: originalWidth,
                        height: originalHeight,
                        path,
                    } = res;

                    if (!originalWidth || !originalHeight) {
                        const err = new Error("图片尺寸无效");
                        console.error("[cutShareImg] 图片尺寸无效:", res);
                        wx.showToast({ title: "图片信息异常", icon: "none" });
                        return reject(err);
                    }

                    const TARGET_ASPECT_RATIO = 5 / 4;
                    const MAX_DIMENSION = 800;
                    let targetWidth, targetHeight;

                    if (originalWidth / originalHeight > TARGET_ASPECT_RATIO) {
                        targetWidth = Math.min(originalWidth, MAX_DIMENSION);
                        targetHeight = targetWidth / TARGET_ASPECT_RATIO;
                    } else {
                        targetHeight = Math.min(originalHeight, MAX_DIMENSION);
                        targetWidth = targetHeight * TARGET_ASPECT_RATIO;
                    }

                    console.log("[cutShareImg] 目标尺寸:", {
                        targetWidth,
                        targetHeight,
                    });

                    // 查询 canvas 节点
                    const query = wx.createSelectorQuery().in(that);
                    query
                        .select("#myCanvas")
                        .fields({ node: true, size: true })
                        .exec((canvasRes) => {
                            if (
                                !canvasRes ||
                                !canvasRes[0] ||
                                !canvasRes[0].node
                            ) {
                                const err = new Error("Canvas 节点未找到");
                                console.error(
                                    "[cutShareImg] Canvas 节点查询失败:",
                                    canvasRes
                                );
                                wx.showToast({
                                    title: "分享组件异常",
                                    icon: "none",
                                });
                                return reject(err);
                            }

                            const canvas = canvasRes[0].node;
                            const ctx = canvas.getContext("2d");
                            console.log("[cutShareImg] Canvas 获取成功");

                            canvas.width = targetWidth;
                            canvas.height = targetHeight;
                            ctx.imageSmoothingEnabled = false;

                            const image = canvas.createImage();
                            image.onload = () => {
                                console.log(
                                    "[cutShareImg] 图片加载成功，开始绘制"
                                );
                                const scale = Math.min(
                                    targetWidth / originalWidth,
                                    targetHeight / originalHeight
                                );
                                const drawWidth = originalWidth * scale;
                                const drawHeight = originalHeight * scale;

                                ctx.clearRect(0, 0, targetWidth, targetHeight);
                                ctx.drawImage(
                                    image,
                                    (targetWidth - drawWidth) / 2,
                                    (targetHeight - drawHeight) / 2,
                                    drawWidth,
                                    drawHeight
                                );

                                console.log(
                                    "[cutShareImg] 绘制完成，生成临时文件"
                                );

                                wx.canvasToTempFilePath({
                                    canvas,
                                    quality: 0.2,
                                    fileType: "jpg",
                                    success: (tempRes) => {
                                        console.log(
                                            "[canvasToTempFilePath] 成功:",
                                            tempRes.tempFilePath
                                        );
                                        that.setData({
                                            shareImg: tempRes.tempFilePath,
                                        });
                                        resolve(tempRes.tempFilePath);
                                    },
                                    fail: (err) => {
                                        console.error(
                                            "[canvasToTempFilePath] 失败:",
                                            err
                                        );
                                        wx.showToast({
                                            title: "分享图生成失败",
                                            icon: "none",
                                        });
                                        reject(
                                            new Error(
                                                "canvasToTempFilePath failed: " +
                                                    JSON.stringify(err)
                                            )
                                        );
                                    },
                                });
                            };

                            image.onerror = (err) => {
                                console.error(
                                    "[cutShareImg] 图片加载失败 (onerror):",
                                    err
                                );
                                wx.showToast({
                                    title: "图片加载失败",
                                    icon: "none",
                                });
                                reject(
                                    new Error(
                                        "Image load error: " +
                                            JSON.stringify(err)
                                    )
                                );
                            };

                            console.log("[cutShareImg] 设置 image.src:", path);
                            image.src = path;
                        });
                },
                fail: (err) => {
                    console.error("[getImageInfo] 失败:", err);
                    wx.showToast({ title: "图片加载失败" });
                    reject(
                        new Error("getImageInfo failed: " + JSON.stringify(err))
                    );
                },
            });
        });
    },
});
