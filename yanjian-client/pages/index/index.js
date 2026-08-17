// index.js
import { personalCenter, getpopup } from "../api/index";
import { allCity, isRegister, getPoster } from "../api/common";
import { recursiveTransform, getLocationByStorage } from "../../utils/utils";
import { env } from "../../env";
import { getAddress } from "../../utils/getAddress";
Page({
    data: {
        imgBaseURL: "",
        tabs: ["附近", "推荐", "新人"],
        activeTab: 1,
        params: {
            city: "全国",
        },
        showMessage: false,
        // 首屏先给一个可见高度，避免设备信息尚未返回时正文高度为 0。
        scrollHeight: 600,
        show4: false,
        show4Options: [],
        mainActiveIndex: 0,
        activeId: null,
        popupList: [],
        showPopupIdx: 0,
        showVipDialog: false,
        showTip: false,
    },
    async onLoad() {
        this.computeAvailableHeight();
        this.getpopupFn();
        this.getAllCity();
        this.setData({
            imgBaseURL: env.imgBaseURL,
        });
        this.getStorageLocation();
        this.getPosterFn();
        this.getLocationFn();
    },
    async onShow() {
        let tempParams = this.data.params;
        let searchData = wx.getStorageSync("searchData");
        if (searchData) {
            let isUpdate = false;
            Object.keys(searchData).forEach((item) => {
                let v1 = searchData[item];
                let v2 = tempParams[item];
                if (v1 != v2) {
                    isUpdate = true;
                }
            });
            if (isUpdate) {
                this.setData({
                    params: {
                        ...tempParams,
                        ...searchData,
                    },
                    activeId: searchData.city,
                });
            }
        }
        try {
            if (!tempParams.sex) {
                await this.getIsRegister();
            }
        } catch (error) {
            console.log(error, "error");
        }
    },
    /* 分享到好友 */
    async onShareAppMessage() {
        let temp = wx.getStorageSync("userInfo");
        await this.setPoster();
        return {
            title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
            path: `pages/index/index?inviterId=${temp.id}`,
            imageUrl: this.data.posterUrl,
        };
    },
    /* 分享到朋友圈 */
    async onShareTimeline() {
        let temp = wx.getStorageSync("userInfo");
        await this.setPoster();
        return {
            title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
            path: `pages/index/index?inviterId=${temp.id}`,
            imageUrl: this.data.posterUrl,
        };
    },
    // getpopup
    getpopupFn() {
        getpopup({
            location: "home",
        }).then((res) => {
            this.setData({
                popupList: res.data,
                showPopupIdx: 0,
            });
        });
    },
    // 如果用户之前有授权过地址，先用之前的去请求
    getStorageLocation() {
        let obj = {};
        let city = wx.getStorageSync("city");
        let sex = wx.getStorageSync("sex");
        let county = wx.getStorageSync("county");
        if (!sex && env.isDev) {
            // 开发工具预览默认展示一组用户列表，不影响正式环境注册流程。
            sex = "\u7537";
        }
        obj = {
            city: city || "全国",
            county: county || "",
            sex: !sex ? "" : sex == "男" ? "女" : "男",
        };
        this.setData({
            params: {
                ...this.data.params,
                ...obj,
            },
            activeId: city || "",
        });
    },
    getLocationFn() {
        return new Promise((resolve, reject) => {
            if (
                this.data.params.city &&
                !this.data.params.county &&
                this.data.activeId
            ) {
                resolve();
                return;
            }
            getAddress()
                .then((res) => {
                    let { city, county, isMove } = res;
                    if (!this.data.activeId || isMove) {
                        wx.setStorageSync("city", city);
                        wx.setStorageSync("county", county);
                        this.setData({
                            params: {
                                ...this.data.params,
                                city: city || "全国",
                                county: county || "",
                            },
                            activeId: city,
                        });
                    }
                    resolve();
                })
                .catch((err) => {
                    reject(err);
                });
        });
    },
    // 如果内存中没有token和sex，有可能是第一次进入系统，先去判断有没有注册过
    getIsRegister() {
        return new Promise((resolve, reject) => {
            wx.login({
                success: (res) => {
                    isRegister({
                        code: res.code,
                    }).then((res2) => {
                        if (!res2.data.isRegister) {
                            let sex = wx.getStorageSync("sex");
                            if (!sex) {
                                wx.navigateTo({
                                    url: "/pages_register/select_sex/index",
                                });
                                reject();
                            }
                        } else {
                            wx.setStorageSync("sex", res2.data.sex);
                            this.setData({
                                params: {
                                    ...this.data.params,
                                    sex: !res2.data.sex
                                        ? ""
                                        : res2.data.sex == "男"
                                        ? "女"
                                        : "男",
                                },
                            });
                            resolve();
                        }
                    });
                },
            });
        });
    },
    getPosterFn() {
        getPoster().then((res) => {
            this.setData({
                posters: res.data,
                posterUrl: res.data[0],
            });
        });
    },
    setPoster() {
        if (this.data.posters.length) {
            const randomIndex = Math.floor(
                Math.random() * this.data.posters.length
            );
            this.setData({
                posterUrl: this.data.posters[randomIndex],
            });
        }
    },
    onChangeTab(e) {
        this.setData({
            activeTab: e.currentTarget.dataset.index,
        });
    },
    swiperChange(event) {
        this.setData({
            activeTab: event.detail.current,
        });
    },
    closePopup() {
        this.setData({
            showPopupIdx: this.data.showPopupIdx + 1,
        });
    },
    popupJump() {
        let path = this.data.popupList[this.data.showPopupIdx].link;
        if (path !== null) {
          if(path.includes("https")){
            this.goLink(path)
          }else{
            wx.navigateTo({
              url: path,
          });
          }
        }
    },
    goLink(path) {
      if (path) {
          wx.navigateTo({
              url: "/pages/web_view/index?link=" + encodeURIComponent(path),
          });
      }
  },
    itemTap(event) {
        wx.navigateTo({
            url: "/pages_index/friend_detail/index?id=" + event.detail.id,
        });
    },
    showSimilarityTip() {
        this.setData({
            showTip: true,
        });
    },
    getAllCity() {
        allCity().then((res) => {
            let temp = recursiveTransform(res.data);
            this.setData({
                show4Options: temp,
            });
        });
    },
    openMessage() {
        this.setData({
            showMessage: true,
        });
    },
    onClose() {
        this.setData({
            showMessage: false,
        });
    },
    changeshow4() {
        this.setData({
            show4: true,
        });
    },
    onClose4() {
        this.setData({
            show4: false,
        });
    },
    onClickNav4({ detail = {} }) {
        this.setData({
            mainActiveIndex: detail.index || 0,
        });
    },
    onClickItem4({ detail = {} }) {
        if (this.data.activeId == detail.id) {
            return;
        }
        this.setData({
            activeId: detail.id,
            params: {
                ...this.data.params,
                pageIndex: 1,
                city: detail.id,
            },
            show4: false,
        });
        this.onClose4();
    },
    goScreen(event) {
        personalCenter().then((res) => {
            let { path } = event.currentTarget.dataset;
            wx.navigateTo({
                url: `${path}?sex=${this.data.params.sex}`,
            });
        });
    },
   
    closeVipDialog() {
        this.setData({
            showVipDialog: false,
        });
    },
    openVip() {
        this.setData({
            showVipDialog: true,
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
            scrollHeight: availableHeight,
        });
    },
});
