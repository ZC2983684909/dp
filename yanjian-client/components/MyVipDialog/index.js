// components/MyMessage/index.js
import { env } from "../../env";
import { priceList } from "../../pages/api/common";
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        title: {
            type: String,
            value: "申请已提交",
        },
        current: {
            type: Number,
            value: 0,
        },
        showClose: {
            type: Boolean,
            value: true,
        },
    },

    /**
     * 组件的初始数据
     */
    data: {
        imgBaseURL: "",
        vipPrice: [{}, {}, {}],
    },
    lifetimes: {
        attached() {
            this.setData({
                imgBaseURL: env.imgBaseURL,
            });
            this.getPriceList();
        },
    },
    /**
     * 组件的方法列表
     */
    methods: {
        onClose() {
            this.triggerEvent("onClose");
        },
        getPriceList() {
            priceList().then((res) => {
                this.setData({
                    vipPrice: res.data.vipPriceList,
                });
            });
        },
        goVipOpen() {
            wx.navigateTo({
                url: "/pages_my/open_vip/index",
            });
        },
    },
});
