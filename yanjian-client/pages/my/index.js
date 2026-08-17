// pages/my/index.js
import {
    personalCenterNoMsg,
    personalCenter,
    hasInvalidInfo,
    getUseSubscribe
} from "../api/index";
import { env } from "../../env";
import { contactCustomerService } from "../../utils/utils";
Page({
    data: {
        imgBaseURL: "",
        isLogin: false,
        userInfo: {},
        authWarningText: "",
        showAuthWarning: false,
        useSubscribe:null
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        let token = wx.getStorageSync("token");
        let userInfoStorage = wx.getStorageSync('userInfo')
        if(userInfoStorage){
          this.setData({
            userInfo:{
              ...this.data.userInfo,
              avatar:userInfoStorage.avatar,
              nickName:userInfoStorage.nickName
            }
          })
        }
        if (token) {
            this.setData({
                isLogin: true,
            });
        } else {
            this.setData({
                isLogin: false,
            });
        }
    },
    onShow() {
        this.getData(false).then(() => {
            this.checkAuthStatus(); // 确保数据加载完再检查
        });
        this.getHasInvalidInfo();
        this.getUseSubscribeFn()
    },

    getUseSubscribeFn(){
      getUseSubscribe().then(res => {
        this.setData({
          useSubscribe:res.data.useSubscribe
        })
      })
    },

    getData(msg = true) {
        let API = msg ? personalCenter : personalCenterNoMsg;
        return API()
            .then((res) => {
                this.setData({
                    userInfo: res.data,
                    isLogin: true,
                });
                let userInfoStorage = wx.getStorageSync('userInfo')
                userInfoStorage.avatar = res.data.avatar
                wx.setStorageSync('userInfo', userInfoStorage)
            })
            .catch((err) => {
                console.log(err);
                if (err.code == 10006) {
                    this.setData({
                        isLogin: false,
                        userInfo: {},
                    });
                }
            });
    },

    checkAuthStatus() {
        let warningText = "";
        // 按照优先级检查认证状态
        // console.log(this.data)
        if (this.data.userInfo.idAuth != 3) {
            warningText = "实名认证";
        } else if (this.data.userInfo.photoAuth != 3) {
            warningText = "相册认证";
        } else if (
            this.data.userInfo.eduAuth != 3 &&
            this.data.userInfo.eduAuth != 2
        ) {
            warningText = "学历认证";
        }
        this.setData({
            authWarningText: warningText,
            showAuthWarning: warningText !== "",
        });
    },

    contactFn() {
        contactCustomerService();
    },
    goPage(event) {
        let path = event.currentTarget.dataset.path;
        let name = event.currentTarget.dataset.name;
        let pages = [
            "关注我的",
            "申请我的",
            "我的申请",
            "我的关注",
            "隐私设置",
            "赞助平台",
            "我的认证",
            "访客记录",
            "浏览记录",
            "邀请好友",
            "会员服务",
        ];
        if (pages.includes(name) && !this.data.isLogin) {
            this.getData(true);
            return;
        }
        if (event.currentTarget.dataset.type) {
            path = path + "?type=" + event.currentTarget.dataset.type;
        }
        wx.navigateTo({
            url: path,
        });
    },

    closeSet() {
        this.setData({
            showPrivacy: false,
        });
    },
    confirmSet() {
        this.closeSet();
    },
    getHasInvalidInfo() {
        hasInvalidInfo().then((res) => {
            if (res.data) {
                wx.showToast({
                    title: "个人信息有误，请检查个人信息",
                    icon: "none",
                });
            }
        });
    },
});
