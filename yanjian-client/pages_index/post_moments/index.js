// pages_index/post_moments/index.js
import uploadFilePromise from "../../utils/upload.js";
import { subjectList } from "../../pages/api/index";
import { addArticle } from "../api/index";
import { showFn } from "../../utils/myHttpMessage";
Page({
    data: {
        value: "",
        fileList: [],
        autoSize: {
            maxHeight: "380rpx",
            minHeight: "380rpx",
        },
        h: "env(safe-area-inset-bottom)",
        focus: false,
        isUpload: true,
        showLabelList: false,
        labelList: [],
        totalPage: 0,
        params: {
            pageIndex: 1,
            pageSize: 20,
        },
        selectLabelList: [],
        uploadActions: [
            {
                name: "图片",
            },
            {
                name: "视频",
            },
        ],
        showSelectUpload: false,
        imgOrVideo: "",
    },
    onLoad(options) {
        let subject = wx.getStorageSync("subject");
        if (subject) {
            this.setData({
                selectLabelList: [subject],
            });
            setTimeout(() => {
                wx.removeStorageSync("subject");
            }, 200);
        }
        this.getLabelList();
    },
    onShow() {},
    iptChange(event) {
        this.setData({
            value: event.detail,
        });
    },
    onClickLeft() {
        wx.navigateBack();
    },
    focusFn(e) {
        this.setData({
            h: e.detail.height + 10 + "px",
            focus: true,
        });
    },
    blurFn(e) {
        this.setData({
            h: "env(safe-area-inset-bottom)",
            focus: false,
        });
    },
    addUpload() {
        if (!this.data.imgOrVideo) {
            this.setData({
                showSelectUpload: true,
            });
        } else if (this.data.imgOrVideo == "image") {
            this.chooseFile("image");
        } else {
            this.chooseFile("video");
        }
    },
    onCloseUpload() {
        this.setData({
            showSelectUpload: false,
        });
    },
    onSelectUpload(event) {
        if (event.detail.name == "图片") {
            this.chooseFile("image");
        } else {
            this.chooseFile("video");
        }
    },
    chooseFile(type) {
        let self = this;
        let num = 0;
        if (type == "image") {
            num = 9 - this.data.fileList.length;
        } else {
            num = 1;
        }
        wx.chooseMedia({
            count: num,
            mediaType: [type],
            maxDuration: 60,
            success: (res) => {
                if (res.type == "video") {
                    wx.openVideoEditor({
                        filePath: res.tempFiles[0].tempFilePath,
                        maxDuration: 60,
                        success: (res) => {
                            let temp = [
                                {
                                    fileType: "video",
                                    size: res.size,
                                    tempFilePath: res.tempFilePath,
                                },
                            ];
                            self.afterRead(temp);
                        },
                        fail: (err) => {
                            console.log(err);
                        },
                    });
                } else {
                    self.afterRead(res.tempFiles);
                }
            },
        });
    },
    preview(e) {
        const index = e.currentTarget.dataset.index;
        let urls = this.data.fileList.map((item) => {
            return {
                url: item.url,
                type: this.data.imgOrVideo,
            };
        });
        wx.previewMedia({
            sources: urls,
            current: index,
            showmenu: false,
            success: () => {},
            fail: (err) => {
                console.log(err);
            },
        });
    },
    submit() {
        if (!this.data.value && !this.data.fileList.length) {
            return;
        }
        if (!this.data.isUpload) {
            wx.showToast({
                title: "请等待上传完成之后再发布！",
                icon: "none",
            });
            return;
        }
        wx.login({
            success: (res) => {
                let data = {
                    code: res.code,
                    type: this.data.imgOrVideo,
                    content: this.data.value,
                    img: this.data.fileList.map((item) => item.url),
                    subjectIdList: this.data.selectLabelList.map(
                        (item) => item.id
                    ),
                };
                addArticle(data)
                    .then((res) => {
                        this.setData({
                            value: "",
                            fileList: [],
                        });
                        wx.showToast({
                            title: "发布成功",
                            icon: "none",
                        });
                        setTimeout(() => {
                            const pages = getCurrentPages();
                            const prePage = pages[pages.length - 2];
                            if (prePage && prePage.onRefresh) {
                                prePage.onRefresh();
                            }
                            wx.navigateBack();
                        }, 1500);
                    })
                    .catch((err) => {
                        if (err.code == "12034") {
                            showFn({
                                message: err.msg,
                            });
                        }
                    });
            },
        });
    },
    async afterRead(files) {
        this.setData({
            isUpload: false,
        });
        let arr = [...this.data.fileList];
        wx.showLoading({
            title: "上传中...",
            mask: true,
        });
        try {
            // 使用 Promise.all 等待所有异步操作完成
            const uploadPromises = files.map(async (item) => {
                const result = await uploadFilePromise(item);
                arr.push({
                    url: result,
                });
            });
            // 等待所有上传任务完成
            await Promise.all(uploadPromises);
            // 更新数据
            this.setData({
                fileList: arr,
                imgOrVideo: files[0].fileType,
            });
            this.setData(
                {
                    isUpload: true,
                },
                2000
            );
        } catch (error) {
            console.log(error, "error");
            wx.showToast({
                title: "上传文件发生错误",
                icon: "none",
            });
        } finally {
            console.log("finally");
            wx.hideLoading();
        }
    },

    delete(event) {
        const index = event.currentTarget.dataset.index;
        const newFileList = [...this.data.fileList]; // 创建 fileList 的副本
        newFileList.splice(index, 1); // 在副本上进行删除操作
        this.setData({
            fileList: newFileList,
        });
        if (this.data.fileList.length == 0) {
            this.setData({
                imgOrVideo: "",
            });
        }
    },
    addLabel() {
        this.setData({
            showLabelList: true,
        });
    },
    closeLabel() {
        this.setData({
            showLabelList: false,
        });
    },
    bindscrolltolower(e) {
        if (this.data.params.pageIndex >= this.data.totalPage) return;
        this.setData({
            params: {
                ...this.data.params,
                pageIndex: (this.data.params.pageIndex += 1),
            },
        });
        this.getLabelList();
    },
    getLabelList() {
        subjectList(this.data.params).then((res) => {
            let temp = [];
            if (this.data.params.pageIndex == 1) {
                temp = res.data.rows;
            } else {
                temp = [...this.data.labelList, ...res.data.rows];
            }
            this.setData({
                labelList: temp,
                totalPage: res.data.totalPage,
            });
        });
    },
    selectLabel(e) {
        let list = [...this.data.selectLabelList];
        let temp = e.currentTarget.dataset.info;
        let idx = list.findIndex((item) => item.id == temp.id);
        if (idx != -1) {
            list.splice(idx, 1);
            list.push(temp);
        } else {
            list.push(temp);
        }
        this.setData({
            selectLabelList: list,
            showLabelList: false,
        });
    },
    delLabel(e) {
        let idx = e.currentTarget.dataset.index;
        let temp = [...this.data.selectLabelList];
        temp.splice(idx, 1);
        this.setData({
            selectLabelList: temp,
        });
    },
});
