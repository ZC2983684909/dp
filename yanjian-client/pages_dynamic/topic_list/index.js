// pages_dynamic/topic_list/index.js
import {subjectList } from "../../pages/api/index"
import { env } from "../../env";
Page({
    /**
     * 页面的初始数据
     */
    data: {
      imgBaseURL: "",
        params: {
            pageIndex: 1,
            pageSize: 8,
        },
        totalPage: 0,
        list: [],
        isLoading: false,
        isLoadingMore: false,
        isPull: false,
        scrollTop: 0,
        refresherTriggered: false,
        scrollHeight: 0,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.computeAvailableHeight();
        this.getList()
        this.setData({
          imgBaseURL: env.imgBaseURL,
        })
    },
    onClickLeft() {
        wx.navigateBack();
    },
    goPage(event) {
      const id = event.currentTarget.dataset.id;
      wx.navigateTo({
        url: id
          ? `/pages_dynamic/topic_detail/index?id=${id}`
          : "/pages_dynamic/topic_list/index",
      });
    },
    getList() {
        subjectList(this.data.params).then(res => {
          let temp  = []
          if (this.data.params.pageIndex == 1) {
            temp = res.data.rows
          } else {
            temp = [...this.data.list, ...res.data.rows]
          }
          this.setData({
            totalPage: res.data.totalPage,
            list: temp
          })
        }).finally(()=>{
          if (this.data.refresherTriggered) {
              this.setData({
                  scrollTop: 0,
                  isLoading: false,
                  refresherTriggered: false,
              });
          }
          if (this.data.isLoadingMore) {
              this.setData({
                  isLoadingMore: false,
              });
          }
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
                pageIndex: 1,
            },
        });
        this.getList();
    },

    // 上拉加载更多事件处理
    loadMoreData() {
        if (this.data.params.pageIndex >= this.data.totalPage) return;
        this.setData({
            isLoadingMore: true,
        });
        this.setData({
            params: {
                ...this.data.params,
                pageIndex: this.data.params.pageIndex + 1,
            },
        });
        this.getList();
    },
    bindrefresherpulling() {
        this.setData({
            isPull: true,
        });
    },
    bindrefresherrefresh() {
        this.onRefresh();
    },
    bindrefresherrestore() {
        this.setData({
            isPull: false, // 隐藏下拉图标
        });
    },
    bindrefresherabort() {
        this.setData({
            isLoading: false,
            isPull: false, // 隐藏下拉图标
        });
    },
    // 获取页面内容高度
    computeAvailableHeight() {
        const systemInfo = wx.getWindowInfo();
        const statusBarHeight = systemInfo.statusBarHeight; // 状态栏高度
        const menuButtonInfo = wx.getMenuButtonBoundingClientRect(); // 胶囊按钮信息
        // 计算导航栏高度
        const navBarHeight =
            (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
        this.setData({
            scrollHeight: systemInfo.windowHeight-statusBarHeight - navBarHeight,
        });
    },
});
