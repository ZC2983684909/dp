import { env } from "../env";

/**
 * WebSocket管理类
 * 支持消息收发、心跳检测、断线重连等功能
 */
class SocketManager {
    constructor() {
        this.socketTask = null;
        this.isConnected = false;
        this.isConnecting = false; // 新增：防止重复连接
        this.reConnect = true;
        this.timeoutObj = null;
        this.timeout = 30000;
        this.ownerId = null;
        this.listenersAdded = false; // 新增：监听器管理标记
        this.callbacks = {
            // 接收到新消息时候去更新未读数的回调
            onUpdateUnreadCount: null,
            // 接收到新消息时候去更新聊天列表的回调
            onUpdateList: null,
            onMessage: null,
            onConnect: null,
            onError: null,
            onClose: null,
        };

        // 新增：消息发送确认管理
        this.pendingMessages = new Map(); // 待确认消息队列：key=tempMsgNo, value={resolve, reject, retryCount, timer}
        this.confirmTimeout = 3000; // 3秒超时
        this.maxRetries = -1; // 无限重试
    }

    /**
     * 初始化连接
     * @param {string} ownerId 用户ID
     * @param {object} callbacks 回调函数集合
     */
    init(ownerId, callbacks = {}) {
        this.ownerId = ownerId;
        this.callbacks = { ...this.callbacks, ...callbacks };
        this.reConnect = true; // 重置重连标记

        // 先清理旧连接
        if (this.socketTask) {
            this.close();
        }

        // 添加监听器（只添加一次）
        this.addSocketListeners();

        // 建立连接
        this.connectSocket();
    }

    /**
     * 添加Socket监听器（只添加一次）
     */
    addSocketListeners() {
        if (this.listenersAdded) {
            return; // 防止重复添加监听器
        }
        this.listenersAdded = true;

        console.log("添加Socket全局监听器");

        // 监听连接打开
        wx.onSocketOpen((res) => {
            console.log("WebSocket连接已打开");
            this.isConnected = true;
            this.isConnecting = false;
            const msgData = {
                messageType: "CONNECT",
                info: this.ownerId,
            };
            console.log("发送连接请求");
            this.sendMessage(msgData);
        });

        // 监听消息
        wx.onSocketMessage((res) => {
            console.log("监听到socket信息：", res);
            if ("CONNECT_SUCCESS" == res.data) {
                // 建立连接成功后开始心跳
                this.reset();
                if (this.callbacks.onConnect) {
                    this.callbacks.onConnect();
                }
            } else {
                // 收到消息
                const msgData = JSON.parse(res.data);
                console.log("收到消息：", JSON.stringify(msgData));

                // 检查是否为服务端确认消息
                if (msgData.type === "sendConfirmation") {
                    this.handleMessageConfirm(msgData);
                    return; // 确认消息不需要回复
                }

                // 普通消息处理
                if (msgData.msgNo) {
                    // 消息应答
                    this.answerMessage(msgData.msgNo);
                }

                if (this.callbacks.onMessage) {
                    this.callbacks.onMessage(msgData);
                }
                if (this.callbacks.onUpdateUnreadCount) {
                    this.callbacks.onUpdateUnreadCount(msgData);
                }
                if (this.callbacks.onUpdateList) {
                    this.callbacks.onUpdateList(msgData);
                }
            }
        });

        // 监听连接关闭
        wx.onSocketClose((res) => {
            console.log("WebSocket连接已关闭");
            this.isConnected = false;
            this.isConnecting = false;

            // 清理所有待确认消息，调用失败回调
            this.clearPendingMessages("连接已断开");

            if (this.callbacks.onClose) {
                this.callbacks.onClose(res);
            }
            // 如果允许重连，则尝试重连
            if (this.reConnect) {
                setTimeout(() => {
                    this.connectSocket();
                }, 2000);
            }
        });

        // 监听连接错误
        wx.onSocketError((res) => {
            console.log("WebSocket连接发生错误");
            this.isConnected = false;
            this.isConnecting = false;
            if (this.callbacks.onError) {
                this.callbacks.onError(res);
            }
        });
    }

    /**
     * 建立WebSocket连接
     */
    connectSocket() {
        if (!this.reConnect || this.isConnecting) {
            console.log(
                "跳过连接：reConnect=",
                this.reConnect,
                ", isConnecting=",
                this.isConnecting
            );
            return;
        }

        this.isConnecting = true;
        console.log("连接socket");
        this.socketTask = wx.connectSocket({
            url: env.wsUrl,
            success: (data) => {
                console.log("连接socket请求发送成功");
            },
            fail: (data) => {
                console.log("连接socket失败");
                this.isConnecting = false;
                if (this.callbacks.onError) {
                    this.callbacks.onError(data);
                }
            },
        });
    }

