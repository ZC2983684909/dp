// pages_register/select_sex/index.js
import {
  env
} from "../../env"
Page({
  data: {
    imgBaseURL: '',
    sex:''
  },
  onLoad(options) {
    this.setData({
      imgBaseURL: env.imgBaseURL
    })
  },
  onShow() {

  },
  onClickLeft(){
    wx.navigateBack()
  },
  selectSex(e){
    this.setData({
      sex:e.currentTarget.dataset.sex
    })
  },
  start(){
    wx.setStorageSync('sex', this.data.sex)
    wx.reLaunch({
      url: '/pages/index/index',
    })
  }
})