package com.wxmblog.yanjian.authority;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.authority.service.IAuthorityServiceImpl;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.auth.common.enums.LoginType;
import com.wxmblog.base.auth.common.rest.request.LoginRequest;
import com.wxmblog.base.auth.service.impl.MsfConfigServiceImpl;
import com.wxmblog.base.common.entity.LoginUser;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.enums.BaseUserTypeEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.service.BaseCommonService;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.NumberUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.file.service.MsfFileService;
import com.wxmblog.base.role.common.rest.response.LoginResponse;
import com.wxmblog.yanjian.common.rest.request.front.user.EduRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.IdCardRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.UserRegisterRequest;
import com.wxmblog.yanjian.common.rest.vo.DetectFaceResultVo;
import com.wxmblog.yanjian.common.rest.vo.EidTokenResulltVo;
import com.wxmblog.yanjian.common.rest.vo.FaceCompareVo;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import com.wxmblog.yanjian.common.utils.FaceUtils;
import com.wxmblog.yanjian.common.utils.LocationAnalysisUtils;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: wxm-fast
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-16 18:05
 **/
@Service
@Slf4j
public class AuthorityServiceImpl extends IAuthorityServiceImpl<LoginRequest, UserRegisterRequest> {

    @Autowired
    MsfFileService fileService;

    @Autowired
    WxAppletService wxAppletService;

    @Autowired
    TUserService tUserService;

    @Autowired
    BaseCommonService baseCommonService;

    @Autowired
    private RedisService redisService;

    @Autowired
    UserIdcardAuditService userIdcardAuditService;

    @Autowired
    UserEduAuditService userEduAuditService;

    @Autowired
    RegisterDistributionService registerDistributionService;

    @Autowired
    DistributionPersonService distributionPersonService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserAccountService userAccountService;


    @Autowired
    public RestTemplate restTemplate;

    private static final String URL = "https://www.apimy.cn/api/xxw/bgcx";

    @Value("${edu.key}")
    private String eduKey;

    @Value("${tencent.secretId}")
    private String secretId;

    @Value("${tencent.secretKey}")
    private String secretKey;
    @Autowired
    private MsfConfigServiceImpl msfConfigService;

    @Autowired
    private WechatPublicBindService wechatPublicBindService;

    @Autowired
    private AsyncService asyncService;

    @Value("${wxmfast.config.auth.dev-mode:false}")
    private boolean devMode;

    public AuthorityServiceImpl() {
        this.setLoginType(LoginType.WX_Applet);
    }

