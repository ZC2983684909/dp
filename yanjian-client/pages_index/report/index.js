// pages_register/register/register2/index.js
import uploadFilePromise from "../../utils/upload.js";
import { complaint } from "../api/index";
import { showFn } from "../../utils/myHttpMessage";
Page({
    /**
     * 页面的初始数据
     */
    data: {
        form: {
            complaintId: "",
            content: "",
            img: [],
            type: 0,
        },
        imgs: [],
        placeholder: "请输入举报理由，例如：信息造假、微信号联系不上等",
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        let placeholder =
            options.type == 1
                ? "请输入举报理由，例如：信息造假、微信号联系不上等"
                : "请输入举报理由";
        this.setData({
            form: {
                ...this.data.form,
                complaintId: options.id,
                type: options.type,
            },
            placeholder: placeholder,
        });
    },
    onShow() {},
    back() {
        wx.navigateBack();
    },
    onChange(value) {
        this.setData({
            form: {
                ...this.data.form,
                content: value.detail,
            },
        });
    },
    submit() {
        if (this.validate()) {
            let parmas = { ...this.data.form };
            parmas.img = this.data.imgs.map((item) => item.url);
            complaint(parmas).then((res) => {
                showFn(
                    {
                        message: "提交成功",
                    },
                    () => {
                        wx.navigateBack();
                    },
                    () => {
                        wx.navigateBack();
                    }
                );
            });
        }
    },
    validate() {
        let msg = "";
        let form = this.data.form;
        if (!form.content || !form.content.length) {
            msg = "请输入举报理由";
        } else if (!this.data.imgs.length) {
            msg = "请上传举报照片";
        }
        if (msg) {
            // showFn({
            //   message: msg
            // })
            wx.showToast({
                title: msg,
                icon: "none",
            });
            return false;
        } else {
            return true;
        }
    },
    async afterRead(event) {
        const { file } = event.detail; // 获取文件列表
        let arr = [...this.data.imgs]; // 复制当前图片数组
        wx.showLoading({
            title: "上传中...",
            mask: true,
        });
        // 遍历每个文件并上传
        try {
            for (const item of file) {
                try {
                    // 调用上传方法
                    const result = await uploadFilePromise(item);
                    // 将上传结果添加到数组中
                    arr.push({
                        url: result,
                    });
                    // 更新数据到页面
                    this.setData({
                        imgs: arr,
                    });
                } catch (error) {
                    // 捕获错误并提示用户
                    console.error("文件上传失败:", error); // 打印错误日志
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
    delete(event) {
        const index = event.detail.index;
        const newFileList = [...this.data.imgs]; // 创建 fileList 的副本
        newFileList.splice(index, 1); // 在副本上进行删除操作
        this.setData({
            imgs: newFileList,
        });
    },
});
