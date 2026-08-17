import { userList } from "../../pages/api/index";
import { env } from "../../env";
Component({
    properties: {
        scrollHeight: {
            type: Number,
            value: 0,
        },
        sortType: {
            type: String,
            value: "",
        },
        params: {
            type: Object,
            value: {},
            observer: function (val) {
                if (val.sex) {
                    this.setQuery(val);
                }
            },
        },
    },
    data: {
        imgBaseURL: "",
        query: {
            pageIndex: 1,
            pageSize: 10,
        },
        list: [],
        totalPage: 0,
        isLoading: false,
        isLoadingMore: false,
        isPull: false,
        scrollTop: 0,
        refresherTriggered: false,
    },
    lifetimes: {
        attached() {
            this.setData({
                imgBaseURL: env.imgBaseURL,
            });
            // this.loadData()
        },
    },
    methods: {
        setQuery(val) {
            let city = val.city == "全国" ? "" : val.city;
            let tempVal = { ...val };
            if (tempVal.minAge == "不限") {
                delete tempVal.minAge;
            }
            if (tempVal.maxAge == "不限") {
                delete tempVal.maxAge;
            }
            // if (tempVal.sex == "不限") {
            //     delete tempVal.sex;
            // }
            this.setData({
                query: {
                    pageIndex: 1,
                    pageSize: 10,
                    ...tempVal,
                    city,
                    sortType: this.data.sortType,
                },
            });
            this.loadData();
        },
        itemTap(e) {
            this.triggerEvent("itemTap", e.currentTarget.dataset.item);
        },
        showSimilarityTip() {
            this.triggerEvent("showSimilarityTip");
        },
        loadData() {
            userList(this.data.query)
                .then((res) => {
                    let temp = [];
                    if (this.data.query.pageIndex == 1) {
                        temp = res.data.rows;
                    } else {
                        temp = [...this.data.list, ...res.data.rows];
                    }
                    this.setData({
                        list: temp,
                        totalPage: res.data.totalPage,
                    });
                })
                .catch((err) => {
                    if (this.data.query.pageIndex > 1) {
                        let pi = this.data.query.pageIndex - 1;
                        this.setData({
                            query: {
                                ...this.data.query,
                                pageIndex: pi,
                            },
                        });
                    }
                    if (err.code == "12029") {
                        this.triggerEvent("openVip");
                    }
                })
                .finally(() => {
                    if (this.data.refresherTriggered) {
                        this.setData({
                            scrollTop: 0,
                            isLoading: false,
                            refresherTriggered: false,
                        });
                    }
                    if (this.data.isLoadingMore) {
                        this.setData({
                            isLoadingMore: false,
                        });
                    }
                });
        },
        // 下拉刷新事件处理
        onRefresh() {
            this.setData({
                isLoading: true,
                isPull: false,
                refresherTriggered: true,
                query: {
                    ...this.data.query,
                    pageIndex: 1,
                },
            });
            this.loadData();
        },

        // 上拉加载更多事件处理
        loadMoreData() {
            if (this.data.query.pageIndex >= this.data.totalPage) return;
            let temp = this.data.query;
            temp.pageIndex += 1;
            this.setData({
                isLoadingMore: true,
                query: temp,
            });
            this.loadData();
        },
        bindrefresherpulling() {
            console.log("自定义下拉刷新控件被下拉");
            this.setData({
                isPull: true,
            });
        },
        bindrefresherrefresh() {
            console.log("自定义下拉刷新被触发");
            this.onRefresh();
        },
        bindrefresherrestore() {
            console.log("自定义下拉刷新被复位");
            this.setData({
                isPull: false, // 隐藏下拉图标
            });
        },
        bindrefresherabort() {
            console.log("自定义下拉刷新被中止");
            this.setData({
                isLoading: false,
                isPull: false, // 隐藏下拉图标
            });
        },
        closeVipDialog() {
            this.setData({
                showVipDialog: false,
            });
        },
    },
});
