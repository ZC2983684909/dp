import { env } from "../../../env";
import { readStar, readApply, readwaitApply } from "../../../pages/api/index";
import { getLocationByStorage } from "../../../utils/utils";
Component({
    properties: {
        scrollHeight: {
            type: Number,
            value: 0,
        },
        api: {
            type: Function,
            observer: function (val) {
                this.setApi(val);
            },
        },
        type: {
            type: String,
            value: "",
        },
    },
    data: {
        imgBaseURL: "",
        query: {
            pageIndex: 1,
            pageSize: 15,
        },
        list: [],
        totalPage: 0,
        isLoading: false,
        isLoadingMore: false,
        isPull: false,
        scrollTop: 0,
        refresherTriggered: false,
        queryApi: null,
    },
    lifetimes: {
        attached() {
            this.setData({
                imgBaseURL: env.imgBaseURL,
            });
        },
    },
    methods: {
        readItem(info) {
            let type = this.data.type;
            let API = null;
            if (type == "申请我的") {
                API = readApply;
            }
            if (info.readStatus == 0 && API !== null) {
                API(info.auditId);
            }
        },
        setApi(val) {
            this.setData({
                queryApi: val,
            });
            this.setUserLocation();
            this.loadData();
        },
        setUserLocation() {
            this.setData({
                query: {
                    ...this.data.query,
                },
            });
        },
        itemTap(e) {
            this.readItem(e.currentTarget.dataset.item);
            let id = e.currentTarget.dataset.item.id;
            wx.navigateTo({
                url: "/pages_index/friend_detail/index?id=" + id,
            });
        },
        seeHello(event) {
            let info = event.currentTarget.dataset.info;
            this.readItem(info);
            this.triggerEvent("seeHello", info);
        },
        reject(event) {
            let info = event.currentTarget.dataset.info;
            this.readItem(info);
            this.triggerEvent("reject", info);
        },
        reslove(event) {
            let info = event.currentTarget.dataset.info;
            this.readItem(info);
            this.triggerEvent("reslove", info);
        },
        loadData() {
            this.data
                .queryApi(this.data.query)
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
            this.setData({
                isLoadingMore: true,
                query: {
                    ...this.data.query,
                    pageIndex: this.data.query.pageIndex + 1,
                },
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
    },
});
