// pages_index/screen_list/index.js

import {
  newUser
} from "../../pages/api/index"
import {
  browse,
  applyPage,
  visit,
  usershieldList,
  usershieldCancel
} from "../api/index"
import {
  env
} from "../../env"
Page({
  data: {
    imgBaseURL: '',
    title: '',
    type: 1, //1.从筛选页进来；2.浏览记录；3.访客记录；4.申请次数
    params: {
      pageIndex: 1,
      pageSize: 6,
      sortType: 'latelyTime'
    },
    totalPage: 0,
    list: [],
    isLoading: false,
    isLoadingMore: false,
    isPull: false,
    scrollTop: 0,
    refresherTriggered: false,
    availableHeight: 0,
    navBarstatusHeight: 0,
    scrollHeight: 0,
    API: null,
    showMsg: false,
    cancelPBItem: {},
    noActiveList:[]
  },
  onLoad (options) {
    this.setData({
      imgBaseURL: env.imgBaseURL,
      type: options.type
    })
    if (options.type == 1) {
      //获取通信通道
      const eventChannel = this.getOpenerEventChannel()
      // 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
      eventChannel.on('searchParams', (data) => {
        console.log(data);
        this.setData({
          params: {
            ...this.data.params,
            ...data,
            otherLabel:data.otherLabel?data.otherLabel.join(','):''
          },
          API: newUser,
          title: '搜索结果'
        })
        this.getList()
      })
    } else if (options.type == 2) {
      this.setData({
        API: browse,
        title: '浏览记录'
      })
      this.getList()
    } else if (options.type == 3) {
      this.setData({
        API: visit,
        title: '访客记录'
      })
      this.getList()
    } else if (options.type == 4) {
      this.setData({
        API: applyPage,
        title: '申请记录'
      })
      this.getList()
    } else if (options.type == 5) {
      this.setData({
        API: usershieldList,
        title: '屏蔽列表'
      })
      this.getList()
    }
    this.computeAvailableHeight()
  },
  getList() {
    this.data.API(this.data.params).then(res => {
      let temp = []
      if(this.data.type==1){
        if (this.data.params.pageIndex == 1) {
          temp = res.data.rows
        } else {
          temp = [...this.data.list,...this.data.noActiveList, ...res.data.rows]
        }
        let arr1 = []
        let arr2 = []
        console.log(temp,"temp");
        temp.forEach(item => {
          if(item.active){
            arr1.push(item)
          }else{
            arr2.push(item)
          }
        })
        
        this.setData({
          totalPage: res.data.totalPage,
          list: arr1,
          noActiveList:arr2
        })
      }else{
        if (this.data.params.pageIndex == 1) {
          temp = res.data.rows
        } else {
          temp = [...this.data.list, ...res.data.rows]
        }
        this.setData({
          totalPage: res.data.totalPage,
          list: temp
        })
      }
     
     
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
  onClickLeft() {
    wx.navigateBack()
  },
  cancelPB(event) {
    let current = event.currentTarget.dataset
    this.setData({
      cancelPBItem: current.item,
      showMsg: true
    })
  },
  onClose() {
    this.setData({
      showMsg: false
    })
  },
  onConfirm() {
    usershieldCancel(this.data.cancelPBItem.id).then(res => {
      let temp = [...this.data.list]
      let idx = temp.findIndex(item => item.id == this.data.cancelPBItem.id)
      temp.splice(idx, 1)
      this.setData({
        list: temp
      })
      this.onClose()
    })
  },
  goPage(event) {
    let current = event.currentTarget.dataset
    let path = current.path
    if (current.id) {
      path = path + `?id=${current.id}`
    }
    wx.navigateTo({
      url: path
    })
  },
  // 下拉刷新事件处理
  onRefresh() {
    this.setData({
      isLoading: true,
      isPull: false,
      refresherTriggered: true,
      params: {
        ...this.data.params,
        pageIndex: 1
      }
    })
    this.getList()
  },

  // 上拉加载更多事件处理
  loadMoreData() {
    if (this.data.params.pageIndex >= this.data.totalPage) return;
    this.setData({
      isLoadingMore: true
    })
    this.setData({
      params: {
        ...this.data.params,
        pageIndex: this.data.params.pageIndex + 1
      }
    })
    this.getList()
  },
  bindrefresherpulling() {
    this.setData({
      isPull: true
    })
  },
  bindrefresherrefresh() {
    this.onRefresh()
  },
  bindrefresherrestore() {
    this.setData({
      isPull: false // 隐藏下拉图标
    });
  },
  bindrefresherabort() {
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
      availableHeight: availableHeight, // 将计算结果存储到data中
      navBarstatusHeight: navBarHeight + statusBarHeight,
      scrollHeight: availableHeight
    });
  },
})