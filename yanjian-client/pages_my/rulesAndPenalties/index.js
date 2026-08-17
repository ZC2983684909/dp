// pages_my/privacyAgreement/index.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    text:`<div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; font-size: 16px; line-height: 1.6; color: #333; padding: 0px;">
    <p style="margin-bottom: 16px;"><strong>为维护平台秩序与用户权益，以下规则需遵守，违规将受到相应处罚：</strong></p>
    
    <ol style="margin-bottom: 16px; padding-left: 20px;">
      <li style="margin-bottom: 8px;">
        <strong>真实照片要求</strong>
        <p style="margin: 8px 0;">用户完成身份验证、学历审核后，如发现用户上传的首张照片非本人真实照片，不及时修改<strong>认证资格将被取消，账号将被删除</strong>。</p>
      </li>
      <li style="margin-bottom: 8px;">
        <strong>资料真实性要求</strong>
        <p style="margin: 8px 0;">用户完成身份验证、学历审核及照片审核后，如用户填写 “自我描述” 或 “交友倾向” 时随意凑数字或乱写，<strong>认证资格将被取消，账号被暂停使用</strong>。由此产生的任何损失，<strong>用户自行承担</strong>。</p>
      </li>
      <li style="margin-bottom: 8px;">
        <strong>微信号填写准确性</strong>
        <p style="margin: 8px 0;">用户完成身份验证、学历审核及照片审核后，如故意填写错误微信号，<strong>认证资格将被取消，账号被暂停使用</strong>。由此产生的任何损失，<strong>用户自行承担</strong>。</p>
      </li>
      <li style="margin-bottom: 8px;">
        <strong>禁止公开联系方式</strong>
        <p style="margin: 8px 0;">用户不得在注册资料中留有任何联系方式。如在“自我描述”、“交友倾向”或个人相册中提供微信ID等联系方式，<strong>一经发现，认证资格将被取消，账号将被删除</strong>。<strong>由此产生的任何损失，用户自行承担</strong>。</p>
      </li>
    </ol>
    <p style="margin-bottom: 16px;"><strong>请您认真阅读并遵守以上规则，共同维护良好的社交环境。</strong></p>
    </div>`
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  back(){
    wx.navigateBack()
  }
})