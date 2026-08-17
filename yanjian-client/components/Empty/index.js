// components/Empty/index.js
import { env } from "../../env";
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        showLoginBtn: {
            type: Boolean,
            value: false,
        },
        msg: {
            type: String,
            value: "暂无更多内容",
        },
        height: {
            type: String,
            value: "60vh",
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
        login() {
            wx.navigateTo({
                url: "/pages/login/index",
            });
        },
    },
});
