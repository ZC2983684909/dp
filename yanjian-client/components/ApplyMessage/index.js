// components/MyMessage/index.js
import {
  env
} from "../../env"
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    title: {
      type: String,
      value: '增加申请次数'
    },
    message: {
      type: String,
      value: "本产品唯一需要付费的项目，感谢捐赠"
    },
    btnStr1: {
      type: String,
      value: "捐赠平台"
    },
    btnStr2: {
      type: String,
      value: "取消"
    },
    showBg: {
      type: String,
      value: "月亮" //背景图目前支持两个，地球和月亮
    },
    num: {
      type: Number,
    },
    applyNum: {
      type: Boolean,
      value: false
    },
    applyNumMsg:{
      type: String,
      value: '额外三次申请次数'
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    imgBaseURL: '',
  },

  lifetimes: {
    attached() {
      this.setData({
        imgBaseURL: env.imgBaseURL
      })
    }
  },
  /**
   * 组件的方法列表
   */
  methods: {
    close() {
      this.triggerEvent('onClose')
    },
    btn1() {
      this.triggerEvent('onConfirm')
    }
  }
})