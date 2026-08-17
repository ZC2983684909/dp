// pages_my/applay_num/index.js
import {
  amountPre
} from "../api/index"
import {
  basePay
} from "../../pages/api/common"
import {
  env
} from "../../env"
import {
  phoneSys
} from "../../utils/utils"
Page({
  data: {
    imgBaseURL: '',
    userInfo: {},
    payNum: 0,
    act: 0,
    showTip:false,
    showMsg: false
  },
  onLoad() {
    this.setData({
      imgBaseURL: env.imgBaseURL,
    })
    this.getAmountPre()
  },
  getAmountPre() {
    amountPre().then(res => {
      this.setData({
        userInfo: res.data
      })
    })
  },
  onClickLeft() {
    wx.navigateBack()
  },
  confirm() {
    let self = this
    let params = {
      platform: "WX_APPLET",
      beanName: "AddApplyPayServiceImpl",
      price: this.data.payNum
    }
    basePay(params).then(res2 => {
      const {
        timeStamp,
        nonceStr,
        packageVal,
        signType,
        paySign
      } = res2.data.chnlFrontParamInfo
      wx.requestPayment({
        timeStamp,
        nonceStr,
        package: packageVal,
        signType,
        paySign,
        success(res) {
          self.getAmountPre()
          wx.showToast({
            title: '支付成功',
            icon:'none'
          })
        },
        fail(err) {
          console.log(err);
        }
      })
    })
  },
  submit() {
    if (phoneSys() == 'iOS') {
      this.setData({
        showMsg: true
      })
      return
    }
    let temp = this.data.userInfo.balancePriceVos[this.data.act]
    this.setData({
      payNum: temp.price,
    })
    this.confirm()
  },
  select(event) {
    this.setData({
      act: event.currentTarget.dataset.index
    })
  },
  goPage(event) {
    let path = event.currentTarget.dataset.path
    wx.navigateTo({
      url: path
    })
  },
  showTipMsg(){
    this.setData({
      showTip:true
    })
  },
  onClose() {
    this.setData({
      showMsg: false
    })
  }
})