// components/MyMessage/index.js
import { env } from "../../env";
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        title: {
            type: String,
            value: "添加微信",
        },
        message: {
            type: String,
            value: "添加微信好友时<br/>请备注“有趣的搭子”更容易通过哦",
        },
        weChat: {
            type: String,
            value: "",
        },
        btnStr: {
            type: String,
            value: "复制微信号",
        },
        showClose: {
            type: Boolean,
            value: true,
        },
        // 父容器高度
        height: {
            type: String,
            default: "504rpx",
        },
    },

    /**
     * 组件的初始数据
     */
    data: {
        imgBaseURL: "",
    },
    lifetimes: {
        attached() {
            this.setData({
                imgBaseURL: env.imgBaseURL,
            });
        },
    },
    /**
     * 组件的方法列表
     */
    methods: {
        onClose() {
            this.triggerEvent("onClose");
        },
        onConfirm(e) {
            // this.triggerEvent("onConfirm");
            wx.setClipboardData({
                data: e.currentTarget.dataset.wechat,
                success(res) {
                    console.log(res);
                },
            });
        },
    },
});
