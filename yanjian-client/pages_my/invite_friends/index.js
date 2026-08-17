// pages_my/invite_friends/index.js
import { env } from "../../env";
import { getShare, distributionamount, wechatsceneToCode } from "../api/index";
import { sysconfigValue, getPoster } from "../../pages/api/common";
import { showFn } from "../../utils/myHttpMessage";
import { contactCustomerService } from "../../utils/utils";
Page({
    data: {
        imgBaseURL: "",
        tabAct: 0,
        info: {},
        process: 0,
        shareTotal: 0,
        isShow: false,
        posters: [],
        posterUrl: "",
        miniCode: "",
        showPoster: false,
        query: {
            pageIndex: 1,
            pageSize: 15,
        },
        list: [],
        scrollHeight: 0,
        totalPage: 0,
        isLoading: false,
        isLoadingMore: false,
        isPull: false,
        scrollTop: 0,
        refresherTriggered: false,
        showSheet: false,
        actions: [
            {
                name: "生成海报",
            },
            {
                name: "分享给微信好友",
                openType: "share",
            },
        ],
        showTip: false,
        showTip2: false,
        prizeRatioVal: {},
    },
    onLoad(options) {
        this.computeAvailableHeight();
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        this.getShareResult();
        this.getPosterFn();
        this.getSysconfigValue();
        this.loadData();
    },
    /* 分享到好友 */
    async onShareAppMessage() {
        await this.setPoster();
        let temp = wx.getStorageSync("userInfo");
        return {
            title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
            path: `pages/index/index?inviterId=` + temp.id,
            imageUrl: this.data.posterUrl,
        };
    },
    /* 分享到朋友圈 */
    async onShareTimeline() {
        await this.setPoster();
        let temp = wx.getStorageSync("userInfo");
        return {
            title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
            path: `pages/index/index?inviterId=` + temp.id,
            imageUrl: this.data.posterUrl,
        };
    },
    shareFn() {
        this.setData({
            showSheet: true,
        });
    },
    onClose() {
        this.setData({
            showSheet: false,
        });
    },

    onSelect() {
        this.setData({
            showSheet: false,
        });
        this.createPoster();
    },
    back() {
        wx.navigateBack();
    },
    goPage(e) {
        wx.navigateTo({
            url: e.currentTarget.dataset.path,
        });
    },
    getSysconfigValue() {
        sysconfigValue({
            code: "prizeRatio",
        }).then((res) => {
            this.setData({
                prizeRatioVal: JSON.parse(res.data),
            });
        });
    },
    getShareResult() {
        getShare().then((res) => {
            this.setData({
                info: res.data,
            });
        });
    },
    getMoney() {
        if (this.data.info.withdrawAbleAmount <= 0) {
            showFn({
                title: "提现",
                message: "可提现金额为0，无法提现，请多分享好友赚取更多奖励吧",
            });

            return;
        }
        showFn(
            {
                title: "提现",
                message: "请联系人工客服提现",
                btnStr: "联系人工客服",
            },
            () => {
                contactCustomerService();
            }
        );
    },
    getPosterFn() {
        getPoster().then((res) => {
            this.setData({
                posters: res.data,
                posterUrl: res.data[0],
            });
        });
    },
    close() {
        this.setData({
            isShow: false,
        });
    },
    confirm() {
        wx.setClipboardData({
            data: this.data.code,
        });
        this.setData({
            isShow: false,
        });
    },
    // 复制邀请码
    copy() {
        wx.setClipboardData({
            data: this.data.info.distributionCode,
        });
    },
    loadData() {
        distributionamount(this.data.query)
            .then((res) => {
                let temp = [];
                if (this.data.query.pageIndex == 1) {
                    temp = res.data.rows;
                } else {
                    temp = [...this.data.list, ...res.data.rows];
                }
                this.setData({
                    list: temp,
                    totalPage: res.data.totalPage,
                });
            })
            .finally(() => {
                if (this.data.refresherTriggered) {
                    this.setData({
                        scrollTop: 0,
                        isLoading: false,
                        refresherTriggered: false,
                    });
                }
                if (this.data.isLoadingMore) {
                    this.setData({
                        isLoadingMore: false,
                    });
                }
            });
    },
    // 上拉加载更多事件处理
    loadMoreData() {
        if (this.data.query.pageIndex >= this.data.totalPage) return;
        this.setData({
            isLoadingMore: true,
            query: {
                ...this.data.query,
                pageIndex: this.data.query.pageIndex + 1,
            },
        });
        this.loadData();
    },
    showTipFn() {
        this.setData({
            showTip: true,
        });
    },
    showTipFn2() {
        this.setData({
            showTip2: true,
        });
    },
    closePaster() {
        this.setData({
            showPoster: false,
        });
    },

    setPoster() {
        if (this.data.posters.length) {
            const randomIndex = Math.floor(
                Math.random() * this.data.posters.length
            );
            this.setData({
                posterUrl: this.data.posters[randomIndex],
            });
        }
    },

    // 生成海报
    createPoster() {
        this.setPoster();
        let temp = wx.getStorageSync("userInfo");
        let params = {
            page: "pages/index/index",
            checkPath: false,
            scene: `inviterId=${temp.id}`,
            // envVersion: "develop",
        };
        console.log(params);
        wechatsceneToCode(params).then((res) => {
            console.log(res);
            // return
            this.setData({
                miniCode: res.data.buffer,
                showPoster: true,
            });
        });
    },

    savePoster() {
        let self = this;
        wx.showLoading({
            title: "正在生成图片...",
        });

        // 获取系统信息，用于计算画布尺寸
        const systemInfo = wx.getSystemInfoSync();
        const pixelRatio = systemInfo.pixelRatio || 2;

        // 画布尺寸
        const canvasWidth = 375;
        const canvasHeight = 560;

        // 创建canvas上下文
        const ctx = wx.createCanvasContext("posterCanvas", this);

        // 设置背景色
        ctx.setFillStyle("#ffffff");
        ctx.fillRect(0, 0, canvasWidth, canvasHeight);

        // 绘制海报背景图
        if (self.data.posterUrl) {
            this.drawImageOnCanvas(
                ctx,
                self.data.posterUrl,
                0,
                0,
                canvasWidth,
                canvasHeight * 0.7
            )
                .then(() => {
                    // 绘制底部信息区域
                    self.drawBottomInfo(ctx, canvasWidth, canvasHeight);

                    // 绘制小程序码
                    if (self.data.miniCode) {
                        return self.drawImageOnCanvas(
                            ctx,
                            self.data.miniCode,
                            canvasWidth - 120,
                            canvasHeight * 0.7 + 20,
                            100,
                            100
                        );
                    }
                })
                .then(() => {
                    // 执行绘制
                    ctx.draw(false, () => {
                        // 延迟一下确保绘制完成
                        setTimeout(() => {
                            self.canvasToImage(canvasWidth, canvasHeight);
                        }, 500);
                    });
                })
                .catch((err) => {
                    wx.hideLoading();
                    wx.showToast({
                        title: "生成图片失败",
                        icon: "none",
                    });
                    console.error("绘制图片失败:", err);
                });
        } else {
            wx.hideLoading();
            wx.showToast({
                title: "海报图片未加载",
                icon: "none",
            });
        }
    },
    // 绘制图片到canvas的工具方法
    drawImageOnCanvas(ctx, imageSrc, x, y, width, height) {
        return new Promise((resolve, reject) => {
            // 增强base64数据检查
            if (typeof imageSrc === "string") {
                if (
                    imageSrc.startsWith("data:image") ||
                    imageSrc.startsWith("/9j/") ||
                    imageSrc.startsWith("iVBORw0KGgo")
                ) {
                    let base64Data = imageSrc;
                    if (imageSrc.startsWith("/9j/")) {
                        base64Data = "data:image/jpeg;base64," + imageSrc;
                    } else if (imageSrc.startsWith("iVBORw0KGgo")) {
                        base64Data = "data:image/png;base64," + imageSrc;
                    } else if (!imageSrc.startsWith("data:image")) {
                        base64Data = "data:image/png;base64," + imageSrc;
                    }

                    this.convertBase64ToTempFile(base64Data, width, height)
                        .then((tempFilePath) => {
                            this.drawImageWithAspectFit(
                                ctx,
                                tempFilePath,
                                x,
                                y,
                                width,
                                height
                            )
                                .then(resolve)
                                .catch(reject);
                        })
                        .catch(reject);
                    return;
                } else if (imageSrc.startsWith("http")) {
                    wx.downloadFile({
                        url: imageSrc,
                        success: (res) => {
                            if (res.statusCode === 200) {
                                this.drawImageWithAspectFit(
                                    ctx,
                                    res.tempFilePath,
                                    x,
                                    y,
                                    width,
                                    height
                                )
                                    .then(resolve)
                                    .catch(reject);
                            } else {
                                reject(new Error("下载图片失败"));
                            }
                        },
                        fail: reject,
                    });
                    return;
                }
            }

            try {
                this.drawImageWithAspectFit(ctx, imageSrc, x, y, width, height)
                    .then(resolve)
                    .catch(reject);
            } catch (e) {
                reject(e);
            }
        });
    },

    // 绘制图片，确保保持宽高比（aspectFit效果）
    drawImageWithAspectFit(ctx, imageSrc, x, y, width, height) {
        return new Promise((resolve, reject) => {
            wx.getImageInfo({
                src: imageSrc,
                success: (res) => {
                    const imgWidth = res.width;
                    const imgHeight = res.height;

                    const aspectRatio = imgWidth / imgHeight;
                    const targetAspectRatio = width / height;

                    let drawWidth, drawHeight;

                    // 根据宽高比决定如何缩放
                    if (aspectRatio > targetAspectRatio) {
                        drawWidth = width;
                        drawHeight = width / aspectRatio;
                    } else {
                        drawHeight = height;
                        drawWidth = height * aspectRatio;
                    }

                    // 计算绘制位置，居中显示
                    const drawX = x + (width - drawWidth) / 2;
                    // const drawY = y + (height - drawHeight) / 2;
                    const drawY = y;

                    // 绘制图片
                    ctx.drawImage(
                        imageSrc,
                        drawX,
                        drawY,
                        drawWidth,
                        drawHeight
                    );

                    resolve();
                },
                fail: (err) => {
                    reject(err);
                },
            });
        });
    },
    // 绘制底部信息和二维码
    drawBottomInfo(ctx, canvasWidth, canvasHeight) {
        const bottomY = canvasHeight * 0.7;
        const bottomHeight = canvasHeight * 0.3;
        // 绘制底部白色背景
        ctx.setFillStyle("#ffffff");
        ctx.fillRect(0, bottomY, canvasWidth, bottomHeight);
        // 绘制标题
        ctx.setFillStyle("#333333");
        ctx.setFontSize(18);
        ctx.setTextAlign("left");
        ctx.fillText("颜见小程序", 20, bottomY + 30);
        // 绘制描述文字
        ctx.setFillStyle("#666666");
        ctx.setFontSize(14);
        ctx.fillText("实名认证 真人真颜", 20, bottomY + 60);
        ctx.fillText("线下陪伴 兴趣社交", 20, bottomY + 90);
        ctx.fillText("长按扫码 立即体验", 20, bottomY + 120);
    },

    // 将base64转换为临时文件
    convertBase64ToTempFile(base64Data, width, height) {
        return new Promise((resolve, reject) => {
            try {
                const base64Str = base64Data.split(",")[1] || base64Data;

                const estimatedSize = (base64Str.length * 3) / 4;
                console.log("估算的base64数据大小（字节）:", estimatedSize);

                if (estimatedSize > 1024 * 1024) {
                    console.warn("base64数据较大，可能需要压缩");
                }

                const fs = wx.getFileSystemManager();
                const fileName = `temp_image_${Date.now()}_${Math.floor(
                    Math.random() * 1000
                )}.png`;
                const filePath = `${wx.env.USER_DATA_PATH}/${fileName}`;

                fs.writeFile({
                    filePath: filePath,
                    data: base64Str,
                    encoding: "base64",
                    success: () => {
                        console.log("临时文件创建成功:", filePath);
                        resolve(filePath);
                    },
                    fail: (err) => {
                        console.error("写入临时文件失败:", err);
                        reject(err);
                    },
                });
            } catch (e) {
                console.error("convertBase64ToTempFile异常:", e);
                reject(e);
            }
        });
    },

    // 删除临时文件
    deleteTempFile(filePath) {
        try {
            const fs = wx.getFileSystemManager();
            fs.unlink({
                filePath: filePath,
                success: () => {
                    console.log("临时文件删除成功:", filePath);
                },
                fail: (err) => {
                    console.error("删除临时文件失败:", err);
                },
            });
        } catch (e) {
            console.error("deleteTempFile异常:", e);
        }
    },

    // 将canvas转换为图片并保存
    canvasToImage(canvasWidth, canvasHeight) {
        wx.canvasToTempFilePath(
            {
                canvasId: "posterCanvas",
                width: canvasWidth,
                height: canvasHeight,
                destWidth: canvasWidth * 2,
                destHeight: canvasHeight * 2,
                success: (res) => {
                    wx.hideLoading();
                    this.saveImageToAlbum(res.tempFilePath);
                },
                fail: (err) => {
                    wx.hideLoading();
                    wx.showToast({
                        title: "生成图片失败",
                        icon: "none",
                    });
                    console.error("canvas转图片失败:", err);
                },
            },
            this
        );
    },

    // 保存图片到相册
    saveImageToAlbum(tempFilePath) {
        wx.saveImageToPhotosAlbum({
            filePath: tempFilePath,
            success: () => {
                wx.showToast({
                    title: "保存成功",
                    icon: "success",
                });
                // 关闭海报弹窗
                this.setData({
                    showPoster: false,
                });
            },
            fail: (err) => {
                if (err.errMsg.includes("auth")) {
                    // 用户拒绝授权，引导用户手动授权
                    wx.showModal({
                        title: "提示",
                        content: "需要您授权保存图片到相册",
                        showCancel: false,
                        confirmText: "去设置",
                        success: () => {
                            wx.openSetting();
                        },
                    });
                } else {
                    wx.showToast({
                        title: "保存失败",
                        icon: "none",
                    });
                }
                console.error("保存图片失败:", err);
            },
        });
    },

    // 获取页面内容高度
    computeAvailableHeight() {
        const systemInfo = wx.getWindowInfo();
        const statusBarHeight = systemInfo.statusBarHeight; // 状态栏高度
        const menuButtonInfo = wx.getMenuButtonBoundingClientRect(); // 胶囊按钮信息
        // 计算导航栏高度
        const navBarHeight =
            (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
        // 计算可用高度
        const availableHeight =
            systemInfo.windowHeight - statusBarHeight - navBarHeight;
        // btn的高度
        let btnHeight = 0;
        const query = wx.createSelectorQuery();
        query
            .select(".btn")
            .boundingClientRect((res) => {
                btnHeight = res.height;
                this.setData({
                    scrollHeight: availableHeight - btnHeight - 40,
                });
            })
            .exec();
    },
});
