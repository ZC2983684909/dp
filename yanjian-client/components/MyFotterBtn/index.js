// components/MyFotterBtn/index.js
Component({
  options: {
    // 设置其他页面的样式和组件使用者的样式 防止冲突样式 需要添加类名
     styleIsolation: 'shared'
  },
  /**
   * 组件的属性列表
   */
  properties: {
    color:{
      type:String,
      value:'#2B2B2B'
    },
    openType:{
      type:String,
      value:'-'
    }
  },
  

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    btnTap(){
      this.triggerEvent('btnTap')
    },
    getPhoneNumber(value){
      this.triggerEvent('getPhoneNumber',value)
    }
  }
})