// app.js
import { isLoginFn, getUnread } from "./pages/api/common";
import { initEid } from "./mp_ecard_sdk/main";
import { parseQueryString } from "./utils/utils";
import socketManager from "./utils/socket";

App({
    globalData: {},

    contactWs() {
        isLoginFn().then(() => {
            this.initSocket();
        });
    },

    initSocket() {
        const self = this;
        const userInfo = wx.getStorageSync("userInfo");
        if (!userInfo || !userInfo.id) {
            return;
        }
        socketManager.init(userInfo.id, {
            onUpdateUnreadCount: () => {
                self.updateUnreadCount();
            },
        });
    },

    updateUnreadCount() {
        // 未登录时不查询受保护的未读消息接口。
        if (!wx.getStorageSync("token")) {
            wx.removeTabBarBadge({ index: 2 });
            return Promise.resolve();
        }

        return getUnread()
            .then((res) => {
                if (res.data > 0) {
                    wx.setTabBarBadge({
                        index: 2,
                        text: res.data.toString(),
                    });
                } else {
                    wx.removeTabBarBadge({ index: 2 });
                }
            })
            .catch((err) => {
                // token 已失效时清掉本地登录态，下一次进入走正常登录流程。
                if (err && (err.code === 10006 || err.code === 10031)) {
                    wx.removeStorageSync("token");
                    wx.removeStorageSync("refreshToken");
                    wx.removeTabBarBadge({ index: 2 });
                }
            });
    },

    onLaunch() {
        initEid();
        this.updateUnreadCount();
    },

    onShow(options) {
        const query = (options && options.query) || {};
        if (query.inviterId) {
            wx.setStorageSync("inviterId", query.inviterId);
        }
        if (query.scene) {
            const scene = decodeURIComponent(query.scene);
            parseQueryString(scene);
        }
        if (!socketManager.getConnectStatus() && wx.getStorageSync("token")) {
            this.contactWs();
        }
    },

    onHide(options) {
        if (options && options.reason == 0) {
            wx.removeStorageSync("searchData");
            socketManager.close();
        }
    },
});
