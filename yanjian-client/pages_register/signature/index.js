// pages_register/register/signature/index.js
import {
  editUserInfo
} from "../api/index"
import {
  showFn
} from "../../utils/myHttpMessage";
Page({
  data: {
    type: 1,
    userInfo: {
      selfDescription: ''
    },
  },
  onLoad(options) {
    this.setData({
      type: options.type
    })
    if (options.type == 1) {
      const pages = getCurrentPages();
      const prePage = pages[pages.length - 2];
      let temp = prePage.data.userInfo
      this.setData({
        userInfo: {
          selfDescription: temp.selfDescription
        }
      })
    }
  },
  back() {
    wx.navigateBack()
  },
  onChange(value) {
    this.setData({
      userInfo: {
        ...this.data.userInfo,
        selfDescription: value.detail
      }
    })
  },
  submit() {
    if (!this.data.userInfo.selfDescription||this.data.userInfo.selfDescription < 20 ) {
      wx.showToast({
        title: '请输入自我描述且字数20字以上',
        icon: 'none'
      })
      return
    }
    let data = {
      ...this.data.userInfo
    }
    editUserInfo(data).then(res => {
      const pages = getCurrentPages();
      const prePage = pages[pages.length - 2];
      prePage.setData({
        userInfo: {
          ...prePage.data.userInfo,
          selfDescription: data.selfDescription,
        }
      })
      wx.navigateBack()
    }).catch(err => {
      if (err.code == "12034") {
        showFn( {
          message: err.msg,
      },)
      }
    })
  },
  validate() {
    let msg = ''
    let userInfo = this.data.userInfo
    if (!userInfo.selfDescription || userInfo.selfDescription.length < 20) {
      msg = "自我描述至少20字"
    }
    if (msg) {
      wx.showToast({
        title: msg,
        icon: 'none'
      })
      return false
    } else {
      return true
    }
  },
})