// components/MyScroll/index.js
import {
  praiseArticle,
  cancelPraiseArticle
} from "../../../pages/api/index"
import {
  env
} from "../../../env"
Component({
  properties: {
    scrollHeight: {
      type: Number,
      value: 0
    },
    type: {
      type: Number,
    },
    api: {
      type: Function
    },
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
    refresherTriggered: false
  },
  lifetimes: {
    attached() {
      this.setData(({
        imgBaseURL: env.imgBaseURL,
      }))
      this.loadData()
    }
  },
  methods: {
    tapItem(event) {
      let info = event.currentTarget.dataset.info
      this.triggerEvent('tapItem', info)
    },
    goUserInfo(event){
      let info = event.currentTarget.dataset.info
      this.triggerEvent('goUserInfo', info)
    },
    praise(event) {
      let idx = event.currentTarget.dataset.index
      const info = event.currentTarget.dataset.item
      let API = info.isLike?cancelPraiseArticle:praiseArticle
      let data = {
        id: info.commentId,
        praiseType:'COMMENT'
        // praiseType: info.type == 1 ? 'REPLY' : info.type == 2 ? 'COMMENT' : ''
      }

      API(data).then(res => {
        let temp = {...info}
        temp.isLike = !temp.isLike
        let tempList = [...this.data.list]
        tempList.splice(idx,1,temp)
        this.setData({
          list:tempList
        })
        
      })
      // this.triggerEvent('praise',item)
    },
    reply(event) {
      const item = event.currentTarget.dataset.item
      this.triggerEvent('reply', item)
    },
    loadData() {
      let data = {
        ...this.data.query,
        type: this.data.type
      }
      this.data.api(data).then(res => {
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
    }
  }
})