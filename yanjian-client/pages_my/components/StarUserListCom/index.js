import {
  env
} from "../../../env"
import {
  tuserStar,
  tuserCancelStar
} from "../../api/index"
Component({
  properties: {
    scrollHeight: {
      type: Number,
      value: 0
    },
    api: {
      type: Function,
      observer: function (val) {
        this.setApi(val)
      }
    },
    type: {
      type: String,
      value: ''
    }
  },
  data: {
    imgBaseURL: '',
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
    queryApi: null
  },
  lifetimes: {
    attached() {
      this.setData({
        imgBaseURL: env.imgBaseURL
      })
    },
  },
  methods: {
    setApi(val) {
      this.setData({
        queryApi: val
      })
      this.setUserLocation()
      this.loadData()
    },
    setUserLocation() {
      this.setData({
        query: {
          ...this.data.query,
        }
      })
    },
    itemTap(e) {
      let id = e.currentTarget.dataset.item.id
      wx.navigateTo({
        url: '/pages_index/friend_detail/index?id=' + id,
      })
    },
    loadData() {
      this.data.queryApi(this.data.query).then(res => {
        let temp = []
        if (this.data.query.pageIndex == 1) {
          temp = res.data.rows.map(item => {
            return {
              ...item,
            isStar:true
            }
          })
        } else {
          temp = [...this.data.list, ...res.data.rows].map(item => {
            return {
              ...item,
            isStar:true
            }
          })
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
    star(val) {
      let item = val.currentTarget.dataset.item
      tuserStar(item.id).then(res => {
        let tempList = [...this.data.list]
        let idx = tempList.findIndex(el => el.id == item.id)
        if (idx != -1) {
          item.isFriend = res.data
          item.isStar = true
          tempList.splice(idx, 1, item)
          this.setData({
            list: tempList
          })
        }
      })
    },
    fsStar(val) {
      let item = val.currentTarget.dataset.item
      tuserStar(item.id).then(res => {
        let tempList = [...this.data.list]
        let idx = tempList.findIndex(el => el.id == item.id)
        if (idx != -1) {
          item.isFriend = res.data
          tempList.splice(idx, 1, item)
          this.setData({
            list: tempList
          })
        }
      })
    },
    cancelStar(val) {
      let item = val.currentTarget.dataset.item
      tuserCancelStar(item.id).then(res => {
        let tempList = [...this.data.list]
        let idx = tempList.findIndex(el => el.id == item.id)
        if (idx != -1) {
          item.isFriend = false
          item.isStar = false
          tempList.splice(idx, 1, item)
          this.setData({
            list: tempList
          })
        }
      })
    },
    fsCancelStar(val) {
      let item = val.currentTarget.dataset.item
      tuserCancelStar(item.id).then(res => {
        let tempList = [...this.data.list]
        let idx = tempList.findIndex(el => el.id == item.id)
        if (idx != -1) {
          item.isFriend = false
          tempList.splice(idx, 1, item)
          this.setData({
            list: tempList
          })
        }
      })
    },
    updateItem(value, index) {
      let list = [...this.data.list]
      list.splice(index, 1, value)
      this.setData({
        list: list
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
    }
  }
})