import { sysconfigValue } from "../../pages/api/common";

Page({
    /**
     * 页面的初始数据
     */
    data: {
        text: "",
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.getPrizeRatio();
    },

    back() {
        wx.navigateBack();
    },
    getPrizeRatio() {
        sysconfigValue({
            code: "prizeRatio",
        }).then((res) => {
            let data = JSON.parse(res.data);
            this.setData({
                text: `<div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; font-size: 16px; line-height: 1.6; color: #333; padding: 0px;">
  <p style="margin-bottom: 16px;"><strong>为保障活动公平性与平台生态健康，以下规则需严格遵守，违规将导致奖励取消或账号处理：</strong></p>

  <ol style="margin-bottom: 16px; padding-left: 20px;">
    <li style="margin-bottom: 16px;">
      <strong>活动核心机制</strong>
      <p style="margin: 8px 0;">本活动基于社交裂变逻辑，通过“邀请人 - 新用户 - 长期互动”的闭环模型，为邀请人提供可持续的现金收益。邀请人通过专属邀请渠道成功邀请新用户注册并活跃使用后，不仅可获得即时现金奖励，还能永久享受被邀请人后续平台行为产生的现金分成，实现“一次邀请，永久收益”。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>参与资格</strong>
      <p style="margin: 8px 0;">邀请人须为平台注册用户，且账号状态正常（无违规封禁、冻结等情况）。</p>
      <p style="margin: 8px 0;">被邀请人须为从未注册过本平台的新用户，一个微信账号视为一个独立用户。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>邀请流程</strong>
      <p style="margin: 8px 0;"><strong>邀请方式：</strong>邀请人可在平台“我的 - 邀请好友页”、“首页”、“用户详情页”、“动态列表页”、“动态详情页”等页面，或生成专属邀请码/海报，通过微信、QQ、朋友圈等合法社交渠道分享。</p>
      <p style="margin: 8px 0;"><strong>关系绑定：</strong>被邀请人通过邀请链接、邀请码或海报完成首次注册并登录后，系统将自动绑定邀请关系，该关系一经绑定不可更改。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>“有效邀请”判定标准</strong>
      <p style="margin: 8px 0;">同时满足以下条件方可视为“有效邀请”：</p>
      <ul style="margin: 8px 0 0 20px; padding-left: 0;">
        <li style="margin-bottom: 4px;">被邀请人通过邀请人的专属渠道完成注册；</li>
        <li>被邀请人为平台首次注册用户。</li>
      </ul>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>现金奖励规则</strong>
      <p style="margin: 8px 0;"><strong>（一）被邀请人消费奖励</strong><br>被邀请人在平台任意消费（如充值VIP、颜币等），邀请人可获得消费金额 <strong>${data.addVip}%</strong> 的现金奖励。</p>
      
      <p style="margin: 8px 0;"><strong>（二）解锁微信奖励</strong><br>当用户本人或其邀请的用户被其他用户消费后解锁微信联系方式时：<br>
        • 用户本人可获得消费金额 <strong>${data.myWechat}%</strong> 的现金奖励；<br>
        • 该用户的上级邀请人可获得消费金额 <strong>${data.nextWechat}%</strong> 的现金奖励。</p>

      <p style="margin: 8px 0;"><strong>（三）私聊奖励</strong><br>当用户本人或其邀请的用户被其他用户消费后主动发起私聊时：<br>
        • 用户本人可获得消费金额 <strong>${data.myWechat}%</strong> 的现金奖励；<br>
        • 该用户的上级邀请人可获得消费金额 <strong>${data.nextWechat}%</strong> 的现金奖励。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>提现规则</strong>
      <p style="margin: 8px 0;"><strong>提现门槛：</strong>现金账户可提现余额大于 0 元即可申请。</p>
      <p style="margin: 8px 0;"><strong>提现方式：</strong>联系人工客服申请提现。</p>
      <p style="margin: 8px 0;"><strong>到账时间：</strong>提现申请提交后，平台将在 1–3 个工作日内完成审核，审核通过后资金实时到账（具体以支付渠道到账时间为准）。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>禁止行为与违规处理</strong>
      <p style="margin: 8px 0;">严禁以下行为：</p>
      <ul style="margin: 8px 0 0 20px; padding-left: 0;">
        <li style="margin-bottom: 4px;">通过虚假宣传、诱导欺诈等方式邀请好友（如承诺非官方奖励、隐瞒规则等）；</li>
        <li style="margin-bottom: 4px;">使用刷单、机刷注册、批量创建虚假账号等手段制造“有效邀请”；</li>
        <li>盗用他人信息注册，或协助被邀请人规避平台规则（如多人共用同一账号）。</li>
      </ul>
      <p style="margin: 8px 0;"><strong>违规后果：</strong>一经发现，平台将立即取消邀请人奖励资格，冻结现金账户余额；情节严重者将封禁账号并保留追究法律责任的权利。</p>
    </li>

    <li style="margin-bottom: 16px;">
      <strong>其他说明</strong>
      <p style="margin: 8px 0;">• 平台有权根据活动数据（如分享率、转化率、成本控制等）动态调整奖励金额、分成比例及阶梯档位，调整前将提前 3 天在活动页面公示。</p>
      <p style="margin: 8px 0;">• 若被邀请人账号因违规被封禁，邀请人将不再享受其后续行为带来的分成收益，但已结算的奖励不受影响。</p>
      <p style="margin: 8px 0;">• 邀请人须确保提现账户信息真实有效，因信息错误导致的提现失败，平台不承担补发责任。</p>
      <p style="margin: 8px 0;"><strong>本活动最终解释权归平台所有，如有疑问请咨询平台客服。</strong></p>
    </li>
  </ol>

  <p style="margin-bottom: 0;"><strong>请您认真阅读并遵守以上规则，共同营造公平、健康的平台生态。</strong></p>
</div>`,
            });
        });
    },
});
