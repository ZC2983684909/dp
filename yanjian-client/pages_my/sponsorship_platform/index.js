// pages_my/applay_num/index.js
import {
  basePay,
  sysconfigValue
} from "../../pages/api/common"
import {
  personalCenter
} from "../../pages/api/index.js"
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
    showMsg: false,
    arr: []
  },
  onLoad() {
    this.setData({
      imgBaseURL: env.imgBaseURL,
    })
    this.getUser()
    this.getSponsor()
  },
  getUser() {
    personalCenter().then(res => {
      this.setData({
        userInfo: res.data
      })
    })
  },
  getSponsor() {
    sysconfigValue({
      code: 'sponsor'
    }).then(res => {
      let arr = []
      JSON.parse(res.data).forEach(element => {
        arr.push(element.amount)
      });
      arr = arr.slice(0, 3)
      this.setData({
        arr: arr
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
      beanName: "SponsorPayServiceImpl",
      amount: this.data.payNum
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
          self.getUser()
          wx.showToast({
            title: '支付成功',
            icon: 'none'
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
    let payNum = this.data.arr[this.data.act]
    this.setData({
      payNum: payNum,
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
  onClose() {
    this.setData({
      showMsg: false
    })
  }
})