// pages_my/login_reward/index.js
import {env} from "../../env"
import {signinCount,signin,exchange} from "../api/index"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgBaseURL:'',
    count:0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      imgBaseURL:env.imgBaseURL
    })
    this.getSigninCount()
  },
  back() {
    wx.navigateBack()
  },
  getSigninCount(){
    signinCount().then(res => {
      this.setData({
        count:res.data.count
      })
    })
  },
  dh(){
    if(this.data.count<7){
      wx.showToast({
        title: '此轮兑换累计登录未满7天，暂不可兑换',
        icon:'none'
      })
      return
    }
    exchange().then(res=>{
      wx.showToast({
        title: '兑换成功',
        icon:'none'
      })
      this.getSigninCount()
    })
  },
  qd(){
    signin().then(res=>{
      wx.showToast({
        title: '签到成功',
        icon:'none'
      })
      this.getSigninCount()
    })
  }
})