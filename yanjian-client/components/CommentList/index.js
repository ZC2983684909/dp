// components/CommentList/index.js
import {
  env
} from "../../env"
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    comment: {
      type: Array,
      value: []
    }
  },
  lifetimes: {
    attached: function () {
      this.setData(({
        imgBaseURL: env.imgBaseURL,
      }))
    },
    detached: function () {
      // 在组件实例被从页面节点树移除时执行
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    imgBaseURL: '',
  },

  /**
   * 组件的方法列表
   */
  methods: {
    reply(e) {
      this.triggerEvent("reply", e.currentTarget.dataset.item)
    },
    article(e) {
      this.triggerEvent("article", e.currentTarget.dataset.item)
    },
    goPage(e) {
      const d = e.currentTarget.dataset
      if (d.anonymous === 'false') {
        this.triggerEvent("goInfo", d.id)
      }
    },
    // edit,把举报和删除放在---里边
    edit(e){
      const info = e.currentTarget.dataset.item
      this.triggerEvent("edit", info)
    }
  }
})