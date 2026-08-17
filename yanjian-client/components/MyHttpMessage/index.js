// components/MyMessage/index.js
import { env } from "../../env";
Component({
    /**
     * 组件的属性列表
     */
    properties: {},

    /**
     * 组件的初始数据
     */
    data: {
        show: false,
        title: "提示",
        message: "消息",
        btnStr: "确定",
        imgBaseURL: "",
        showBtn: true,
        showClose: true,
        showCancelBtn: false,
        cancelBtn: "暂不登录",
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
        onConfirmFn() {
            this.setData({
                show: false,
            });
            this.onConfirm();
        },
        onCloseFn() {
            this.setData({
                show: false,
            });
            this.onClose();
        },
        onClose() {},
        onConfirm() {},
    },
});
