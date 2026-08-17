import { getDynamicList } from "../../pages/api/index";

Page({
    data: {
        articleId: "",
        article: null,
        isLoading: true,
    },

    onLoad(options) {
        this.setData({
            articleId: options.articleId || options.id || "",
        });
        this.loadArticle();
    },

    loadArticle() {
        if (!this.data.articleId) {
            this.setData({ isLoading: false });
            return;
        }

        this.setData({ isLoading: true });
        getDynamicList({
            articleId: this.data.articleId,
            pageIndex: 1,
            pageSize: 1,
        }).then((res) => {
            const rows = res.data && Array.isArray(res.data.rows)
                ? res.data.rows
                : [];
            this.setData({
                article: rows[0] || null,
            });
        }).finally(() => {
            this.setData({ isLoading: false });
        });
    },

    onClickLeft() {
        wx.navigateBack();
    },

    onShareAppMessage() {
        return {
            title: this.data.article ? this.data.article.content : "动态详情",
            path: `/pages_index/dynamic_detail/index?articleId=${this.data.articleId}`,
        };
    },
});