    @Transactional
    @Override
    public LoginUser login(LoginRequest loginRequest) {

        LoginUser loginUser = new LoginUser();
        TUserEntity frUserEntity = this.tUserService.getFrUserByOpenId(loginRequest.getOpenId());

        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if (FrUserStatusEnum.LOGOFF.equals(frUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if (FrUserStatusEnum.DISABLE.equals(frUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_STATUS_ERROR_EXCEPTION);
        }

        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());

        if (userProfileEntity != null && StringUtils.isBlank(userProfileEntity.getUnionId())) {
            userProfileEntity.setUnionId(loginRequest.getUnionId());
            userProfileService.updateById(userProfileEntity);
        }

        loginUser.setId(frUserEntity.getId());
        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(frUserEntity, loginResponse);
        loginResponse.setUserType(BaseUserTypeEnum.TOURIST);
        loginUser.setInfo(loginResponse);
        return loginUser;
    }

    @Override
    @Transactional
    public void wxAppletRegister(UserRegisterRequest request) {

        TUserEntity frUserEntity = new TUserEntity();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        BeanUtils.copyProperties(request, userProfileEntity);
        BeanUtils.copyProperties(request, frUserEntity);
        if (StringUtils.isNotBlank(frUserEntity.getCounty()) && StringUtils.isNotBlank(frUserEntity.getCity())) {
            frUserEntity.setMainCity(LocationAnalysisUtils.isMainCity(frUserEntity.getCity(), frUserEntity.getCounty()));
        }

        TUserEntity oldUser = this.tUserService.getFrUserByOpenId(request.getOpenId());

        if (oldUser != null && FrUserStatusEnum.DISABLE.equals(oldUser.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_STATUS_ERROR_EXCEPTION);
        }

        if (oldUser != null && FrUserStatusEnum.ENABLE.equals(oldUser.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_EXIST_EXCEPTION);
        }

        if (request.getNickName().contains("颜见")) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("昵称请勿使用颜见");
        }
        frUserEntity.setPhotoAuth("1");
        frUserEntity.setIdAuth("1");
        frUserEntity.setLatelyTime(new Date());
        frUserEntity.setSimilarity(0f);
        frUserEntity.setRecommend("0");
        userProfileEntity.setDistributionCode(NumberUtils.getShortUUID());
        List<PhotoResultVo> personalPhotoList = new LinkedList<>();
        if (!devMode && CollectionUtil.isNotEmpty(request.getPersonalPhoto())) {
            for (String url : request.getPersonalPhoto()) {
                PhotoResultVo photoResultVo = new PhotoResultVo();
                photoResultVo.setUrl(url);
                photoResultVo.setSimilarity(0f);
                photoResultVo.setIsMatch(false);
                personalPhotoList.add(photoResultVo);
            }

            //头像
            String avatar = request.getPersonalPhoto().get(0);
            FaceCompareVo faceCompareVo = new FaceCompareVo();
            faceCompareVo.setUrlA(avatar);
            ServiceR<DetectFaceResultVo> detectFaceResultVoServiceR = FaceUtils.detectFace(faceCompareVo, 1);
            if (ServiceR.isError(detectFaceResultVoServiceR)) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(detectFaceResultVoServiceR.getMsg());
            }
            DetectFaceResultVo detectFaceResultVo = detectFaceResultVoServiceR.getData();
            if (Boolean.TRUE.equals(detectFaceResultVo.getIsExistFace()) && detectFaceResultVo.getFaceCount() == 1) {
                frUserEntity.setPhotoAuth("2");
                if (StringUtils.isNotBlank(detectFaceResultVo.getGender())
                        && !detectFaceResultVo.getGender().equals(frUserEntity.getSex())) {
                    //选择的性别错误 修改性别
                    frUserEntity.setSex(detectFaceResultVo.getGender());
                    userProfileEntity.setWarningMsg("封面相册性别不一致");
                }
                if (detectFaceResultVo.getQuality() != null && detectFaceResultVo.getQuality() < 70) {
                    frUserEntity.setPhotoAuth("6");
                    userProfileEntity.setWarningMsg("封面照片不清晰，请重新上传");
                }
            }

        }
        frUserEntity.setPersonalPhoto(personalPhotoList);
        //身份认证校验
        IdCardRequest idCardRequest = null;
        if (StringUtils.isNotBlank(request.getEidToken())) {
            ServiceR<EidTokenResulltVo> eidTokenResulltVoServiceR = FaceUtils.getEidResult(request.getEidToken());
            if (ServiceR.isError(eidTokenResulltVoServiceR)) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(eidTokenResulltVoServiceR.getMsg());
            }
            frUserEntity.setIdAuth("3");
            EidTokenResulltVo eidTokenResulltVo = eidTokenResulltVoServiceR.getData();
            idCardRequest = new IdCardRequest();
            idCardRequest.setName(eidTokenResulltVo.getName());
            idCardRequest.setIdCard(eidTokenResulltVo.getIdCard());

