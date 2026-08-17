// pages/message/index.js
import { getImList, deleteIm } from "../api/index";
import { isLoginFn } from "../api/common";
import { env } from "../../env";
import socketManager from "../../utils/socket";
Page({
    data: {
        imgBaseURL: "",
        scrollHeight: 600,
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
        message: "",
        showMsg: false,
        showSeeHello: false,
        isLogin: true,
    },
    onLoad() {
        this.computeAvailableHeight();
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
    },
    onShow() {
        this.setData({
            query: {
                ...this.data.query,
                pageIndex: 1,
            },
        });
        this.loadData();
        this.getLogin();
        this.updateCallback();
        const app = getApp();
        app.updateUnreadCount();
    },
    getLogin() {
        isLoginFn()
            .then((res) => {
                console.log(res);
                this.setData({
                    isLogin: true,
                });
            })
            .catch((err) => {
                this.setData({
                    isLogin: false,
                });
            });
    },
    updateCallback() {
        socketManager.updateCallBack({
            onUpdateList: () => {
                this.setData({
                    query: {
                        ...this.data.query,
                        pageIndex: 1,
                    },
                });
                this.loadData();
            },
        });
    },
    delete(e) {
        const { item } = e.currentTarget.dataset;
        if (e.detail == "cell") {
            wx.navigateTo({
                url:
                    "/pages_message/IM/index?userId=" +
                    item.sendUserId +
                    "&nickName=" +
                    item.nickName,
            });
        } else {
            deleteIm(item.sendUserId).then((res) => {
                let temp = this.data.list;
                let idx = temp.findIndex(
                    (el) => el.sendUserId == item.sendUserId
                );
                if (idx !== -1) {
                    temp.splice(idx, 1);
                    this.setData({
                        list: temp,
                    });
                }
            });
        }
    },
    onClose() {
        this.setData({
            showMsg: false,
        });
    },

    loadData() {
        getImList(this.data.query)
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
    // 获取页面内容高度
    computeAvailableHeight() {
        const systemInfo = wx.getWindowInfo
            ? wx.getWindowInfo()
            : wx.getSystemInfoSync();
        const statusBarHeight = Number(systemInfo.statusBarHeight) || 0;
        const menuButtonInfo = wx.getMenuButtonBoundingClientRect
            ? wx.getMenuButtonBoundingClientRect()
            : { top: statusBarHeight + 4, height: 32 };
        // 计算导航栏高度
        const navBarHeight =
            (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
        // 计算可用高度
        const availableHeight = Math.max(
            1,
            Number(systemInfo.windowHeight) - statusBarHeight - navBarHeight
        );
        this.setData({
            scrollHeight: Math.max(1, availableHeight - 10),
        });
    },
});
