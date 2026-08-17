// components/MyMessage/index.js
import {env} from "../../env"
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    
    message:{
      type:String,
      value:"微信申请已提交，对方同意后即可查看Ta的微信号哦~"
    },
    btnStr:{
      type:String,
      value:"确认隐身"
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
    onClose(){
      this.triggerEvent('onClose')
    },
    onConfirm(){
      this.triggerEvent('onConfirm')
    }
  }
})