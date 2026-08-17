// pages_my/subscribe/index.js
import {sysconfigValue} from "../../pages/api/common"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    img:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getSys()
  },
  back(){
    wx.navigateBack()
  },
  getSys(){
    sysconfigValue({code: "useSubscribe"}).then(res => {
      this.setData({
        img:res.data
      })
    })
  }
})