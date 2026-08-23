import socketManager from "../../utils/socket";
import { getImInfo } from "../../pages/api/index";

Page({
    data: {
        userId: "",
        nickName: "",
        ownerId: "",
        value: "",
        list: [],
        scrollIntoView: "",
        connected: false,
        query: {
            pageIndex: 1,
            pageSize: 20,
        },
        totalPage: 0,
        loadingHistory: false,
    },

    onLoad(options) {
        const owner = wx.getStorageSync("userInfo") || {};
        this.setData({
            userId: options.userId || "",
            nickName: options.nickName || "",
            ownerId: owner.id || "",
            connected: socketManager.getConnectStatus(),
        });

        if (!socketManager.getConnectStatus() && owner.id) {
            getApp().initSocket();
        }

        this.handleSocketMessage = (message) => {
            let data = message && (message.info || message);
            try {
                data = typeof data === "string" ? JSON.parse(data) : data;
            } catch (error) {
                return;
            }
            const targetId = String(this.data.userId || "");
            const sendUserId = String((data && data.sendUserId) || "");
            const acceptUserId = String((data && data.acceptUserId) || "");
            if (!data || (sendUserId !== targetId && acceptUserId !== targetId)) {
                return;
            }
            this.appendMessage(data, sendUserId === String(this.data.ownerId));
        };
        this.handleSocketConnect = () => this.setData({ connected: true });
        this.handleSocketClose = () => this.setData({ connected: false });
        socketManager.setCallback("onMessage", this.handleSocketMessage);
        socketManager.setCallback("onConnect", this.handleSocketConnect);
        socketManager.setCallback("onClose", this.handleSocketClose);
        this.loadHistory(true);
    },

    onUnload() {
        socketManager.setCallback("onMessage", null);
        socketManager.setCallback("onConnect", null);
        socketManager.setCallback("onClose", null);
    },

    onClickLeft() {
        wx.navigateBack();
    },

    onChange(event) {
        this.setData({ value: event.detail });
    },

    appendMessage(message, mine) {
        const id = message.msgNo || message.tempMsgNo || `${Date.now()}-${Math.random()}`;
        if (this.data.list.some((item) => item.id === id)) {
            return;
        }
        this.setData({
            list: [...this.data.list, {
                id,
                content: message.content || "",
                mine,
                status: message.status || "sent",
            }],
            scrollIntoView: `msg-${id}`,
        });
    },

    loadHistory(initial = false) {
        if (!this.data.userId || this.data.loadingHistory) {
            return Promise.resolve();
        }
        const pageIndex = initial ? 1 : this.data.query.pageIndex;
        this.setData({ loadingHistory: true });
        return getImInfo(this.data.userId, {
            ...this.data.query,
            pageIndex,
        })
            .then((res) => {
                const rows = ((res.data && res.data.rows) || [])
                    .map((item) => ({
                        ...item,
                        id: item.msgNo,
                        mine: Boolean(item.self) || String(item.sendUserId) === String(this.data.ownerId),
                        status: "sent",
                    }))
                    .reverse();
                const existing = initial ? [] : this.data.list;
                const ids = new Set(existing.map((item) => item.id));
                const history = rows.filter((item) => item.id && !ids.has(item.id));
                this.setData({
                    list: [...history, ...existing],
                    totalPage: Number((res.data && res.data.totalPage) || 0),
                    query: {
                        ...this.data.query,
                        pageIndex,
                    },
                    scrollIntoView: initial && rows.length ? `msg-${rows[rows.length - 1].id}` : "",
                });
            })
            .finally(() => this.setData({ loadingHistory: false }));
    },

    loadOlderMessages() {
        if (this.data.loadingHistory || this.data.query.pageIndex >= this.data.totalPage) {
            return;
        }
        this.setData({
            query: {
                ...this.data.query,
                pageIndex: this.data.query.pageIndex + 1,
            },
        });
        this.loadHistory();
    },

    updateMessageStatus(id, status) {
        this.setData({
            list: this.data.list.map((item) => item.id === id ? { ...item, status } : item),
        });
    },

    send() {
        const content = (this.data.value || "").trim();
        if (!content || !this.data.userId) {
            return;
        }
        if (!socketManager.getConnectStatus()) {
            wx.showToast({ title: "聊天连接尚未建立", icon: "none" });
            return;
        }
        const tempMsgNo = `local-${Date.now()}`;
        this.appendMessage({ tempMsgNo, content, status: "sending" }, true);
        this.setData({ value: "" });
        socketManager.sendChatMessage(
            this.data.userId,
            content,
            "text",
            tempMsgNo,
            () => this.updateMessageStatus(tempMsgNo, "sent"),
            () => {
                this.updateMessageStatus(tempMsgNo, "failed");
                wx.showToast({ title: "消息发送失败", icon: "none" });
            }
        ).catch(() => {});
    },
});
