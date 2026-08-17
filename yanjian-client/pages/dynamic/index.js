// pages/dynamic/index.js
import {
  getDynamicList,
  delArticle,
  homeSubject,
  unreadCount,
  personalCenter,
} from "../api/index";
import {
  getPoster,
  isRegister
} from "../api/common";
import {
  env
} from "../../env";

Page({
  data: {
    imgBaseURL: "",
    active: "全国",
    currentIndex: 0,
    scrollHeight: 600,
    efresherTriggered: false,
    scrollTop: 0,
    isLoading: false,
    isLoadingMore: false,
    isPull: false,
    query1: {
      pageIndex: 1,
      pageSize: 10,
    },
    list1: [],
    totalPage1: 0,
    query2: {
      pageIndex: 1,
      pageSize: 10,
      city: "",
    },
    list2: [],
    totalPage2: 0,
    actions: [{
      name: "删除",
    }, ],
    show: false,
    showMsg: false,
    message: "",
    tempId: "",
    labelSwiper: [],
    dots: 0,
    currentDot: 0,
    indicatorStart: 0,
    maxIndicator: 5,
    shareInfo: {},
    unreadCountNum: 0,
    showPopup:false
  },
  /* 分享到好友 */
  async onShareAppMessage(res) {
    let temp = wx.getStorageSync("userInfo");

    // 如果是通过button分享，保持现有逻辑
    if (res.from === "button") {
      let shareObj = {};
      if (
        this.data.shareInfo.img.length &&
        !this.data.shareInfo.img[0].includes("mp4")
      ) {
        shareObj.img = await this.data.shareInfo.img[0];
      } else {
        shareObj.img = await this.data.shareInfo.headPortrait;
      }
      if (this.data.shareInfo.content) {
        shareObj.title =
          (await this.data.shareInfo.nickName) +
          "：" +
          this.data.shareInfo.content;
      } else {
        shareObj.title =
          (await this.data.shareInfo.nickName) +
          "发布了一条动态，去看看ta说了啥";
      }
      return {
        title: shareObj.title, //标题
        path: `pages_index/dynamic_detail/index?id=${this.data.shareInfo.id}&isHome=1&inviterId=${temp.id}`, //路由地址以及参数
        imageUrl: shareObj.img ?? "", //封面图片
      };
    }
    // 如果是通过右上角菜单分享，分享当前页面
    else {
      await this.setPoster();

      return {
        title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
        path: `/pages/dynamic/index?inviterId=${temp.id}`, //分享当前页面
        imageUrl: this.data.posterUrl, //封面图片
      };
    }
  },
  /* 分享到朋友圈 */
  async onShareTimeline() {
    let temp = wx.getStorageSync("userInfo");
    await this.setPoster();
    return {
      title: "实名认证 真人真颜 线下陪伴 兴趣社交", //标题
      path: `/pages/dynamic/index?inviterId=${temp.id}`, //分享当前页面
      imageUrl: this.data.posterUrl, //封面图片
    };
  },
  onLoad() {
    this.computeAvailableHeight();
    this.getIsRegister();
    this.getHomeSubject();
    this.getPosterFn();
    this.setData({
      imgBaseURL: env.imgBaseURL,
    });
  },
  onShow() {
    this.getUnreadCount();
  },
  getPosterFn() {
    getPoster().then((res) => {
      this.setData({
        posters: res.data,
        posterUrl: res.data[0],
      });
    });
  },
  setPoster() {
    if (this.data.posters.length) {
      const randomIndex = Math.floor(
        Math.random() * this.data.posters.length
      );
      this.setData({
        posterUrl: this.data.posters[randomIndex],
      });
    }
  },
  // 禁止用户滑动
  catchTouchMove() {
    return false;
  },
  share(event) {
    this.setData({
      shareInfo: event.currentTarget.dataset.item,
    });
  },
  getHomeSubject() {
    homeSubject().then((res) => {
      this.setData({
        labelSwiper: res.data,
        dots: res.data.length,
      });
    });
  },
  closePopup(){
    this.setData({
      showPopup:false
    })
  },
  onChange(event) {
    let tab = event.target.dataset.tab;
    if (this.data.active == tab) {
      this.setData({
        showPopup:!this.data.showPopup
      })
      return
    }
    let idx = tab == "全国" ? 0 : 1;
    this.setData({
      active: tab,
      currentIndex: idx,
    });
    if (this.data["list" + (idx + 1)].length == 0) {
      this.loadData();
    }
  },
  swiperChange(event) {
    this.setData({
      currentIndex: event.detail.current,
    });
  },
  onChangeDot(event) {
    this.updateIndicator(event.detail.current);
  },
  updateIndicator(current) {
    const {
      labelSwiper,
      maxIndicator
    } = this.data;
    let indicatorStart = Math.max(0, current - 3);

    // 如果最后几个不足5个，则从倒数第五个开始
    if (current >= labelSwiper.length - 2) {
      indicatorStart = Math.max(0, labelSwiper.length - maxIndicator);
    }
    this.setData({
      currentDot: current,
      indicatorStart,
    });
  },
  loadData() {
    if (this.data.active == "全国") {
      getDynamicList(this.data.query1)
        .then((res) => {
          let temp = [];
          if (this.data.query1.pageIndex == 1) {
            temp = res.data.rows;
          } else {
            temp = [...this.data.list1, ...res.data.rows];
          }
          this.setData({
            list1: temp,
            totalPage1: res.data.totalPage,
          });
        })
        .finally(() => {
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
        });
    } else {
      let city = wx.getStorageSync("city");
      this.setData({
        query2: {
          ...this.data.query2,
          city,
        },
      });
      getDynamicList(this.data.query2)
        .then((res) => {
          let temp = [];
          if (this.data.query2.pageIndex == 1) {
            temp = res.data.rows;
          } else {
            temp = [...this.data.list2, ...res.data.rows];
          }
          this.setData({
            list2: temp,
            totalPage2: res.data.totalPage,
          });
        })
        .finally(() => {
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
        });
    }
  },
  // 切换性别时候，全国和同城同时刷新到第一个数据
  loadDataSex() {
      getDynamicList(this.data.query1)
        .then((res) => {
          let temp = [];
          if (this.data.query1.pageIndex == 1) {
            temp = res.data.rows;
          } else {
            temp = [...this.data.list1, ...res.data.rows];
          }
          this.setData({
            list1: temp,
            totalPage1: res.data.totalPage,
          });
        })
        .finally(() => {
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
        });
      let city = wx.getStorageSync("city");
      this.setData({
        query2: {
          ...this.data.query2,
          city,
        },
      });
      getDynamicList(this.data.query2)
        .then((res) => {
          let temp = [];
          if (this.data.query2.pageIndex == 1) {
            temp = res.data.rows;
          } else {
            temp = [...this.data.list2, ...res.data.rows];
          }
          this.setData({
            list2: temp,
            totalPage2: res.data.totalPage,
          });
        })
        .finally(() => {
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
        });
  },
  getUnreadCount() {
    unreadCount().then((res) => {
      let num = res.data.commentCount + res.data.likeCount;
      this.setData({
        unreadCountNum: num,
      });
    });
  },
  // 下拉刷新事件处理
  onRefresh() {
    if (this.data.active == "全国") {
      this.setData({
        query1: {
          ...this.data.query1,
          pageIndex: 1,
        },
      });
    } else {
      this.setData({
        query2: {
          ...this.data.query2,
          pageIndex: 1,
        },
      });
    }
    this.setData({
      isLoading: true,
      isPull: false,
      refresherTriggered: true,
    });
    this.loadData();
    this.getHomeSubject();
  },
  changeSex(e) {
    if(Object.keys(e.currentTarget.dataset).length<=0){
      return
    }
    let s = e.currentTarget.dataset.sex
    if (s != this.data.query1.sex) {
      this.setData({
        query1: {
          ...this.data.query1,
          pageIndex:1,
          sex: s
        },
        query2: {
          ...this.data.query2,
          pageIndex:1,
          sex: s
        },
        showPopup:false
      })
      this.loadDataSex()
    }
  },
  loadMoreData() {
    if (this.data.active == "全国") {
      if (this.data.query1.pageIndex >= this.data.totalPage1) return;
      this.setData({
        isLoadingMore: true,
        query1: {
          ...this.data.query1,
          pageIndex: this.data.query1.pageIndex + 1,
        },
      });
    } else {
      if (this.data.query2.pageIndex >= this.data.totalPage2) return;
      this.setData({
        isLoadingMore: true,
        query2: {
          ...this.data.query2,
          pageIndex: this.data.query2.pageIndex + 1,
        },
      });
    }
    this.loadData();
  },
  // 如果内存中没有sex，有可能是第一次进入系统，先去判断有没有注册过
  getIsRegister() {
    let sex = wx.getStorageSync("sex");
    if (sex) {
      this.setData({
        query1: {
          ...this.data.query1,
          sex: sex == "女" ? "男" : "女",
        },
        query2: {
          ...this.data.query2,
          sex: sex == "女" ? "男" : "女",
        },
      });
      this.loadData();
    } else {
      wx.login({
        success: (res) => {
          isRegister({
            code: res.code,
          }).then((res2) => {
            if (!res2.data.isRegister) {
              let sex = wx.getStorageSync("sex");
              if (!sex) {
                wx.navigateTo({
                  url: "/pages_register/select_sex/index",
                });
              }
            } else {
              wx.setStorageSync("sex", res2.data.sex);
              let qsex = !res2.data.sex ?
                "" :
                res2.data.sex == "男" ?
                "女" :
                "男";
              this.setData({
                query1: {
                  ...this.data.query1,
                  sex: qsex,
                },
                query2: {
                  ...this.data.query2,
                  sex: qsex,
                },
              });
              wx.login({
                success: (res) => {
                  isRegister({
                    code: res.code,
                  }).then((res2) => {
                    if (!res2.data.isRegister) {
                      let sex = wx.getStorageSync("sex");
                      if (!sex) {
                        wx.navigateTo({
                          url: "/pages_register/select_sex/index",
                        });
                      }
                    } else {
                      wx.setStorageSync(
                        "sex",
                        res2.data.sex
                      );
                      let qsex = !res2.data.sex ?
                        "" :
                        res2.data.sex == "男" ?
                        "女" :
                        "男";
                      this.setData({
                        query2: {
                          ...this.data.query1,
                          sex: qsex,
                        },
                        query2: {
                          ...this.data.query2,
                          sex: qsex,
                        },
                      });
                      this.loadData();
                    }
                  });
                },
              });
            }
          });
        },
      });
    }
  },
  bindrefresherpulling() {
    console.log("自定义下拉刷新控件被下拉");
    this.setData({
      isPull: true,
    });
  },
  bindrefresherrefresh() {
    console.log("自定义下拉刷新被触发");
    this.onRefresh();
  },
  bindrefresherrestore() {
    console.log("自定义下拉刷新被复位");
    this.setData({
      isPull: false, // 隐藏下拉图标
    });
  },
  bindrefresherabort() {
    console.log("自定义下拉刷新被中止");
    this.setData({
      isLoading: false,
      isPull: false, // 隐藏下拉图标
    });
  },

  goPage(event) {
    let path = event.currentTarget.dataset.path;
    wx.navigateTo({
      url: event.currentTarget.dataset.id
        ? `${path}?id=${event.currentTarget.dataset.id}`
        : path,
    });
    
  },
  goPageSubmit(){
    personalCenter().then((res) => {
      wx.navigateTo({
        url: '/pages_index/post_moments/index',
      });
    });
  },
  goCommentList(event) {
    wx.navigateTo({
      url: event.currentTarget.dataset.path,
    });
  },
  // 获取页面内容高度
  computeAvailableHeight() {
    const systemInfo = wx.getWindowInfo
      ? wx.getWindowInfo()
      : wx.getSystemInfoSync();
    const statusBarHeight = Number(systemInfo.statusBarHeight) || 0;
    const menuButtonInfo = wx.getMenuButtonBoundingClientRect
      ? wx.getMenuButtonBoundingClientRect()
      : { top: statusBarHeight + 4, height: 32 };
    // 计算导航栏高度
    const navBarHeight =
      (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
    // 计算可用高度
    const availableHeight = Math.max(
      1,
      Number(systemInfo.windowHeight) - statusBarHeight - navBarHeight
    );
    // tabs的高度
    let tabsHeight = 0;
    const query = wx.createSelectorQuery();
    query
      .select(".tabs")
      .boundingClientRect((res) => {
        tabsHeight = res && res.height ? res.height : 0;
        this.setData({
          // availableHeight: availableHeight, // 将计算结果存储到data中
          // navBarstatusHeight: navBarHeight + statusBarHeight,
          scrollHeight: Math.max(1, availableHeight - tabsHeight + 50),
        });
      })
      .exec();
  },

  onSelect(event) {
    let name = event.detail.name;
    if (name == "删除") {
      this.setData({
        message: `是否确定删除此条动态？`,
        showMsg: true,
      });
    } else if (name == "举报") {
      wx.navigateTo({
        url: `/pages_index/report/index?id=${this.data.tempId}&type=2`,
      });
    }
  },
  onConfirmMsg() {
    let params = {
      id: this.data.tempId,
    };
    delArticle(params).then((res) => {
      let temp = [];
      if (this.data.active == "全国") {
        temp = [...this.data.list1];
        let idx = temp.findIndex((item) => item.id == this.data.tempId);
        if (idx != -1) {
          temp.splice(idx, 1);
        }
        this.setData({
          showMsg: false,
          list1: temp,
        });
      } else {
        temp = [...this.data.list2];
        let idx = temp.findIndex((item) => item.id == this.data.tempId);
        if (idx != -1) {
          temp.splice(idx, 1);
        }
        this.setData({
          showMsg: false,
          list2: temp,
        });
      }
    });
  },
  onCloseMsg() {
    this.setData({
      showMsg: false,
    });
  },
  onClose() {
    this.setData({
      show: false,
    });
  },
  edit(event) {
    if (event.detail) {
      this.setData({
        actions: [{
          name: "删除",
        }, ],
      });
    } else {
      this.setData({
        actions: [{
          name: "举报",
        }, ],
      });
    }
    this.setData({
      show: true,
      tempId: event.currentTarget.dataset.id,
    });
  },
});
