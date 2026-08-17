// pages_my/aboutUsSub/index.js
Page({
  data: {

  },

  onLoad(options) {

  },
  back(){
    wx.navigateBack()
  },
  goPage(event){
    wx.navigateTo({
      url: event.currentTarget.dataset.path
    })
  },
})