    /**
     * 发送消息
     * @param {object} msgData 消息数据
     * @param {function} successCallback 成功回调
     * @param {function} failCallback 失败回调
     */
    sendMessage(msgData, successCallback, failCallback) {
        if (!this.isConnected) {
            console.log("WebSocket未连接，无法发送消息");
            if (failCallback) {
                failCallback({ error: "WebSocket未连接" });
            }
            return;
        }

        wx.sendSocketMessage({
            data: JSON.stringify(msgData),
            success: (res) => {
                if (msgData.messageType == "CONNECT") {
                    console.log(
                        "建立连接成功 启动监听" + JSON.stringify(msgData)
                    );
                } else {
                    console.log("消息发送成功" + JSON.stringify(msgData));
                }
                if (successCallback) {
                    successCallback(msgData);
                }
            },
            fail: (err) => {
                console.log("消息发送失败：" + JSON.stringify(err));
                // 检查连接状态，如果断开则标记为断开
                this.isConnected = false;

                if (failCallback) {
                    failCallback(err);
                }
            },
        });
    }

    /**
     * 发送聊天消息（等待服务端确认）
     * @param {string} acceptUserId 接收方用户ID
     * @param {string} content 消息内容
     * @param {string} messageFormat 消息格式 (text/image/emoji等)
     * @param {string} tempMsgNo 临时消息ID
     * @param {function} successCallback 成功回调
     * @param {function} failCallback 失败回调
     */
    sendChatMessage(
        acceptUserId,
        content,
        messageFormat,
        tempMsgNo,
        successCallback,
        failCallback
    ) {
        return new Promise((resolve, reject) => {
            const msgData = {
                acceptUserId: acceptUserId,
                sendUserId: this.ownerId,
                content: content,
                messageFormat: messageFormat,
                tempMsgNo: tempMsgNo, // 添加临时消息ID
            };
            const sendMessage = {
                messageType: "IM_MESSAGE",
                info: JSON.stringify(msgData),
            };

            // 发送消息到服务端
            this.sendMessage(
                sendMessage,
                (sentMsg) => {
                    console.log(`消息发送到服务端成功，等待确认: ${tempMsgNo}`);

                    // 添加到待确认消息队列
                    this.addPendingMessage(tempMsgNo, {
                        resolve: () => {
                            if (successCallback) successCallback(sentMsg);
                            resolve(sentMsg);
                        },
                        reject: (error) => {
                            if (failCallback) failCallback(error);
                            reject(error);
                        },
                        msgData: msgData,
                        retryCount: 0,
                    });
                },
                (error) => {
                    console.error(`消息发送失败: ${tempMsgNo}`, error);
                    if (failCallback) failCallback(error);
                    reject(error);
                }
            );
        });
    }

    /**
     * 消息应答
     * @param {string} msgNo 消息编号
     */
    answerMessage(msgNo) {
        wx.sendSocketMessage({
            data: `{"messageType":"ANSWER","info":"${msgNo}"}`,
            success: () => {
                console.log("消息应答：" + msgNo);
            },
            fail: () => {
                console.log("消息应答失败");
            },
        });
    }

    /**
     * 启动心跳检测
     */
    start() {
        this.timeoutObj = setInterval(() => {
            // 只在已连接状态下发送心跳
            if (this.isConnected) {
                wx.sendSocketMessage({
                    data: '{"messageType":"ALIVE"}',
                    success: () => {
                        console.log("心跳成功");
                    },
                    fail: () => {
                        console.log("心跳失败，连接可能已断开");
                        this.isConnected = false;
                    },
                });
            }
        }, 1000); // 1秒心跳
    }

    /**
     * 重置心跳检测
     */
    reset() {
        clearInterval(this.timeoutObj);
        this.start();
    }

    /**
     * 关闭连接
     */
    close() {
        this.reConnect = false;
        this.isConnected = false;
        this.isConnecting = false;

        // 清理所有待确认消息
        this.clearPendingMessages("连接已关闭");

        // 清理心跳定时器
        if (this.timeoutObj) {
            clearInterval(this.timeoutObj);
            this.timeoutObj = null;
        }

        // 关闭Socket连接
        if (this.socketTask) {
            wx.closeSocket({
                success: (res) => {
                    console.log("关闭socket成功", res);
                },
                fail: (err) => {
                    console.log("关闭socket失败", err);
                },
            });
            this.socketTask = null;
        }

        // 重置监听器标记，允许下次重新添加
        this.listenersAdded = false;
    }

    /**
     * 获取连接状态
     */
    getConnectStatus() {
        return this.isConnected;
    }

    /**
     * 设置回调函数
     * @param {string} type 回调类型 (onMessage/onConnect/onError/onClose)
     * @param {function} callback 回调函数
     */
    setCallback(type, callback) {
        if (this.callbacks.hasOwnProperty(type)) {
            this.callbacks[type] = callback;
        }
    }

