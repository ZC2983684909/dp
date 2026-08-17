import { userArticle } from "../api/index";

Page({
    data: {
        userId: "",
        list: [],
        isLoading: true,
    },

    onLoad(options) {
        this.setData({ userId: options.userId || "" });
        this.loadData();
    },

    loadData() {
        if (!this.data.userId) {
            this.setData({ isLoading: false });
            return;
        }
        userArticle({
            userId: this.data.userId,
            pageIndex: 1,
            pageSize: 20,
        }).then((res) => {
            this.setData({
                list: res.data && Array.isArray(res.data.rows)
                    ? res.data.rows
                    : [],
            });
        }).finally(() => {
            this.setData({ isLoading: false });
        });
    },

    onClickLeft() {
        wx.navigateBack();
    },
});
