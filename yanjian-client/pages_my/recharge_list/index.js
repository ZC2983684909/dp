// pages_my/apply_user_list/index.js
import {
  useraccountPage
} from "../api/index"
import {env} from "../../env"
Page({
  data: {
    imgBaseURL:'',
    query: {
      pageIndex: 1,
      pageSize: 15,
    },
    list: [],
    totalPage: 0,
    isLoading: false,
    isLoadingMore: false,
    isPull: false,
    scrollTop: 0,
    refresherTriggered: false,
  },
  onLoad(options) {
    this.computeAvailableHeight()
    this.loadData()
    this.setData({
      imgBaseURL: env.imgBaseURL,
    })
  },
  onShow() {
  },
  back() {
    wx.navigateBack()
  },

  loadData() {
    useraccountPage(this.data.query).then(res => {
      let temp = []
      if (this.data.query.pageIndex == 1) {
        temp = res.data.rows
      } else {
        temp = [...this.data.list, ...res.data.rows]
      }
      this.setData({
        list: temp,
        totalPage: res.data.totalPage
      })
    }).finally(() => {
      if (this.data.refresherTriggered) {
        this.setData({
          scrollTop: 0,
          isLoading: false,
          refresherTriggered: false
        })
      }
      if (this.data.isLoadingMore) {
        this.setData({
          isLoadingMore: false
        })
      }
    })
  },
  // 下拉刷新事件处理
  onRefresh() {
    this.setData({
      isLoading: true,
      isPull: false,
      refresherTriggered: true,
      query: {
        ...this.data.query,
        pageIndex: 1
      }
    })
    this.loadData()
  },

  // 上拉加载更多事件处理
  loadMoreData() {
    if (this.data.query.pageIndex >= this.data.totalPage) return;
    this.setData({
      isLoadingMore: true,
      query: {
        ...this.data.query,
        pageIndex: this.data.query.pageIndex + 1
      }
    })
    this.loadData()

  },
  bindrefresherpulling() {
    console.log("自定义下拉刷新控件被下拉");
    this.setData({
      isPull: true,
    })
  },
  bindrefresherrefresh() {
    console.log("自定义下拉刷新被触发");
    this.onRefresh()
  },
  bindrefresherrestore() {
    console.log("自定义下拉刷新被复位");
    this.setData({
      isPull: false // 隐藏下拉图标
    });
  },
  bindrefresherabort() {
    console.log("自定义下拉刷新被中止");
    this.setData({
      isLoading: false,
      isPull: false // 隐藏下拉图标
    });
  },

  // 获取页面内容高度
  computeAvailableHeight() {
    const systemInfo = wx.getWindowInfo();
    const statusBarHeight = systemInfo.statusBarHeight; // 状态栏高度
    const menuButtonInfo = wx.getMenuButtonBoundingClientRect(); // 胶囊按钮信息
    // 计算导航栏高度
    const navBarHeight = (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
    // 计算可用高度
    const availableHeight = systemInfo.windowHeight - statusBarHeight - navBarHeight;
    this.setData({
      scrollHeight: availableHeight
    });
  },
})