    /**
     * 处理服务端消息确认
     * @param {object} confirmData 确认消息数据
     */
    handleMessageConfirm(confirmData) {
        const { tempMsgNo } = confirmData;
        console.log(`收到服务端消息确认: ${tempMsgNo}`);

        const pendingMessage = this.pendingMessages.get(tempMsgNo);
        if (pendingMessage) {
            // 清除超时定时器
            if (pendingMessage.timer) {
                clearTimeout(pendingMessage.timer);
            }

            // 从待确认队列中移除
            this.pendingMessages.delete(tempMsgNo);

            // 调用成功回调
            if (pendingMessage.resolve) {
                pendingMessage.resolve(confirmData);
            }
        } else {
            console.warn(`未找到对应的待确认消息: ${tempMsgNo}`);
        }
    }

    /**
     * 添加待确认消息
     * @param {string} tempMsgNo 临时消息ID
     * @param {object} messageInfo 消息信息
     */
    addPendingMessage(tempMsgNo, messageInfo) {
        // 设置超时定时器
        const timer = setTimeout(() => {
            console.log(`消息确认超时，尝试重试: ${tempMsgNo}`);
            this.retryMessage(tempMsgNo);
        }, this.confirmTimeout);

        // 保存到待确认队列
        this.pendingMessages.set(tempMsgNo, {
            ...messageInfo,
            timer: timer,
            timestamp: Date.now(),
        });

        console.log(
            `添加待确认消息: ${tempMsgNo}，当前队列长度: ${this.pendingMessages.size}`
        );
    }

    /**
     * 重试发送消息
     * @param {string} tempMsgNo 临时消息ID
     */
    retryMessage(tempMsgNo) {
        const pendingMessage = this.pendingMessages.get(tempMsgNo);
        if (!pendingMessage) {
            console.warn(`重试时未找到消息: ${tempMsgNo}`);
            return;
        }

        // 增加重试次数
        pendingMessage.retryCount++;
        console.log(
            `开始第${pendingMessage.retryCount}次重试发送消息: ${tempMsgNo}`
        );

        // 清除旧的定时器
        if (pendingMessage.timer) {
            clearTimeout(pendingMessage.timer);
        }

        // 重新发送消息
        const sendMessage = {
            messageType: "IM_MESSAGE",
            info: JSON.stringify(pendingMessage.msgData),
        };

        this.sendMessage(
            sendMessage,
            (sentMsg) => {
                console.log(
                    `第${pendingMessage.retryCount}次重试发送成功: ${tempMsgNo}`
                );
                // 重新设置超时定时器
                const timer = setTimeout(() => {
                    console.log(
                        `第${pendingMessage.retryCount}次重试后仍未收到确认，继续重试: ${tempMsgNo}`
                    );
                    this.retryMessage(tempMsgNo);
                }, this.confirmTimeout);

                // 更新定时器
                pendingMessage.timer = timer;
                this.pendingMessages.set(tempMsgNo, pendingMessage);
            },
            (error) => {
                console.error(
                    `第${pendingMessage.retryCount}次重试发送失败: ${tempMsgNo}`,
                    error
                );

                // 延迟后再次重试，使用指数退避
                const retryDelay = Math.min(
                    2000 * Math.pow(1.5, pendingMessage.retryCount),
                    30000
                );
                console.log(`${retryDelay}ms后将进行下次重试`);

                const timer = setTimeout(() => {
                    this.retryMessage(tempMsgNo);
                }, retryDelay);

                pendingMessage.timer = timer;
                this.pendingMessages.set(tempMsgNo, pendingMessage);
            }
        );
    }

    /**
     * 清理所有待确认消息
     * @param {string} reason 清理原因
     */
    clearPendingMessages(reason = "连接关闭") {
        console.log(
            `清理待确认消息，原因: ${reason}，数量: ${this.pendingMessages.size}`
        );

        this.pendingMessages.forEach((pendingMessage, tempMsgNo) => {
            // 清除定时器
            if (pendingMessage.timer) {
                clearTimeout(pendingMessage.timer);
            }

            // 调用失败回调
            if (pendingMessage.reject) {
                pendingMessage.reject(new Error(reason));
            }
        });

        // 清空队列
        this.pendingMessages.clear();
    }

    /**
     * 获取待确认消息列表（用于调试）
     * @returns {Array} 待确认消息列表
     */
    getPendingMessages() {
        const messages = [];
        this.pendingMessages.forEach((message, tempMsgNo) => {
            messages.push({
                tempMsgNo: tempMsgNo,
                retryCount: message.retryCount,
                timestamp: message.timestamp,
            });
        });
        return messages;
    }

    /**
     * 用于不同页面调用这个ws方法，更新回调函数
     */
    updateCallBack(callbacks) {
        this.callbacks = { ...this.callbacks, ...callbacks };
    }
}

// 创建单例实例
const socketManager = new SocketManager();

export default socketManager;
