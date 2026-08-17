import { getDynamicList } from "../../pages/api/index";
import {
    subjectCancelStar,
    subjectInfo,
    subjectStar,
} from "../api/index";

Page({
    data: {
        id: "",
        subject: null,
        list: [],
        isLoading: true,
        isStarLoading: false,
    },

    onLoad(options) {
        this.setData({ id: options.id || "" });
        this.loadData();
    },

    loadData() {
        if (!this.data.id) {
            this.setData({ isLoading: false });
            return;
        }

        this.setData({ isLoading: true });
        Promise.all([
            subjectInfo(this.data.id),
            getDynamicList({
                subjectId: this.data.id,
                pageIndex: 1,
                pageSize: 20,
            }),
        ]).then(([subjectRes, articleRes]) => {
            this.setData({
                subject: subjectRes.data || null,
                list: articleRes.data && Array.isArray(articleRes.data.rows)
                    ? articleRes.data.rows
                    : [],
            });
        }).finally(() => {
            this.setData({ isLoading: false });
        });
    },

    toggleStar() {
        if (!this.data.subject || this.data.isStarLoading) {
            return;
        }
        const isStar = !!this.data.subject.isStar;
        this.setData({ isStarLoading: true });
        const request = isStar
            ? subjectCancelStar({ id: this.data.id })
            : subjectStar({ id: this.data.id });
        request.then(() => {
            this.setData({
                "subject.isStar": !isStar,
            });
        }).finally(() => {
            this.setData({ isStarLoading: false });
        });
    },

    onClickLeft() {
        wx.navigateBack();
    },
});
