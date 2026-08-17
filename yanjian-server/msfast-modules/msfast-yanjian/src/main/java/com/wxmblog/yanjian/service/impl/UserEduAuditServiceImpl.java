package com.wxmblog.yanjian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.user.EduRequest;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.RegisterDistributionService;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.UserEduAuditDao;
import com.wxmblog.yanjian.entity.UserEduAuditEntity;
import com.wxmblog.yanjian.service.UserEduAuditService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service("userEduAuditService")
public class UserEduAuditServiceImpl extends ServiceImpl<UserEduAuditDao, UserEduAuditEntity> implements UserEduAuditService {

    private static final String URL = "https://www.apimy.cn/api/xxw/bgcx";

    @Autowired
    public RestTemplate restTemplate;

    @Value("${edu.key}")
    private String eduKey;

    @Autowired
    private UserProfileService userProfileService;

    @Transactional
    @Override
    public R<EduRequest> checkEdu(String onlineVerificationCode) {

        String ownerId = TokenUtils.getOwnerId();
        if (StringUtils.isNotBlank(ownerId)) {
            TUserService tUserService = SpringUtils.getBean(TUserService.class);
            TUserEntity tUserEntity = tUserService.getById(ownerId);
            if (tUserEntity != null) {
                if (!"3".equals(tUserEntity.getIdAuth())) {
                    throw new JrsfException(UserExceptionEnum.NOT_ID_AUTH_EXCEPTION);
                }
            }
        }
        String result = restTemplate.getForObject(URL + "?key=" + eduKey + "&vcode=" + onlineVerificationCode, String.class);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String errcode = jsonObject.getString("code");
        if (!"200".equals(errcode)) {
            throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg(jsonObject.getString("msg"));
        }

        EduRequest eduRequest = new EduRequest();
        JSONObject data = jsonObject.getJSONObject("data");
        eduRequest.setSchool(data.getString("学校名称"));
        eduRequest.setEducation(StringUtils.isNotBlank(data.getString("层次")) ? data.getString("层次").replace("研究生", "") : "未知");
        String name = data.getString("姓名");
        if (StringUtils.isNotBlank(ownerId)) {
            TUserService tUserService = SpringUtils.getBean(TUserService.class);
            TUserEntity tUserEntity = tUserService.getById(ownerId);
            if (tUserEntity != null) {
                if (!"3".equals(tUserEntity.getIdAuth())) {
                    throw new JrsfException(UserExceptionEnum.NOT_ID_AUTH_EXCEPTION);
                }
                tUserEntity.setSchool(eduRequest.getSchool());
                tUserEntity.setEducation(eduRequest.getEducation());
                UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());

                if (StringUtils.isNotBlank(name) && !name.equals(userProfileEntity.getName())) {
                    throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg("学历姓名和实名信息不一致");
                }
                tUserEntity.setEduAuth("3");
                UserEduAuditEntity userEduAuditEntity = new UserEduAuditEntity();
                userEduAuditEntity.setUserId(tUserEntity.getId());
                userEduAuditEntity.setStatus("3");
                BeanUtils.copyProperties(eduRequest, userEduAuditEntity);
                this.save(userEduAuditEntity);
                tUserService.updateById(tUserEntity);

                //发放奖励
                RegisterDistributionService registerDistributionService = SpringUtils.getBean(RegisterDistributionService.class);
                registerDistributionService.sendAward(tUserEntity.getId());
            }
        }
        return R.ok(eduRequest);
    }
}
