// components/SubjectList/index.js
import {
  env
} from "../../env";
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    subjectList:{
      type:Array,
      value:[]
    },
    style:{
      type:String,
      value:''
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
        imgBaseURL: env.imgBaseURL,
      })
    }
  },

  /**
   * 组件的方法列表
   */
    methods: {
    goDetail(event){
      this.triggerEvent("detail", {
        id: event.currentTarget.dataset.id,
      });
    }
  }
})
