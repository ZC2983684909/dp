// components/SeeHelloMsg/index.js
import {env} from "../../env"
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    
    message:{
      type:String,
      value:"微信申请已提交，对方同意后即可查看Ta的微信号哦~"
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
    onReject(){
      this.triggerEvent('onReject')
    },
    onResolve(){
      this.triggerEvent('onResolve')
    },

  }
})