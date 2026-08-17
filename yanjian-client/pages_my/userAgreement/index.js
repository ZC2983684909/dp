// pages_my/userAgreement/index.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        text: `<div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; font-size: 16px; line-height: 1.6; color: #333; padding: 0;">
        <p style="margin-bottom: 12px;">感谢您选择颜见App。为使用本平台提供的各项服务，您应当事先认真阅读并充分理解本协议的所有内容，尤其是其中与您的权益和义务密切相关的条款，包括但不限于违约责任、账号管理、隐私保护等。</p>
        <p style="margin-bottom: 12px;">当您完成身份认证时，即表示您已阅读、理解并同意接受本协议所有条款的约束。如果您不同意本协议的任何内容，请立即停止使用本平台服务。本协议将不定期更新，更新后的版本自公布之日起立即生效，并代替原有版本。如您继续使用本平台服务，即视为同意接受更新后的协议；如您不同意，请停止使用本平台服务。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">一、账号注册</h3>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 注册条件</h4>
        <p style="margin-bottom: 8px;">1）注册资格：仅限具有中国国籍且成年人（年满18周岁）注册，否则需自行承担相关责任。</p>
        <p style="margin-bottom: 8px;">2）信息真实义务：用户须保证提供的个人信息真实、准确，因用户信息不实或未及时更新所导致的全部后果由用户自行承担。</p>
        <p style="margin-bottom: 8px;">3）账号使用限制：账号仅限本人使用，禁止转让、租借、共享或售卖。如发现账号存在非本人使用情况，本平台有权封禁或回收该账号，且因此产生的损失由用户自行承担。</p>
        <p style="margin-bottom: 8px;">4）信息审查与责任：本平台有权对用户提供的信息进行审查，用户须对其所提供信息的真实性、合法性负责。若因用户信息不实给本平台或第三方造成损失，用户应依法承担赔偿责任。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 注册动机</h4>
        <p style="margin-bottom: 8px;">用户注册并使用本平台服务，须以交友、恋爱或婚姻为目的。严禁以诱导投资、销售产品、约P等其他目的接触异性会员。一经发现，本平台有权对账号进行封禁，并视情况向相关机关举报或报警处理。因上述违规行为所造成的损失，由用户自行承担。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">3. 真实信息</h4>
        <p style="margin-bottom: 8px;">1）注册时，用户需提交真实姓名、年龄、身高、照片、学校、学历、微信等资料。封面照片必须为本人真实照片，使用虚假照片或非本人照片将导致账号被封禁。提供虚假交友信息的用户，需自行承担由此带来的不利后果。</p>
        <p style="margin-bottom: 8px;">2）您理解并同意，本平台将基于您的授权，向公安系统传输您的个人身份信息（包括姓名、身份证号）以验证是否为本人注册账号。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">二、服务内容与功能介绍</h3>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 核心服务定位</h4>
        <p style="margin-bottom: 8px;">本小程序聚焦于高效、安全的社交连接场景，通过精准的用户信息展示、直接的互动通道搭建及智能的搜索匹配功能，打破社交壁垒，为用户提供从信息获取到深度连接的全链路社交服务，满足拓展人脉、兴趣交友、资源对接等多元化社交需求。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 主要功能详解</h4>
    
        <p style="margin-bottom: 8px;"><strong>1）用户信息精准查看：构建透明社交基础</strong></p>
        <p style="margin-bottom: 8px;">基于用户授权机制，为社交决策提供可靠依据，同时严格保障信息安全与隐私边界。</p>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;"><strong>信息展示维度</strong>：支持查看经过用户自主公开的核心信息，涵盖基础资料（昵称、头像、年龄、地域等）、个性化标签（兴趣爱好、职业背景、社交偏好等）及动态内容（发布的合规分享、互动记录等），帮助用户快速建立初步认知。</li>
            <li style="margin-bottom: 4px;"><strong>隐私权限管控</strong>：采用“用户自主配置 + 系统权限保护”双重机制，用户可在个人中心灵活设置信息可见范围（如仅好友可见、部分信息隐藏等），小程序仅在用户明确授权后获取并展示信息，杜绝隐私泄露风险。</li>
            <li style="margin-bottom: 4px;"><strong>信息更新同步</strong>：用户修改个人资料或动态后，系统实时同步展示内容，确保查看的信息具备时效性，为社交互动提供准确参考。</li>
        </ul>
    
        <p style="margin-bottom: 8px;"><strong>2）微信申请便捷发起：实现跨平台深度连接</strong></p>
        <p style="margin-bottom: 8px;">提供合规、高效的微信添加通道，助力用户从平台互动延伸至私域社交。</p>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;"><strong>申请流程简化</strong>：用户在查看目标用户信息页时，可直接点击“申请微信”按钮，系统将引导填写简要申请备注（如“来自 XX 兴趣圈，想交流摄影技巧”），提交后即时推送至对方消息中心。</li>
            <li style="margin-bottom: 4px;"><strong>合规实现方式</strong>：通过二维码展示或官方接口对接模式实现功能落地：对方同意申请后，可选择展示个人微信添加，全程遵循微信平台规范。</li>
            <li style="margin-bottom: 4px;"><strong>申请状态追踪</strong>：发起方可在“消息 - 申请记录”中查看申请进度（待同意、已同意、已拒绝），对方同意后将同步提示添加方式，避免沟通断层。</li>
        </ul>
    
        <p style="margin-bottom: 8px;"><strong>3）主动私聊互动：搭建即时沟通桥梁</strong></p>
        <p style="margin-bottom: 8px;">内置安全高效的聊天系统，支持用户随时发起一对一互动，深化社交连接。</p>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;"><strong>无门槛发起对话</strong>：无需对方预先同意，用户可直接通过目标用户主页的“私聊”按钮发起对话，首次消息将以“陌生人消息”形式推送，兼顾沟通效率与接收方体验。</li>
            <li style="margin-bottom: 4px;"><strong>多元化消息支持</strong>：支持发送文字、表情、图片等基础消息类型，满足不同场景下的沟通需求，如分享兴趣内容、确认对接细节等。</li>
            <li style="margin-bottom: 4px;"><strong>聊天安全保障</strong>：系统内置敏感信息过滤机制，对违规言论实时拦截；同时提供消息举报、拉黑功能，用户可随时屏蔽骚扰信息，维护良好沟通环境。</li>
        </ul>
    
        <p style="margin-bottom: 8px;"><strong>4）用户信息智能搜索：精准匹配社交目标</strong></p>
        <p style="margin-bottom: 8px;">基于多维度筛选条件，帮助用户快速定位符合需求的社交对象，提升社交效率。</p>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;"><strong>多条件组合搜索</strong>：支持通过关键词（如兴趣标签、职业关键词）、基础属性（年龄、地域、性别）、互动偏好等条件进行组合筛选，精准缩小搜索范围。</li>
            <li style="margin-bottom: 4px;"><strong>智能排序展示</strong>：搜索结果按照“匹配度优先”原则排序，综合考量用户设置的筛选条件与目标用户的信息契合度，同时可切换为“最新注册”“活跃度高”等排序方式。</li>
            <li style="margin-bottom: 4px;"><strong>模糊搜索支持</strong>：当用户输入关键词不明确时，系统将提供联想推荐功能，如输入“摄影”时，自动关联“人像摄影”“风光摄影”等相关标签及用户，降低搜索操作门槛。</li>
        </ul>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">三、免责条款</h3>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 服务范围</h4>
        <p style="margin-bottom: 8px;">平台旨在为通过认证的用户提供认识机会，已对注册资料进行基本审核，但无法全面核实用户职业、收入及婚姻状态等动态信息，也不对用户人品、动机作背书。</p>
        <p style="margin-bottom: 8px;">本平台不保证其提供的服务一定能满足用户及会员的要求和期望，也不保证服务不会中断，对服务的及时性、安全性、准确性都不作保证。</p>
        <p style="margin-bottom: 8px;">对于用户上传的照片、资料（包括身高、收入状况、所在地、家乡、工作、自我介绍等），本平台已采用相关措施并已尽合理努力进行审核，但不保证其内容的正确性、合法性或可靠性，相关责任由上传上述内容的用户负责。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 互动责任</h4>
        <p style="margin-bottom: 8px;">用户通过平台认识后的一切互动及后果由用户自行承担。平台将以诚意协助解决相关问题，但不承担法律责任。</p>
        <p style="margin-bottom: 8px;">用户以自己的独立判断从事与交友相关的行为，并独立承担可能产生的不利后果和责任。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">3. 投诉处理</h4>
        <p style="margin-bottom: 8px;">对于用户的投诉，本平台将尽合理努力认真核实，但不保证最终公之于众的投诉的真实性、合法性，对于投诉内容侵犯用户隐私权、名誉权等合法权利的，所有法律责任由投诉者承担，与本平台无关。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">4. 服务变更</h4>
        <p style="margin-bottom: 8px;">用户理解并同意，因业务发展需要，本平台保留单方面对本服务的全部或部分服务内容变更、暂停、终止或撤销的权利，用户需承担此风险。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">四、隐私保护政策</h3>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 隐私信息</h4>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">非公开信息：真实姓名、身份证号码、微信号、手机号等。</li>
            <li style="margin-bottom: 4px;">公开信息：照片、年龄、身高、家乡、职业等可能会被其他用户查看。</li>
        </ul>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 隐私保护措施</h4>
        <p style="margin-bottom: 8px;">本平台采取严格技术措施保护用户信息，防止信息泄漏、损坏或丢失。未经用户同意，本平台不会向第三方披露用户的隐私信息，以下情况除外：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">根据法律法规或主管机关指令；</li>
            <li style="margin-bottom: 4px;">用户自行公开信息；</li>
            <li style="margin-bottom: 4px;">因用户设备遗失或他人登录造成的信息泄漏；</li>
            <li style="margin-bottom: 4px;">因黑客攻击或其他不可抗力事件导致的信息泄漏；</li>
            <li style="margin-bottom: 4px;">根据法律法规规定或有权机关的指示，向其提供用户的个人隐私信息；</li>
            <li style="margin-bottom: 4px;">用户将其密码或账号信息告知他人或与他人共享，导致的任何信息泄漏；</li>
            <li style="margin-bottom: 4px;">用户自行向第三方公开其个人隐私信息；</li>
            <li style="margin-bottom: 4px;">用户与本平台及合作单位之间就用户个人隐私信息的使用公开达成约定，本平台因此向合作单位公开用户个人隐私信息；</li>
            <li style="margin-bottom: 4px;">用户的个人信息已做去标识化处理，无法识别特定个人且不能复原。</li>
        </ol>
        <p style="margin-bottom: 8px;">本平台保证在取得用户书面同意的情况下收集、使用或公开用户的个人隐私信息。用户同意，本平台无需获得其另行确认与授权即可收集、使用或公开用户的非个人隐私信息。</p>
        <p style="margin-bottom: 8px;">本平台重视对用户个人信息私密性的保护，将采取技术手段及其他必要措施，确保用户个人信息安全，防止在本服务中收集的用户个人信息被泄露、毁损或丢失。一旦发生前述情形或本平台发现存在此种风险时，本平台将及时采取补救措施并告知用户；若用户发现其个人信息存在泄露风险，也应立即与本平台联系。</p>
        <p style="margin-bottom: 8px;">本平台承诺在合法、正当、必要的原则下收集、使用或公开用户个人信息，不会收集与提供的服务无关的用户个人信息。</p>
    
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">3. 信息使用范围</h4>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">用于向用户推送产品通知及协议更新信息；</li>
            <li style="margin-bottom: 4px;">用于协助用户匹配潜在对象，并通过平台内信或电话与用户沟通；</li>
            <li style="margin-bottom: 4px;">未注册用户可通过分享链接查看部分注册用户的公开资料。</li>
        </ul>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">五、内容规范</h3>
        <p style="margin-bottom: 8px;">1. 本条所称"内容"是指用户在使用本服务过程中所制作、上载、复制、发布、传播的任何内容，包括但不限于：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">账号头像、名称、个人信息、自我描述等注册信息及认证资料；</li>
            <li style="margin-bottom: 4px;">文字、图片、图文等信息、消息或相关链接页面；</li>
            <li style="margin-bottom: 4px;">其他使用账号或本服务所产生的内容。</li>
        </ol>
        <p style="margin-bottom: 8px;">2. 用户不得利用"颜见App"账号或本服务制作、上载、复制、发布、传播以下法律、法规和政策明令禁止的违法内容：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">反对宪法所确定的基本原则的；</li>
            <li style="margin-bottom: 4px;">危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</li>
            <li style="margin-bottom: 4px;">损害国家荣誉和利益的；</li>
            <li style="margin-bottom: 4px;">煽动民族仇恨、民族歧视，破坏民族团结的；</li>
            <li style="margin-bottom: 4px;">破坏国家宗教政策，宣扬邪教和封建迷信的；</li>
            <li style="margin-bottom: 4px;">散布谣言，扰乱社会秩序，破坏社会稳定的；</li>
            <li style="margin-bottom: 4px;">散布淫秽、色情、赌博、暴力、凶杀、恐怖或教唆犯罪的；</li>
            <li style="margin-bottom: 4px;">侮辱或诽谤他人，侵害他人合法权益的；</li>
            <li style="margin-bottom: 4px;">不遵守法律法规底线、社会主义制度底线、国家利益底线、公民合法权益底线、社会公共秩序底线、道德风尚底线和信息真实性底线（"七条底线"）的；</li>
            <li style="margin-bottom: 4px;">其他法律、行政法规禁止的内容。</li>
        </ol>
        <p style="margin-bottom: 8px;">3. 用户不得利用"颜见App"账号或本服务制作、上载、复制、发布、传播以下干扰"颜见App"正常运营或侵犯其他用户或第三方合法权益的内容：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">含有任何性或性暗示的；</li>
            <li style="margin-bottom: 4px;">含有辱骂、恐吓、威胁内容的；</li>
            <li style="margin-bottom: 4px;">含有骚扰、垃圾广告、恶意信息、诱骗信息的；</li>
            <li style="margin-bottom: 4px;">涉及他人隐私、个人信息或资料的；</li>
            <li style="margin-bottom: 4px;">侵害他人名誉权、肖像权、知识产权、商业秘密等合法权利的；</li>
            <li style="margin-bottom: 4px;">含有其他干扰本服务正常运营或侵犯其他用户或第三方合法权益的信息。</li>
        </ol>
        <p style="margin-bottom: 8px;">4. 用户应坚持社会主义核心价值观，坚持正确积极导向，遵守平台内容规范，并对自身行为承担责任。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">六、使用规则</h3>
        <p style="margin-bottom: 8px;">1. 用户通过本服务所传送、发布的任何内容，不代表也不得被视为代表本平台的观点、立场或政策，本平台对此不承担任何责任。</p>
        <p style="margin-bottom: 8px;">2. 用户不得利用"颜见App"账号或本服务从事以下行为：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">提交、发布虚假信息，或盗用他人头像、资料，冒充或利用他人名义；</li>
            <li style="margin-bottom: 4px;">强制或诱导其他用户分享信息；</li>
            <li style="margin-bottom: 4px;">编造并利用虚假信息或隐瞒真相以误导、欺骗他人；</li>
            <li style="margin-bottom: 4px;">利用技术手段批量注册或建立虚假账号；</li>
            <li style="margin-bottom: 4px;">从事任何违法犯罪活动或实施违背社会道德和公序良俗的行为；</li>
            <li style="margin-bottom: 4px;">利用"颜见App"账号出售、泄露嘉宾信息；</li>
            <li style="margin-bottom: 4px;">利用"颜见App"账号向他人推销"可代其向指定客户联系"的服务（包括但不限于猎头、广告营销等）；</li>
            <li style="margin-bottom: 4px;">制作、发布与上述行为相关的方法或工具，或对这类方法、工具进行运营或传播，无论是否出于商业目的；</li>
            <li style="margin-bottom: 4px;">其他违反法律法规规定、侵犯其他用户合法权益、干扰"颜见App"正常运营或未经本平台明示授权的行为。</li>
        </ol>
        <p style="margin-bottom: 8px;">若用户有上述任何行为，本平台有权删除相关不良或违法信息，封禁或注销用户账号，并保留追究用户法律责任的权利。若因用户的违法行为或违约行为使本平台或第三人遭受损失，用户应当予以赔偿，包括但不限于律师费、交通费、公证费和合理的调查费用等。</p>
        <p style="margin-bottom: 8px;">3. 若用户投诉他人存在违法或违反本协议的行为，投诉者应当对其投诉内容的真实性负责。若投诉内容不实并损害他人合法权益，则由投诉者独立承担全部法律责任；若给本平台造成损失，投诉者亦须对本平台进行相应赔偿。</p>
        <p style="margin-bottom: 8px;">4. 用户应对其利用"颜见App"账号或本服务发布、传播的信息的真实性、合法性、无害性、准确性、有效性等承担全部责任，因该等信息产生的任何法律责任由用户自行承担，与本平台无关。若因此给本平台或第三方造成损害，用户应依法予以赔偿。</p>
        <p style="margin-bottom: 8px;">5. 本平台所提供的服务中可能包括广告，您同意在使用过程中本平台和第三方供应商、合作伙伴可向您展示广告。您应对广告内容及相关链接的真实性和可靠性自行判断并承担责任。除法律法规明确规定外，如您通过上述广告或链接网站购买商品或服务，其交易仅在您与商品或服务提供方之间进行，与本平台无关。本平台对用户和该商品或服务的提供方之间的交易不承担任何责任。</p>
        <p style="margin-bottom: 8px;">6. 除非本平台另行书面许可，用户不得从事以下行为：</p>
        <ol style="list-style-type: decimal; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">删除本平台或其副本上关于著作权的信息；</li>
            <li style="margin-bottom: 4px;">对本平台进行反向工程、反向汇编、反向编译，或以其他方式试图发现本平台的源代码；</li>
            <li style="margin-bottom: 4px;">对本平台拥有知识产权的内容进行使用、出租、出借、复制、修改、链接、转载、汇编、发表、出版或建立镜像站点等；</li>
            <li style="margin-bottom: 4px;">对本平台运行过程中释放到任何终端内存中的数据、客户端与服务器端的交互数据及运行所必需的系统数据进行复制、修改、增加、删除、挂接运行或创作衍生作品，包括但不限于使用插件、外挂或非经本平台授权的第三方工具/服务接入本平台及相关系统；</li>
            <li style="margin-bottom: 4px;">通过修改或伪造本平台运行中的指令、数据，增加、删减、变动本平台的功能或运行效果，或对此类方法、工具进行运营或向公众传播，无论是否具有商业目的；</li>
            <li style="margin-bottom: 4px;">通过非本平台开发、授权的第三方产品、插件、外挂、系统登录或使用本平台，或制作、发布、传播上述非经授权的第三方产品、插件、外挂、系统；</li>
            <li style="margin-bottom: 4px;">对本平台提供的服务之任何部分进行"帧链接（Framing）"或"镜像（Mirror）"，或以任何方法使用含有本平台标识、名称或其他信息的HTML标签、元标记（Meta Tags）、代码等，引导用户至任何其他平台。</li>
        </ol>
        <p style="margin-bottom: 8px;">7. 您理解并同意：本平台有权根据合理判断，对违反法律法规或本协议规定的行为采取相应措施，包括但不限于删除违法违规信息、封禁账号、注销账号，并向有关部门报告或配合调查。由此产生的全部法律责任由用户自行承担。在本平台提供服务过程中，如发现涉诈违法犯罪线索或风险信息，本平台有权依照国家有关规定，将涉诈风险信息移送公安、金融、电信、网信等有权部门。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">七、用户信息使用条款</h3>
        <p style="margin-bottom: 8px;">用户同意并授权本平台可在以下事项中使用或共享用户的个人信息：</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 合作共享</h4>
        <p style="margin-bottom: 8px;">经用户明确同意后，将部分资料与本平台合作的第三方共享，以便优化服务或提供额外功能。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 推荐与展示</h4>
        <p style="margin-bottom: 8px;">1）用户的个人主页可能被推荐至非注册用户（如微信等社交平台）进行查看。</p>
        <p style="margin-bottom: 8px;">2）未注册用户打开小程序后，可查看所在城市的新近注册并通过认证的用户信息。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">3. 通知服务</h4>
        <p style="margin-bottom: 8px;">用于向用户发送重要通知，如产品更新、功能升级、协议变更等。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">4. 服务优化</h4>
        <p style="margin-bottom: 8px;">为了改进平台服务，平台可能选择特定用户进行沟通或意见反馈。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">5. 管理与审查</h4>
        <p style="margin-bottom: 8px;">根据本协议约定，平台可对用户信息进行管理、审查并进行必要的处理。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">6. 精准匹配</h4>
        <p style="margin-bottom: 8px;">平台可能通过站内信、电话、短信或微信等方式与特定用户联系与沟通。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">7. 法律法规要求</h4>
        <p style="margin-bottom: 8px;">在适用法律法规或有权机关要求的情况下，平台可依法处理或提供用户信息。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">8. 特定情况下无需事先同意的说明</h4>
        <p style="margin-bottom: 8px;">在以上事项中，若存在以下情形，则无需用户事先同意即可使用或共享个人信息：</p>
        <ul style="list-style-type: disc; margin-left: 4px; margin-bottom: 8px;">
            <li style="margin-bottom: 4px;">根据法律法规或政府有权机关的指示；</li>
            <li style="margin-bottom: 4px;">用户主动公开信息，或因设备遗失、账号被盗等非平台原因导致信息泄露；</li>
            <li style="margin-bottom: 4px;">因黑客攻击、病毒侵入等不可抗力事件导致的隐私信息泄露；</li>
            <li style="margin-bottom: 4px;">其他符合法律法规规定或本协议约定的情形。</li>
        </ul>
        <p style="margin-bottom: 8px;">除上述情况外，平台不会在未经用户事先同意的情况下使用或共享用户个人隐私信息。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">八、账号管理</h3>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 账号归属</h4>
        <p style="margin-bottom: 8px;">用户注册后，账号使用权仅限注册人本人，禁止转让、租借、出售或与他人共享。如违规使用，本平台有权回收账号。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 信息修改</h4>
        <p style="margin-bottom: 8px;">用户可对个人资料进行修改，但性别与出生日期不得更改。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">3. 责任声明</h4>
        <p style="margin-bottom: 8px;">用户须妥善保管账号及密码信息，因保管不善导致的账号被盗、信息泄露或财产损失，由用户自行承担相应责任。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">九、知识产权声明</h3>
        <p style="margin-bottom: 8px;">本平台所有内容（包括但不限于文字、图片、视频、程序代码等）均受知识产权或相关法律保护。未经本平台书面授权，任何组织或个人不得以任何形式擅自使用、转载、复制、修改、展示、镜像或以其他方式侵犯本平台所拥有的知识产权。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">十、违约责任</h3>
        <p style="margin-bottom: 8px;">如用户违反本协议的任何条款或存在其他违法行为，本平台有权在无需事先通知的情况下对用户账号采取封禁、注销或终止服务等措施。情节严重者，本平台可依法将相关信息提交至公安机关或其他有权部门处理。由此产生的后果由用户自行承担。</p>
    
        <h3 style="font-size: 18px; font-weight: bold; margin-top: 24px; margin-bottom: 12px;">十一、协议生效与终止</h3>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">1. 生效</h4>
        <p style="margin-bottom: 8px;">用户完成身份认证之时，即视为同意本协议的全部条款，本协议即刻生效。</p>
        <h4 style="font-size: 16px; font-weight: bold; margin-top: 16px; margin-bottom: 8px;">2. 终止</h4>
        <p style="margin-bottom: 8px;">用户可随时通过"隐私设置"中的注销功能申请注销账号，账号注销成功后，本协议终止。</p>
        <p style="margin-bottom: 8px;">终止后，如法律另有规定或根据本协议其他条款另行约定的，本平台仍可在合理范围内保留或使用用户的信息。</p>
    
        <p style="font-size: 12px; color: #666; margin-top: 24px; margin-bottom: 0;">本协议的最终解释权归颜见App所有。</p>
    </div>`,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {},
    back() {
        wx.navigateBack();
    },
});
