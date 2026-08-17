// pages_my/privacySet/index.js
import {
  setInvisible,
  removeUser
} from "../api/index";
import {
  personalCenter
} from "../../pages/api/index";
import {
  removeStorageItem
} from "../../utils/utils";
Page({
  data: {
    showPrivacy: false,
    message: "",
    btnStr: "",
    userInfo: {},
    showPrivacy2: false,
  },
  onLoad(options) {
    this.getUser();
  },
  back() {
    wx.navigateBack();
  },
  getUser() {
    personalCenter().then((res) => {
      this.setData({
        userInfo: res.data,
      });
    });
  },
  goPage(event) {
    wx.navigateTo({
      url: event.currentTarget.dataset.path,
    });
  },
  setting() {
    let msg = "";
    let btnStr = "";
    if (this.data.userInfo.invisible == 2) {
      msg =
        "每周只能隐身一次，设置隐身后不会被搜索到，也无法查看对方主页";
      btnStr = "确认隐身";
    } else {
      msg = "设置公开之后，在首页可以被搜索到";
      btnStr = "确认公开";
    }
    this.setData({
      showPrivacy: true,
      message: msg,
      btnStr,
    });
  },
  exit() {
    this.setData({
      showPrivacy: true,
      message: "确认退出登录吗？",
      btnStr: "确认退出"
    });
  },
  closeSet() {
    this.setData({
      showPrivacy: false,
    });
  },
  confirmSet() {
    if (this.data.btnStr == '确认退出') {
      removeStorageItem()
      wx.reLaunch({
        url: "/pages/index/index",
    });
    } else {
      setInvisible({
          status: this.data.userInfo.invisible == 2 ? 1 : 2,
        })
        .then((res) => {
          this.getUser();
        })
        .catch((err) => {
          wx.showToast({
            title: err.msg,
            icon: "none",
          });
        })
        .finally(() => {
          this.closeSet();
        });
    }

  },
  remove() {
    this.setData({
      showPrivacy2: true,
    });
  },
  closeSet2() {
    this.setData({
      showPrivacy2: false,
    });
  },
  confirmSet2() {
    removeUser().then((res) => {
      this.closeSet2();
      wx.clearStorageSync();
      wx.reLaunch({
        url: "/pages/index/index",
      });
    });
  },
});