            log.info("查询是否注册过：{} {}", idCardRequest.getIdCard(), idCardRequest.getName());
            if (StringUtils.isNotBlank(eidTokenResulltVo.getIdCard())) {
                Wrapper<UserProfileEntity> wrapper = new QueryWrapper<UserProfileEntity>().lambda().eq(UserProfileEntity::getIdCard, idCardRequest.getIdCard()).orderByDesc(UserProfileEntity::getCreateTime).last("limit 1");
                UserProfileEntity userProfile = userProfileService.getOne(wrapper);
                if (userProfile != null) {
                    Wrapper<TUserEntity> userWrapper = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getProfileId, userProfile.getId()).orderByDesc(TUserEntity::getCreateTime).last("limit 1");
                    TUserEntity userEntity = tUserService.getOne(userWrapper);
                    if (userEntity != null) {
                        log.info("已经注册");
                        if (FrUserStatusEnum.DISABLE.equals(userEntity.getStatus())) {
                            throw new JrsfException(BaseUserExceptionEnum.USER_STATUS_ERROR_EXCEPTION);
                        } else if (FrUserStatusEnum.ENABLE.equals(userEntity.getStatus())) {
                            throw new JrsfException(BaseUserExceptionEnum.USER_EXIST_EXCEPTION);
                        }
                    } else {
                        log.info("未注册");
                    }
                }
            }
            if (eidTokenResulltVo.getBirthDate() != null) {
                frUserEntity.setBirthDate(eidTokenResulltVo.getBirthDate());
            }
            if (StringUtils.isNotBlank(eidTokenResulltVo.getSex())) {
                frUserEntity.setSex(eidTokenResulltVo.getSex());
            }
            String bestFrame = eidTokenResulltVo.getBestFrame();
            // 存储最佳帧
            userProfileEntity.setImgBase64(bestFrame);
            for (int i = 0; i < personalPhotoList.size(); i++) {

                FaceCompareVo faceCompareVo = new FaceCompareVo();
                faceCompareVo.setImageA(bestFrame);
                faceCompareVo.setUrlB(personalPhotoList.get(i).getUrl());
                ServiceR<Float> compareFaceR = FaceUtils.detectFaceSimilarity(faceCompareVo, 1);
                if (ServiceR.isError(compareFaceR)) {
                    throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(compareFaceR.getMsg());
                }
                // 输出json格式的字符串回包
                log.info("头像人脸对比第{}张:{}", i + 1, JSON.toJSONString(compareFaceR));
                float score = compareFaceR.getData();

                if (i == 0) {
                    if (score >= 70 && !"6".equals(frUserEntity.getPhotoAuth())) {
                        frUserEntity.setPhotoAuth("3");
                    }
                    frUserEntity.setSimilarity(score);
                }

                personalPhotoList.get(i).setSimilarity(score);
                personalPhotoList.get(i).setIsMatch(true);
            }
        }

        frUserEntity.setStatus(FrUserStatusEnum.ENABLE);
        userProfileEntity.setHasInvalidInfo(Boolean.FALSE);
        frUserEntity.setEduAuth("1");
        frUserEntity.setInvisible("2");
        frUserEntity.setUserType("1");
        userProfileEntity.setFakeWechat(NumberUtils.replaceWithRandom(userProfileEntity.getWechat()));
        userProfileEntity.setApplyCount(0);
        if (!devMode && CollectionUtil.isNotEmpty(request.getPersonalPhoto())) {
            frUserEntity.setAvatar(request.getPersonalPhoto().get(0));
        }
        if (devMode) {
            frUserEntity.setAvatar("local-dev-avatar");
        }
        if (StringUtils.isNotBlank(userProfileEntity.getUnionId())) {
            Wrapper<WechatPublicBindEntity> wrapper = new QueryWrapper<WechatPublicBindEntity>().lambda()
                    .eq(WechatPublicBindEntity::getUnionId, userProfileEntity.getUnionId())
                    .orderByDesc(WechatPublicBindEntity::getCreateTime)
                    .last("limit 1");
            WechatPublicBindEntity wechatPublicBindEntity = wechatPublicBindService.getOne(wrapper);
            if (wechatPublicBindEntity != null) {
                userProfileEntity.setPublicOpenId(wechatPublicBindEntity.getOpenId());
            }
        }

        userProfileService.save(userProfileEntity);
        frUserEntity.setProfileId(userProfileEntity.getId());
        this.tUserService.save(frUserEntity);

        if (idCardRequest != null) {
            userProfileEntity.setIdCard(idCardRequest.getIdCard());
            userProfileEntity.setName(idCardRequest.getName());
            frUserEntity.setIdAuth("3");
            UserIdcardAuditEntity userIdcardAuditEntity = new UserIdcardAuditEntity();
            userIdcardAuditEntity.setUserId(frUserEntity.getId());
            userIdcardAuditEntity.setStatus("3");
            BeanUtils.copyProperties(idCardRequest, userIdcardAuditEntity);
            userIdcardAuditService.save(userIdcardAuditEntity);
            this.tUserService.updateById(frUserEntity);
            userProfileService.updateById(userProfileEntity);
        }

        if (StringUtils.isNotBlank(request.getEduCode())) {

            String result = restTemplate.getForObject(URL + "?key=" + eduKey + "&vcode=" + request.getEduCode(), String.class);
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
            if (StringUtils.isNotBlank(name) && !name.equals(userProfileEntity.getName())) {
                throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg("学历姓名和实名信息不一致");
            }
            frUserEntity.setSchool(eduRequest.getSchool());
            frUserEntity.setEducation(eduRequest.getEducation());
            frUserEntity.setEduAuth("3");
            UserEduAuditEntity userEduAuditEntity = new UserEduAuditEntity();
            userEduAuditEntity.setUserId(frUserEntity.getId());
            userEduAuditEntity.setStatus("3");
            BeanUtils.copyProperties(eduRequest, userEduAuditEntity);
            userEduAuditService.save(userEduAuditEntity);
            this.tUserService.updateById(frUserEntity);

        } else if (request.getEduRequest() != null) {
            frUserEntity.setSchool(request.getEduRequest().getSchool());
            frUserEntity.setEducation(request.getEduRequest().getEducation());
            frUserEntity.setEduAuth("2");
            UserEduAuditEntity userEduAuditEntity = new UserEduAuditEntity();
            userEduAuditEntity.setUserId(frUserEntity.getId());
            userEduAuditEntity.setStatus("2");
            BeanUtils.copyProperties(request.getEduRequest(), userEduAuditEntity);
            userEduAuditEntity.setEvidence(request.getEduRequest().getSchoolPhoto());
            userEduAuditService.save(userEduAuditEntity);
            this.tUserService.updateById(frUserEntity);
        }

        if (StringUtils.isNotBlank(request.getRegistrationNo())) {

            Wrapper<UserProfileEntity> wrapper = new QueryWrapper<UserProfileEntity>().lambda().eq(UserProfileEntity::getDistributionCode, request.getRegistrationNo()).last("limit 1");
            UserProfileEntity userProfileEntityDistribution = userProfileService.getBaseMapper().selectOne(wrapper);
            if (userProfileEntityDistribution != null) {
                Wrapper<TUserEntity> wrapperUser = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getProfileId, userProfileEntityDistribution.getId()).orderByDesc(TUserEntity::getCreateTime).last("limit 1");
                TUserEntity distributionUserEntity = tUserService.getOne(wrapperUser);
                if (distributionUserEntity != null) {
                    RegisterDistributionEntity registerDistributionEntity = new RegisterDistributionEntity();
                    registerDistributionEntity.setDistributionPersonId(distributionUserEntity.getId());
                    registerDistributionEntity.setUserId(frUserEntity.getId());
                    registerDistributionEntity.setType("2");
                    registerDistributionService.save(registerDistributionEntity);
                } else if (StringUtils.isNotBlank(request.getInviterId())) {

                    RegisterDistributionEntity registerDistributionEntity = new RegisterDistributionEntity();
                    registerDistributionEntity.setDistributionPersonId(request.getInviterId());
                    registerDistributionEntity.setUserId(frUserEntity.getId());
                    registerDistributionEntity.setType("2");
                    registerDistributionService.save(registerDistributionEntity);
                }
            }


        } else if (StringUtils.isNotBlank(request.getInviterId())) {

            RegisterDistributionEntity registerDistributionEntity = new RegisterDistributionEntity();
            registerDistributionEntity.setDistributionPersonId(request.getInviterId());
            registerDistributionEntity.setUserId(frUserEntity.getId());
            registerDistributionEntity.setType("2");
            registerDistributionService.save(registerDistributionEntity);
        }


        //保存图片
        if (!devMode && CollectionUtil.isNotEmpty(request.getPersonalPhoto())) {
            fileService.changeTempListFile(request.getPersonalPhoto());
        }

        if (StringUtils.isNotBlank(request.getCameraImg())) {
            fileService.changeTempFile(request.getCameraImg());
        }

        if (request.getEduRequest() != null && CollectionUtil.isNotEmpty(request.getEduRequest().getSchoolPhoto())) {
            fileService.changeTempListFile(request.getEduRequest().getSchoolPhoto());
        }


        /*if (oldUser == null) {
            String applyAmount = msfConfigService.getValueByCode("applyAmount");
            BigDecimal price = new BigDecimal(applyAmount);
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(frUserEntity.getId());
            userAccountEntity.setAmount(price);
            userAccountEntity.setSource("TUserEntity");
            userAccountEntity.setSourceId(frUserEntity.getId());
            userAccountEntity.setRemarks("新用户奖励");
            userAccountService.save(userAccountEntity);
        }*/
        if ("3".equals(frUserEntity.getIdAuth())) {
            Integer age = DateUtils.getAgeByBirth(frUserEntity.getBirthDate());
            if (age < 18) {
                frUserEntity.setNatureSort(7);
            } else if (age <= 21) {
                frUserEntity.setNatureSort(6);
            } else if (age <= 25) {
                frUserEntity.setNatureSort(5);
            } else if (age <= 29) {
                frUserEntity.setNatureSort(4);
            } else if (age <= 33) {
                frUserEntity.setNatureSort(3);
            } else if (age <= 37) {
                frUserEntity.setNatureSort(2);
            } else if (age <= 41) {
                frUserEntity.setNatureSort(1);
            } else {
                frUserEntity.setNatureSort(0);
            }
            tUserService.updateById(frUserEntity);
        }
        //注册发送客服通知
        asyncService.addMessageList(frUserEntity.getId());
    }
}
