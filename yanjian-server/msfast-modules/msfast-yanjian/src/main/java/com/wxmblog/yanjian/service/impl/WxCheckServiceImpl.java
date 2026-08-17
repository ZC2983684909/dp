package com.wxmblog.yanjian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.vo.WxCheckResultVo;
import com.wxmblog.yanjian.service.WxCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxCheckServiceImpl implements WxCheckService {

    private static final String URL = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=";

    @Autowired
    private WxAppletService wxAppletService;

    @Autowired
    public RestTemplate restTemplate;

    @Override
    public ServiceR<WxCheckResultVo> msgSecCheck(String content, Integer scene, String code) {

        String accessToken = wxAppletService.getAccessToken();

        Map param = new HashMap();
        param.put("content", content);
        param.put("version", 2);
        param.put("scene", scene);
        param.put("openid", wxAppletService.getOpenIdInfoByCode(code).getOpenId());

        String result =restTemplate.postForObject(URL + accessToken, param, String.class);
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null&& errcode != 0) {
            throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg(jsonObject.getString("errmsg"));
        }
        JSONObject checkResult = jsonObject.getJSONObject("result");
        if (checkResult != null) {
            Integer resultCode = checkResult.getInteger("label");

            if (resultCode == 100) {
                WxCheckResultVo wxCheckResultVo = new WxCheckResultVo();
                wxCheckResultVo.setResult(true);
                return ServiceR.ok(wxCheckResultVo);
            } else {
                WxCheckResultVo wxCheckResultVo = new WxCheckResultVo();
                wxCheckResultVo.setResult(false);
                StringBuffer msg = new StringBuffer("内容非法:");
                if (resultCode == 10001) {
                    msg.append("广告");
                } else if (resultCode == 20001) {
                   msg.append("时政");
                } else if (resultCode == 20002) {
                   msg.append("色情");
                } else if (resultCode == 20003) {
                   msg.append("辱骂");
                } else if (resultCode == 20006) {
                   msg.append("违法犯罪");
                } else if (resultCode == 20008) {
                   msg.append("欺诈");
                } else if (resultCode == 20012) {
                   msg.append("低俗");
                } else if (resultCode == 20013) {
                   msg.append("版权");
                } else if (resultCode == 21000) {
                   msg.append("其他");
                }
                wxCheckResultVo.setMsg(msg.toString());

                return ServiceR.ok(wxCheckResultVo);
            }

        }
        return ServiceR.fail("内容检测失败");
    }
}
