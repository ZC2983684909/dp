// pages/web_view/index.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    link:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      link:decodeURIComponent(options.link)
    })
  },

})