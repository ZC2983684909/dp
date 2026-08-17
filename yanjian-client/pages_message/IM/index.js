import socketManager from "../../utils/socket";

Page({
    data: {
        userId: "",
        nickName: "",
        ownerId: "",
        value: "",
        list: [],
        scrollIntoView: "",
        connected: false,
    },

    onLoad(options) {
        const owner = wx.getStorageSync("userInfo") || {};
        this.setData({
            userId: options.userId || "",
            nickName: options.nickName || "",
            ownerId: owner.id || "",
            connected: socketManager.getConnectStatus(),
        });

        this.handleSocketMessage = (message) => {
            let data = message && message.info;
            try {
                data = typeof data === "string" ? JSON.parse(data) : data;
            } catch (error) {
                return;
            }
            if (!data || (data.sendUserId !== this.data.userId && data.acceptUserId !== this.data.userId)) {
                return;
            }
            this.appendMessage(data, data.sendUserId === this.data.ownerId);
        };
        this.handleSocketConnect = () => this.setData({ connected: true });
        this.handleSocketClose = () => this.setData({ connected: false });
        socketManager.setCallback("onMessage", this.handleSocketMessage);
        socketManager.setCallback("onConnect", this.handleSocketConnect);
        socketManager.setCallback("onClose", this.handleSocketClose);
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
            }],
            scrollIntoView: `msg-${id}`,
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
        this.appendMessage({ tempMsgNo, content }, true);
        this.setData({ value: "" });
        socketManager.sendChatMessage(
            this.data.userId,
            content,
            "text",
            tempMsgNo,
            null,
            () => wx.showToast({ title: "消息发送失败", icon: "none" })
        ).catch(() => {});
    },
});
