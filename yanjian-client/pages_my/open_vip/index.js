// pages_my/open_vip/index.js
import { env } from "../../env";
import { basePay, priceList } from "../../pages/api/common";
import { phoneSys } from "../../utils/utils";
Page({
    data: {
        imgBaseURL: "",
        vipInfo: {},
        act: 0,
        showMsg: false,
    },
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        this.getPriceList();
    },
    onShow() {},
    getPriceList() {
        priceList().then((res) => {
            this.setData({
                vipInfo: res.data,
            });
        });
    },
    onChange(e) {
        this.setData({
            act: e.currentTarget.dataset.index,
        });
    },
    back() {
        wx.navigateBack();
    },
    submit() {
        if (phoneSys() == "iOS") {
            this.setData({
                showMsg: true,
            });
            return;
        }
        this.confirm();
    },
    onClose() {
        this.setData({
            showMsg: false,
        });
    },
    confirm() {
        let self = this;
        let temp = this.data.vipInfo.vipPriceList[this.data.act];
        let params = {
            platform: "WX_APPLET",
            beanName: "IPayServiceImpl",
            amount: temp.price,
        };
        basePay(params).then((res2) => {
            const { timeStamp, nonceStr, packageVal, signType, paySign } =
                res2.data.chnlFrontParamInfo;
            wx.requestPayment({
                timeStamp,
                nonceStr,
                package: packageVal,
                signType,
                paySign,
                success(res) {
                    self.getPriceList();
                    wx.showToast({
                        title: "支付成功",
                        icon: "none",
                    });
                },
                fail(err) {
                    console.log(err);
                },
            });
        });
    },
});
