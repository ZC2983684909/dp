package com.wxmblog.yanjian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.constant.PropertiesConstants;
import com.wxmblog.yanjian.common.rest.request.front.area.UnlimitedQRequest;
import com.wxmblog.yanjian.common.rest.response.front.home.UnlimitedQResponse;
import com.wxmblog.yanjian.common.rest.response.front.home.WechatSceneResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.WechatSceneDao;
import com.wxmblog.yanjian.entity.WechatSceneEntity;
import com.wxmblog.yanjian.service.WechatSceneService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service("wechatSceneService")
public class WechatSceneServiceImpl extends ServiceImpl<WechatSceneDao, WechatSceneEntity> implements WechatSceneService {

    @Autowired
    private WxAppletService wxAppletService;

    @Resource
    private RestTemplate restTemplate;

    @Transactional
    @Override
    public ServiceR<UnlimitedQResponse> unlimitedQrCode(UnlimitedQRequest request) {

        String qrHost = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + wxAppletService.getAccessToken();
        Map<String, Object> param = new HashMap<>();

        WechatSceneEntity sceneEntity = new WechatSceneEntity();
        sceneEntity.setContent(request.getScene());
        save(sceneEntity);

        param.put("scene", sceneEntity.getId());
        if (StringUtils.isNotBlank(request.getPage())) {
            param.put("page", request.getPage());
        }
        param.put("env_version", PropertiesConstants.qrCodeEnvVersion());
        if (request.getWidth() != null) {
            param.put("width", request.getWidth());
        }
        if (request.getAutoColor() != null) {
            param.put("auto_color", request.getAutoColor());
        }
        if (request.getLineColor() != null) {
            param.put("line_color", request.getLineColor());
        }
        if (request.getCheckPath() != null) {
            param.put("check_path", request.getCheckPath());
        }
        if (request.getIsHyaline() != null) {
            param.put("is_hyaline", request.getIsHyaline());
        }

        byte[] qrCodeResult = restTemplate.postForObject(qrHost, param, byte[].class);
        if (qrCodeResult == null || qrCodeResult.length < 200) {
            assert qrCodeResult != null;
            String str = new String(qrCodeResult, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONObject.parseObject(str);
            return ServiceR.fail(jsonObject.getString("errmsg"));
        }
        UnlimitedQResponse response = new UnlimitedQResponse();
        response.setBuffer("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(qrCodeResult));
        return ServiceR.ok(response);
    }

    @Override
    public ServiceR<WechatSceneResponse> getScene(String id) {
        if (StringUtils.isNotBlank(id)) {
            WechatSceneEntity sceneEntity = getById(id);
            if (sceneEntity != null) {
                WechatSceneResponse response = new WechatSceneResponse();
                response.setContent(sceneEntity.getContent());
                response.setId(sceneEntity.getId());
                return ServiceR.ok(response);
            }
        }
        return ServiceR.fail("参数不存在");
    }
}
