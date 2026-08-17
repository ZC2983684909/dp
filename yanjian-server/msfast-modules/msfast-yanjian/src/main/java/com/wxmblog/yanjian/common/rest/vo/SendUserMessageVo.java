package com.wxmblog.yanjian.common.rest.vo;

import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendUserMessageVo {

    private String userId;

    private SendUserMessageTypeEnum type;

    private SendUserMessageEnum title;

    private String content;

    private List<SmsData> smsDataList;

    private Boolean smsAlways;
}
