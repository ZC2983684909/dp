// components/MyMessage/index.js
import { env } from "../../env";
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        price: {
            type: Number,
            value: 0,
        },
        // 余额
        balance: {
            type: Number,
            value: 0,
        },
        // vip次数
        vipCount: {
            type: Number,
            value: 0,
        },
        btnStr1: {
            type: String,
            value: "VIP免费解锁",
        },
        btnStr2: {
            type: String,
            value: "单次解锁",
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
        close() {
            this.triggerEvent("close");
        },
        btn1() {
            this.triggerEvent("btn1");
        },
        btn2() {
            this.triggerEvent("btn2");
        },
    },
});
