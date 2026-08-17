package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.auth.authority.service.Wxh5Service;
import com.wxmblog.base.auth.common.rest.response.WxAppletOpenResponse;
import com.wxmblog.base.auth.common.rest.response.WxH5UserInfoResponse;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.annotation.RedissonLock;
import com.wxmblog.base.common.constant.SecurityConstants;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.rest.response.BaseUserInfo;
import com.wxmblog.base.common.service.BaseCommonService;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.*;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.file.constant.FileConstants;
import com.wxmblog.base.file.service.MsfFileService;
import com.wxmblog.base.websocket.service.MsFastMessageService;
import com.wxmblog.yanjian.common.constant.Constants;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.user.PhotoEditTypeEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.admin.statistic.UserRegisterStatisticRequest;
import com.wxmblog.yanjian.common.rest.request.admin.user.*;
import com.wxmblog.yanjian.common.rest.request.front.auth.IdAuthRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.*;
import com.wxmblog.yanjian.common.rest.response.admin.statistic.OutlineResponse;
import com.wxmblog.yanjian.common.rest.response.admin.statistic.ProportionResponse;
import com.wxmblog.yanjian.common.rest.response.admin.user.*;
import com.wxmblog.yanjian.common.rest.response.front.article.ArticlePreVo;
import com.wxmblog.yanjian.common.rest.response.front.article.UserArticleViewResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.*;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.common.rest.vo.*;
import com.wxmblog.yanjian.common.utils.FaceUtils;
import com.wxmblog.yanjian.common.utils.LocationAnalysisUtils;
import com.wxmblog.yanjian.dao.RegisterDistributionDao;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.TUserDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service("tUserService")
@Slf4j
public class TUserServiceImpl extends ServiceImpl<TUserDao, TUserEntity> implements TUserService {

    @Autowired
    private UserEduAuditService userEduAuditService;

    @Autowired
    private UserIdcardAuditService userIdcardAuditService;

    @Autowired
    private UserApplyService userApplyService;

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserVisitService userVisitService;

    @Autowired
    MsfFileService msfFileService;

    @Autowired
    RedissonClient redissonClient;

    @Resource
    private RedisService redisService;

    @Autowired
    private UserShieldService userShieldService;

    @Autowired
    private MsfConfigService msfConfigService;

    @Autowired
    private GiftPontService giftPontService;

    @Value("${tencent.secretId}")
    private String secretId;

    @Value("${tencent.secretKey}")
    private String secretKey;

    @Value("${tencent.merchantId}")
    private String merchantId;

    @Autowired
    private ComplaintServiceImpl complaintService;

    @Resource
    RestTemplate restTemplate;

    @Autowired
    private WxAppletService wxAppletService;

    @Autowired
    private RegisterDistributionService registerDistributionService;

    @Autowired
    private WithdrawalRecordService withdrawalRecordService;

    @Autowired
    private RegisterDistributionDao registerDistributionDao;

    @Autowired
    private ArticleCommentServiceImpl articleCommentService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserVipService userVipService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private UserChatService userChatService;

    @Autowired
    private BaseCommonService baseCommonService;

    @Autowired
    private Wxh5Service wxh5Service;


    @Override
    public TUserEntity getFrUserByOpenId(String openId) {
        Wrapper<TUserEntity> frUserEntityWrapper = new QueryWrapper<TUserEntity>().lambda()
                .eq(TUserEntity::getOpenId, openId)
                .orderByDesc(TUserEntity::getCreateTime)
                .last("limit 1");
        return getOne(frUserEntityWrapper);
    }

    @Override
    public TUserEntity getFrUserByUnionId(String unionId) {
        Wrapper<UserProfileEntity> userProfileEntityWrapper = new QueryWrapper<UserProfileEntity>().lambda()
                .eq(UserProfileEntity::getUnionId, unionId);
        List<UserProfileEntity> userProfileEntitys = userProfileService.list(userProfileEntityWrapper);
        if (CollectionUtil.isNotEmpty(userProfileEntitys)) {
            List<String> profileIds = userProfileEntitys.stream().map(UserProfileEntity::getId).collect(Collectors.toList());
            Wrapper<TUserEntity> frUserEntityWrapper = new QueryWrapper<TUserEntity>().lambda()
                    .in(TUserEntity::getProfileId, profileIds)
                    .orderByDesc(TUserEntity::getCreateTime).last("limit 1");
            return getOne(frUserEntityWrapper);
        }
        return null;
    }

    @Override
    public TUserEntity getFrUserByIdCard(String idCardNumber) {
        Wrapper<UserProfileEntity> userProfileEntityWrapper = new QueryWrapper<UserProfileEntity>().lambda().eq(UserProfileEntity::getIdCard, idCardNumber).orderByDesc(UserProfileEntity::getCreateTime).last("limit 1");
        UserProfileEntity userProfileEntity = userProfileService.getOne(userProfileEntityWrapper);
        if (userProfileEntity != null) {
            Wrapper<TUserEntity> frUserEntityWrapper = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getProfileId, userProfileEntity.getId()).orderByDesc(TUserEntity::getCreateTime).last("limit 1");
            return getOne(frUserEntityWrapper);
        }
        return null;
    }

    @Override
    public PersonalCenterResponse getPersonalCenter() {


        TUserEntity tUserEntity = getById(TokenUtils.getOwnerId());
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        PersonalCenterResponse personalCenterResponse = this.getBaseMapper().getUserNumber(TokenUtils.getOwnerId());
        BeanUtils.copyProperties(tUserEntity, personalCenterResponse);

        Wrapper<ArticleEntity> articleEntityWrapper = new QueryWrapper<ArticleEntity>().lambda()
                .eq(ArticleEntity::getUserId, TokenUtils.getOwnerId())
                .orderByDesc(ArticleEntity::getCreateTime).last("limit 1");
        ArticleEntity articleEntity = articleService.getOne(articleEntityWrapper);
        if (articleEntity != null) {
            DynamicsBriefResponse dynamicsBriefResponse = new DynamicsBriefResponse();
            dynamicsBriefResponse.setTitle(articleEntity.getContent());
            if (CollectionUtil.isNotEmpty(articleEntity.getImg())) {
                ArticlePreVo articlePreVo = new ArticlePreVo();
                articlePreVo.setImg(articleEntity.getImg().get(0));
                articlePreVo.setType(articleEntity.getType());
                dynamicsBriefResponse.setImg(articlePreVo);
            }
            if (articleEntity.getCreateTime() != null) {
                dynamicsBriefResponse.setYear(DateUtil.format(articleEntity.getCreateTime(), "yyyy"));
                dynamicsBriefResponse.setMonth(DateUtil.format(articleEntity.getCreateTime(), "MM"));
                dynamicsBriefResponse.setDay(DateUtil.format(articleEntity.getCreateTime(), "dd"));
                String year = DateUtil.format(new Date(), "yyyy");
                if (year.equals(dynamicsBriefResponse.getYear())) {
                    dynamicsBriefResponse.setYear("");
                }
            }
            personalCenterResponse.setDynamics(dynamicsBriefResponse);
        }

        personalCenterResponse.setBalance(userAccountService.getBalance(TokenUtils.getOwnerId()));
        personalCenterResponse.setIsVip(userVipService.isVip(TokenUtils.getOwnerId()));

        List<String> warnings = new ArrayList<>();


        if (FrUserStatusEnum.VIOLATION.equals(tUserEntity.getStatus())) {
            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
            warnings.add("您的账号存在违规行为：" + userProfileEntity.getViolationMsg());
        }
        List<String> warningList = new ArrayList<>();
        for (int i = 0; i < warnings.size(); i++) {
            warningList.add((i + 1) + "." + warnings.get(i));
        }
        personalCenterResponse.setWarning(String.join("\n", warningList));

        return personalCenterResponse;
    }

    @Transactional
    @Override
    public UserInfoResponse info() {

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        TUserEntity tUserEntity = getById(TokenUtils.getOwnerId());

        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
        if (userProfileEntity != null) {
            BeanUtils.copyProperties(userProfileEntity, userInfoResponse);
        }
        BeanUtils.copyProperties(tUserEntity, userInfoResponse);
        userInfoResponse.setBirthFormatDate(tUserEntity.getBirthDate());
        if (tUserEntity.getHeight() != null) {
            userInfoResponse.setHeightFormat(tUserEntity.getHeight() + "cm");
        }
        if (tUserEntity.getWeight() != null) {
            userInfoResponse.setWeightFormat(tUserEntity.getWeight() + "kg");
        }

        if (tUserEntity.getFondTags() != null) {
            Set<String> tags = new LinkedHashSet<>();
            if (StringUtils.isNotBlank(tUserEntity.getFondTags().getMainLabel())) {
                tags.addAll(ListUtil.toList(tUserEntity.getFondTags().getMainLabel().split(",")));
            }
            if (StringUtils.isNotBlank(tUserEntity.getFondTags().getMyLabel())) {
                tags.addAll(Arrays.asList(tUserEntity.getFondTags().getMyLabel().split(",")));
            }
            userInfoResponse.setFondTagsFormat(StringUtils.join(tags, ","));

            String json = msfConfigService.getValueByCode("label_json");
            if (StringUtils.isNotBlank(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
                List<LabelInfoResponse> list = jsonObject.getJSONArray("label").toJavaList(LabelInfoResponse.class);
                List<LabelInfoResponse> labelList = new ArrayList<>();
                for (String tag : tags) {
                    for (LabelInfoResponse labelInfoResponse : list) {
                        for (LabelInfoResponse child : labelInfoResponse.getChilds()) {
                            if (child.getName().equals(tag)) {
                                LabelInfoResponse labelInfoResponse1 = new LabelInfoResponse();
                                labelInfoResponse1.setName(child.getName());
                                labelInfoResponse1.setImgUrl(child.getImgUrl());
                                labelList.add(labelInfoResponse1);
                                break;
                            }
                        }
                    }
                }
                userInfoResponse.setFondTagsList(labelList);
            }


        }

        if (userProfileEntity != null && StringUtils.isBlank(userProfileEntity.getDistributionCode())) {
            userProfileEntity.setDistributionCode(NumberUtils.getShortUUID());
            userProfileService.updateById(userProfileEntity);
            userInfoResponse.setDistributionCode(userProfileEntity.getDistributionCode());
        }

        if ("1".equals(tUserEntity.getPhotoAuth())) {
            userInfoResponse.setWarning("封面没有检测到人脸");
        } else if ("2".equals(tUserEntity.getPhotoAuth())) {
            if ("3".equals(tUserEntity.getIdAuth())) {
                userInfoResponse.setWarning("封面相似度过低，请上传本人真实照片");
            } else {
                userInfoResponse.setWarning("封面无法检测，请先完成实名认证");
            }
        } else if ("4".equals(tUserEntity.getPhotoAuth()) || "5".equals(tUserEntity.getPhotoAuth())) {
            userInfoResponse.setWarning("封面检测失败，请上传其他本人照片");
        } else if ("6".equals(tUserEntity.getPhotoAuth())) {
            userInfoResponse.setWarning(userProfileEntity.getWarningMsg());
        } else if (!"3".equals(tUserEntity.getIdAuth())) {
            userInfoResponse.setWarning("未实名认证，建议完成实名认证");
        }

        return userInfoResponse;
    }

    @Transactional
    @Override
    public void editUserInfo(UserInfoEditRequest request) {

        String userId = TokenUtils.getOwnerId();
        TUserEntity tUserEntity = getById(userId);
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
        List<PhotoResultVo> personalPhotoList = null;
        if (CollectionUtil.isNotEmpty(request.getPersonalPhoto())) {
            personalPhotoList = new LinkedList<>();
            for (String url : request.getPersonalPhoto()) {
                PhotoResultVo photoResultVo = new PhotoResultVo();
                photoResultVo.setUrl(url);
                photoResultVo.setSimilarity(0f);
                photoResultVo.setIsMatch(false);
                if (CollectionUtil.isNotEmpty(tUserEntity.getPersonalPhoto())) {
                    tUserEntity.getPersonalPhoto().stream().filter(p -> StringUtils.isNotBlank(p.getUrl()) && p.getUrl().equals(url) && Boolean.TRUE.equals(p.getIsMatch())).findFirst().ifPresent(p -> {
                        photoResultVo.setSimilarity(p.getSimilarity());
                        photoResultVo.setIsMatch(true);
                    });
                }
                personalPhotoList.add(photoResultVo);
            }
            //头像
            String avatar = request.getPersonalPhoto().get(0);
            if (!avatar.equals(tUserEntity.getAvatar())) {
                FaceCompareVo faceCompareVo = new FaceCompareVo();
                faceCompareVo.setUrlA(avatar);
                ServiceR<DetectFaceResultVo> detectFaceResultVoServiceR = FaceUtils.detectFace(faceCompareVo, 1);
                if (ServiceR.isError(detectFaceResultVoServiceR)) {
                    throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(detectFaceResultVoServiceR.getMsg());
                }
                DetectFaceResultVo detectFaceResultVo = detectFaceResultVoServiceR.getData();
                if (Boolean.TRUE.equals(detectFaceResultVo.getIsExistFace()) && detectFaceResultVo.getFaceCount() == 1) {
                    tUserEntity.setPhotoAuth("2");
                    if (StringUtils.isNotBlank(detectFaceResultVo.getGender())
                            && !detectFaceResultVo.getGender().equals(tUserEntity.getSex())) {
                        tUserEntity.setPhotoAuth("6");
                        userProfileEntity.setWarningMsg("封面相册性别不一致");
                    }
                    if (detectFaceResultVo.getQuality() != null && detectFaceResultVo.getQuality() < 70) {
                        tUserEntity.setPhotoAuth("6");
                        userProfileEntity.setWarningMsg("封面照片不清晰，请重新上传");
                    }

                } else {
                    tUserEntity.setPhotoAuth("1");
                }
                tUserEntity.setAvatar(avatar);
            }

        }
        if (StringUtils.isNotBlank(userProfileEntity.getImgBase64()) && CollectionUtil.isNotEmpty(personalPhotoList)) {
            String bestFrame = userProfileEntity.getImgBase64();
            for (int i = 0; i < personalPhotoList.size(); i++) {
                if (!Boolean.TRUE.equals(personalPhotoList.get(i).getIsMatch())) {
                    FaceCompareVo faceCompareVo = new FaceCompareVo();
                    faceCompareVo.setImageA(bestFrame);
                    faceCompareVo.setUrlB(personalPhotoList.get(i).getUrl());
                    ServiceR<Float> compareFaceR = FaceUtils.detectFaceSimilarity(faceCompareVo, 1);
                    if (ServiceR.isError(compareFaceR)) {
                        throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(compareFaceR.getMsg());
                    }
                    // 输出json格式的字符串回包
                    log.info("相册人脸对比第{}张:{}", i + 1, JSON.toJSONString(compareFaceR));
                    float score = compareFaceR.getData();
                    personalPhotoList.get(i).setSimilarity(score);
                    personalPhotoList.get(i).setIsMatch(true);
                }
                if (i == 0) {
                    float score = personalPhotoList.get(i).getSimilarity();
                    if (score >= 70) {
                        tUserEntity.setPhotoAuth("3");
                    }
                    tUserEntity.setSimilarity(score);
                }

            }
        }

        if (StringUtils.isNotBlank(request.getSelfDescription())) {
            String banWord = msfConfigService.getValueByCode("banWord");
            if (StringUtils.isNotBlank(banWord)) {
                String[] banWordList = banWord.split(",");
                for (String banWordModel : banWordList) {
                    if (request.getSelfDescription().contains(banWordModel)) {
                        throw new JrsfException(UserExceptionEnum.CONTENT_CONTAIN_ILLEGAL_CHARACTER).setMsg("内容涉嫌敏感词汇:" + banWordModel + ",我们鼓励大家积极分享自己的状态，美好生活，日常等内容，请不要发布涉嫌广告营销或是其他非法内容，谢谢！");
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(request.getNickName()) && request.getNickName().contains("颜见")) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("昵称请勿使用颜见");
        }

        BeanUtils.copyProperties(request, tUserEntity);
        userProfileEntity.setFakeWechat(NumberUtils.replaceWithRandom(request.getWechat()));
        tUserEntity.setPersonalPhoto(personalPhotoList);
        this.getBaseMapper().updateById(tUserEntity);
        BeanUtils.copyProperties(request, userProfileEntity);
        userProfileService.updateById(userProfileEntity);

        List<BaseUserInfo> baseUserInfos = new ArrayList<>();
        BaseUserInfo baseUserInfo = new BaseUserInfo();
        baseUserInfo.setNickName(tUserEntity.getNickName());
        baseUserInfo.setHeadPortrait(tUserEntity.getAvatar());
        baseUserInfo.setSex(tUserEntity.getSex());
        baseUserInfo.setId(tUserEntity.getId());
        baseUserInfos.add(baseUserInfo);
        baseCommonService.updateUser(baseUserInfos);

    }

    @Override
    public EduApplyResponse getEduApply() {

        EduApplyResponse eduApplyResponse = new EduApplyResponse();
        Wrapper<UserEduAuditEntity> userEduAuditEntityWrapper = new QueryWrapper<UserEduAuditEntity>().lambda().eq(UserEduAuditEntity::getUserId, TokenUtils.getOwnerId()).orderByDesc(UserEduAuditEntity::getCreateTime).last("limit 1");
        UserEduAuditEntity userEduAuditEntity = userEduAuditService.getOne(userEduAuditEntityWrapper);
        if (userEduAuditEntity != null) {
            BeanUtils.copyProperties(userEduAuditEntity, eduApplyResponse);
        }
        return eduApplyResponse;
    }

    @Override
    public IdApplyResponse getIdInfo() {
        IdApplyResponse idApplyResponse = new IdApplyResponse();
        Wrapper<UserIdcardAuditEntity> userIdcardAuditEntityWrapper = new QueryWrapper<UserIdcardAuditEntity>().lambda().eq(UserIdcardAuditEntity::getUserId, TokenUtils.getOwnerId()).orderByDesc(UserIdcardAuditEntity::getCreateTime).last("limit 1");
        UserIdcardAuditEntity userIdcardAuditEntity = userIdcardAuditService.getOne(userIdcardAuditEntityWrapper);
        if (userIdcardAuditEntity != null) {
            BeanUtils.copyProperties(userIdcardAuditEntity, idApplyResponse);
        }
        return idApplyResponse;
    }

    @Override
    public AuthStatusResponse getAuthStatus() {

        AuthStatusResponse authStatusResponse = new AuthStatusResponse();
        TUserEntity tUserEntity = getById(TokenUtils.getOwnerId());
        if (tUserEntity != null) {
            authStatusResponse.setIdAuth(tUserEntity.getIdAuth());
            authStatusResponse.setEduAuth(tUserEntity.getEduAuth());
            authStatusResponse.setPhotoAuth(tUserEntity.getPhotoAuth());
        }
        return authStatusResponse;
    }

    @Override
    public PageResult<UserInfoPageResponse> getPage(UserInfoPageRequest request, Integer pageIndex, Integer pageSize) {

        UserLocationVo userLocationVo = LocationAnalysisUtils.getUserHeadLocation();
        if (userLocationVo != null) {
            request.setLon(userLocationVo.getLon());
            request.setLat(userLocationVo.getLat());
        }
        if (StringUtils.isNotBlank(request.getResidentialCity())) {
            request.setResidentialCity("%" + request.getResidentialCity() + "%");
        }

        if (StringUtils.isNotBlank(request.getTendLiveCity())) {
            request.setTendLiveCity("%" + request.getTendLiveCity() + "%");
        }

        if (StringUtils.isNotBlank(request.getHomeTown())) {
            request.setHomeTown("%" + request.getHomeTown() + "%");
        }

        if (StringUtils.isNotBlank(TokenUtils.getOwnerId())) {

            /*if (pageIndex > 1) {
                Boolean isVip = userVipService.isVip(TokenUtils.getOwnerId());
                if (!Boolean.TRUE.equals(isVip)) {
                    throw new JrsfException(UserExceptionEnum.NOT_VIP_USER_EXCEPTION);
                }
            }*/
            request.setOwnerId(TokenUtils.getOwnerId());
            TUserEntity tUserEntity = getById(TokenUtils.getOwnerId());
            if (tUserEntity == null) throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            if ("1".equals(tUserEntity.getInvisible())) {

                if (StringUtils.isNotBlank(request.getResidentialCity())
                        || request.getMinAge() != null
                        || request.getMaxAge() != null
                        || request.getMinHeight() != null
                        || request.getMaxHeight() != null
                        || StringUtils.isNotBlank(request.getTendLiveCity())
                        || StringUtils.isNotBlank(request.getOtherLabel())
                        || request.getIsStudy() != null
                        || StringUtils.isNotBlank(request.getEducation())
                        || StringUtils.isNotBlank(request.getHomeTown())) {
                    throw new JrsfException(UserExceptionEnum.USER_IS_INVISIBLE);
                }
            }
            if (StringUtils.isBlank(request.getSex())) {
                request.setSex("男".equals(tUserEntity.getSex()) ? "女" : "男");
            }

            if ("不限".equals(request.getSex())) {
                request.setSex(null);
            }


            if (StringUtils.isBlank(request.getCity())) {
                request.setCity(tUserEntity.getCity());
            }
            if (StringUtils.isBlank(request.getLon())) {
                request.setLon(tUserEntity.getLon());
            }
            if (StringUtils.isBlank(request.getLat())) {
                request.setLat(tUserEntity.getLat());
            }

            if (Boolean.TRUE.equals(request.getIsIdAuth())
                    || request.getSimilarity() != null
                    || request.getMinAge() != null
                    || request.getMaxAge() != null
                    || Boolean.TRUE.equals(request.getIsOriginalCamera())
                    || CollectionUtil.isNotEmpty(request.getLabelList())) {

                Boolean isVip = userVipService.isVip(tUserEntity.getId());
                if (!Boolean.TRUE.equals(isVip)) {
                    throw new JrsfException(UserExceptionEnum.NOT_VIP_USER_EXCEPTION);
                }
            }

            if (StringUtils.isBlank(request.getCity())) {
                request.setCity(tUserEntity.getCity());
            }

            if (StringUtils.isBlank(request.getCounty())) {
                request.setCounty(tUserEntity.getCounty());
            }
            request.setMainCity(StringUtils.isBlank(request.getCounty()) || StringUtils.isBlank(request.getCity()) || LocationAnalysisUtils.isMainCity(request.getCity(), request.getCounty()));

            if (StringUtils.isNotBlank(request.getCity()) && !request.getCity().equals(tUserEntity.getCity())) {
                request.setMainCity(true);
            }

        } else {

            if (StringUtils.isBlank(request.getSex())) {
                throw new JrsfException(UserExceptionEnum.NOT_SEX_EXCEPTION);
            }

            if (Boolean.TRUE.equals(request.getIsIdAuth())
                    || request.getSimilarity() != null
                    || request.getMinAge() != null
                    || request.getMaxAge() != null
                    || Boolean.TRUE.equals(request.getIsOriginalCamera())
                    || CollectionUtil.isNotEmpty(request.getLabelList())) {
                if (StringUtils.isBlank(SecurityUtils.getToken())) {
                    throw new JrsfException(BaseExceptionEnum.NO_LOGIN_EXCEPTION);
                } else {
                    throw new JrsfException(BaseExceptionEnum.TOKEN_EXPIRED_EXCEPTION);
                }
            }
            if (pageIndex > 1) {
                if (StringUtils.isBlank(SecurityUtils.getToken())) {
                    throw new JrsfException(BaseExceptionEnum.NO_LOGIN_EXCEPTION);
                } else {
                    throw new JrsfException(BaseExceptionEnum.TOKEN_EXPIRED_EXCEPTION);
                }
            }
            request.setMainCity(StringUtils.isBlank(request.getCounty()) || StringUtils.isBlank(request.getCity()) || LocationAnalysisUtils.isMainCity(request.getCity(), request.getCounty()));
        }

        if (StringUtils.isNotBlank(request.getOtherLabel())) {
            request.setLabelList(Arrays.asList(request.getOtherLabel().split(",")));
        }

        // 当前小程序实现中，在首页的请求中sortType为createTime，在筛选结果的请求中sortType为latelyTime
        Page<UserInfoPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().getPage(request);
        PageResult<UserInfoPageResponse> result = new PageResult<>(page);
        List<String> idWebsocketList = new ArrayList<>();
        result.getRows().forEach(userInfoPageResponse -> {
            idWebsocketList.add(com.wxmblog.base.common.constant.Constants.SOCKET_USER_ONLINE + userInfoPageResponse.getId());
        });

        List<String> onlineUserList = redisService.getMultiCacheObject(idWebsocketList);
        result.getRows().forEach(userInfoPageResponse -> {
            userInfoPageResponse.setActive(userInfoPageResponse.getLatelyTime() != null && DateUtil.betweenDay(userInfoPageResponse.getLatelyTime(), new Date(), true) < 30);
            if (CollectionUtil.isNotEmpty(userInfoPageResponse.getPersonalPhoto())) {
                userInfoPageResponse.setPhoto(userInfoPageResponse.getPersonalPhoto().get(0).getUrl());
                userInfoPageResponse.setSimilarity(userInfoPageResponse.getPersonalPhoto().get(0).getSimilarity());
            }
            userInfoPageResponse.setPersonalPhoto(null);
            if (userInfoPageResponse.getHeight() != null) {
                userInfoPageResponse.setHeightFormat(userInfoPageResponse.getHeight() + "cm");
            }

            if (StringUtils.isNotBlank(userInfoPageResponse.getHomeTown())) {
                if (userInfoPageResponse.getHomeTown().contains("重庆")) {
                    userInfoPageResponse.setHomeTown("重庆市");
                } else if (userInfoPageResponse.getHomeTown().contains("北京")) {
                    userInfoPageResponse.setHomeTown("北京市");
                } else if (userInfoPageResponse.getHomeTown().contains("天津")) {
                    userInfoPageResponse.setHomeTown("天津市");
                } else if (userInfoPageResponse.getHomeTown().contains("上海")) {
                    userInfoPageResponse.setHomeTown("上海市");
                }
            }
            if (StringUtils.isNotBlank(userInfoPageResponse.getResidentialCity())) {
                if (userInfoPageResponse.getResidentialCity().contains("重庆")) {
                    userInfoPageResponse.setResidentialCity("重庆市");
                } else if (userInfoPageResponse.getResidentialCity().contains("北京")) {
                    userInfoPageResponse.setResidentialCity("北京市");
                } else if (userInfoPageResponse.getResidentialCity().contains("天津")) {
                    userInfoPageResponse.setResidentialCity("天津市");
                } else if (userInfoPageResponse.getResidentialCity().contains("上海")) {
                    userInfoPageResponse.setResidentialCity("上海市");
                } else {
                    String[] city = userInfoPageResponse.getResidentialCity().split("省");
                    if (city.length > 1) {
                        userInfoPageResponse.setResidentialCity(userInfoPageResponse.getResidentialCity().split("省")[1]);
                    } else {
                        userInfoPageResponse.setResidentialCity(userInfoPageResponse.getResidentialCity());
                    }
                }
            }

            if (userInfoPageResponse.getCreateTime() != null) {
                userInfoPageResponse.setIsNew(DateUtil.betweenDay(userInfoPageResponse.getCreateTime(), new Date(), true) <= 7);
            }

            onlineUserList.stream().filter(onlineUser -> onlineUser != null && onlineUser.equals(userInfoPageResponse.getId())).findFirst()
                    .ifPresent(onlineUser -> userInfoPageResponse.setActiveStatus("1"));
            if (userInfoPageResponse.getLatelyTime() != null && !"1".equals(userInfoPageResponse.getActiveStatus())) {
                if (DateUtil.between(userInfoPageResponse.getLatelyTime(), new Date(), DateUnit.HOUR) <= 6) {
                    userInfoPageResponse.setActiveStatus("2");
                } else if (DateUtil.between(userInfoPageResponse.getLatelyTime(), new Date(), DateUnit.DAY) <= 7) {
                    userInfoPageResponse.setActiveStatus("3");
                } else {
                    userInfoPageResponse.setActiveStatus("4");
                }
            }
            if (userInfoPageResponse.getBirthDate() != null) {
                userInfoPageResponse.setAge(DateUtils.getAgeByBirth(userInfoPageResponse.getBirthDate()));
            }

            if (userInfoPageResponse.getDistance() != null) {
                if (userInfoPageResponse.getDistance().compareTo(new BigDecimal("500")) < 0) {
                    userInfoPageResponse.setDistanceFormat(userInfoPageResponse.getDistance().setScale(0, RoundingMode.DOWN) + "m");
                } else {
                    userInfoPageResponse.setDistanceFormat(userInfoPageResponse.getDistance().divide(new BigDecimal("1000"), 1, RoundingMode.DOWN) + "km");
                }
            }

            if (Boolean.FALSE.equals(userInfoPageResponse.getMainCity()) && StringUtils.isNotBlank(userInfoPageResponse.getCity()) && StringUtils.isNotBlank(userInfoPageResponse.getCounty())) {
                userInfoPageResponse.setCity(userInfoPageResponse.getCity() + userInfoPageResponse.getCounty());
            }

            StringBuilder cityBuilder = new StringBuilder();
            if (StringUtils.isNotBlank(userInfoPageResponse.getCity())) {
                cityBuilder.append(userInfoPageResponse.getCity());
            }
            if (StringUtils.isNotBlank(userInfoPageResponse.getDistanceFormat())) {
                cityBuilder.append(userInfoPageResponse.getDistanceFormat());
            }
            if (cityBuilder.length() > 12) {
                int cityLength = 12 - (StringUtils.isNotBlank(userInfoPageResponse.getDistanceFormat()) ? userInfoPageResponse.getDistanceFormat().length() : 0);
                if (StringUtils.isNotBlank(userInfoPageResponse.getCity()) && userInfoPageResponse.getCity().length() > cityLength) {
                    userInfoPageResponse.setCity(cityBuilder.substring(0, cityLength) + ".");
                }
            }
            if (userInfoPageResponse.getNickName().length() > 5) {
                userInfoPageResponse.setNickName(userInfoPageResponse.getNickName().substring(0, 5) + ".");
            }
        });
        return result;
    }

    @Override
    public UserDetailResponse getUserDetail(String id) {

        UserDetailResponse userDetailResponse = new UserDetailResponse();
        if (StringUtils.isNotBlank(TokenUtils.getOwnerId())) {
            Wrapper<UserShieldEntity> shieldWrapper = new QueryWrapper<UserShieldEntity>().lambda().eq(UserShieldEntity::getUserId, id).eq(UserShieldEntity::getShieldId, TokenUtils.getOwnerId());
            if (userShieldService.count(shieldWrapper) > 0) {
                throw new JrsfException(UserExceptionEnum.SHIELD_USER_EXCEPTION);
            }
        }

        DistanceRequest distanceRequest = new DistanceRequest();
        UserLocationVo userLocationVo = LocationAnalysisUtils.getUserHeadLocation();
        if (userLocationVo != null) {
            distanceRequest.setLonA(userLocationVo.getLon());
            distanceRequest.setLatA(userLocationVo.getLat());
        }
        if (StringUtils.isNotBlank(TokenUtils.getOwnerId())) {
            TUserEntity tUserEntity = getById(TokenUtils.getOwnerId());
            if (tUserEntity != null && "1".equals(tUserEntity.getInvisible())) {
                throw new JrsfException(UserExceptionEnum.USER_IS_INVISIBLE);
            }

            if (tUserEntity != null) {
                userDetailResponse.setMyIdAuth(tUserEntity.getIdAuth());
                userDetailResponse.setMyEduAuth(tUserEntity.getEduAuth());
                userDetailResponse.setMyPhotoAuth(tUserEntity.getPhotoAuth());
            }
            if (TokenUtils.getOwnerId().equals(id)) {
                userDetailResponse.setSeeCameraImg(true);
            } else {
                userDetailResponse.setSeeCameraImg(userVipService.isVip(TokenUtils.getOwnerId()));
            }
        } else {
            userDetailResponse.setSeeCameraImg(false);
        }

        //查询
        TUserEntity tUserEntity = getById(id);
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.OTHTN_USER_NOT_EXIST_EXCEPTION);
        }

        if (FrUserStatusEnum.DISABLE.equals(tUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_STATUS_ERROR_EXCEPTION);
        }

        if (FrUserStatusEnum.LOGOFF.equals(tUserEntity.getStatus())) {
            throw new JrsfException(BaseUserExceptionEnum.USER_IS_LOGOFF);
        }

        if ("1".equals(tUserEntity.getInvisible())) {
            throw new JrsfException(UserExceptionEnum.USER_IS_INVISIBLE_STATUS);
        }

        BeanUtils.copyProperties(tUserEntity, userDetailResponse);

        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
        BeanUtils.copyProperties(userProfileEntity, userDetailResponse);
        userDetailResponse.setId(tUserEntity.getId());
        distanceRequest.setLonB(tUserEntity.getLon());
        distanceRequest.setLatB(tUserEntity.getLat());
        if (CollectionUtil.isNotEmpty(tUserEntity.getPersonalPhoto())) {
            userDetailResponse.setAvatar(tUserEntity.getPersonalPhoto().get(0).getUrl().replaceAll(FileConstants.SIMPLE, ""));
            userDetailResponse.getPersonalPhoto().forEach(photo -> photo.setUrl(photo.getUrl().replaceAll(FileConstants.SIMPLE, "")));
        }
        if (!id.equals(TokenUtils.getOwnerId()) && StringUtils.isNotBlank(userProfileEntity.getWechat())) {
            //加密微信号
            userDetailResponse.setWechat(userProfileEntity.getFakeWechat());
        }
        if (tUserEntity.getCreateTime() != null) {
            userDetailResponse.setIsNew(DateUtil.betweenDay(tUserEntity.getCreateTime(), new Date(), true) <= 7);
        }

        String onlineUser = redisService.getCacheObject(com.wxmblog.base.common.constant.Constants.SOCKET_USER_ONLINE + id);
        if (StringUtils.isNotBlank(onlineUser)) {
            userDetailResponse.setActiveStatus("1");
        }

        if (tUserEntity.getLatelyTime() != null && !"1".equals(userDetailResponse.getActiveStatus())) {

            if (DateUtil.between(tUserEntity.getLatelyTime(), new Date(), DateUnit.HOUR) <= 6) {
                userDetailResponse.setActiveStatus("2");
            } else if (DateUtil.between(tUserEntity.getLatelyTime(), new Date(), DateUnit.DAY) <= 7) {
                userDetailResponse.setActiveStatus("3");
            } else {
                userDetailResponse.setActiveStatus("4");
            }
        }

        userDetailResponse.setApplyStatus("0");
        userDetailResponse.setIsStar(false);


        List<String> informationList = new ArrayList<>();

        if (tUserEntity.getBirthDate() != null) {
            userDetailResponse.setAge(DateUtils.getAgeByBirth(tUserEntity.getBirthDate()));
            informationList.add(userDetailResponse.getAge() + "岁");
            informationList.add(DateUtils.getConstellation(tUserEntity.getBirthDate()));

        }

        if (tUserEntity.getHeight() != null) {
            informationList.add(tUserEntity.getHeight() + "cm");
        }

        if (tUserEntity.getWeight() != null) {
            informationList.add(tUserEntity.getWeight() + "kg");
        }

        userDetailResponse.setInformationList(StringUtils.join(informationList, "·"));

        List<String> informationList2 = new ArrayList<>();
        if (StringUtils.isNotBlank(tUserEntity.getCity())) {
            String city = tUserEntity.getCity();
            if (Boolean.FALSE.equals(tUserEntity.getMainCity()) && StringUtils.isNotBlank(tUserEntity.getCity()) && StringUtils.isNotBlank(tUserEntity.getCounty())) {
                city = city + tUserEntity.getCounty();
            }
            informationList2.add(city);
        }
        if (StringUtils.isNotBlank(tUserEntity.getJobMes())) {
            informationList2.add(tUserEntity.getJobMes());
        }
        userDetailResponse.setInformationBaseList(StringUtils.join(informationList2, "·"));
        if (tUserEntity.getFondTags() != null) {
            Set<String> tags = new LinkedHashSet<>();
            if (StringUtils.isNotBlank(tUserEntity.getFondTags().getMainLabel())) {
                tags.addAll(ListUtil.toList(tUserEntity.getFondTags().getMainLabel().split(",")));
            }
            if (StringUtils.isNotBlank(tUserEntity.getFondTags().getMyLabel())) {
                for (String tag : tUserEntity.getFondTags().getMyLabel().split(",")) {
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                    }
                }
            }
            userDetailResponse.setFondTagsFormat(StringUtils.join(tags, ","));

            String json = msfConfigService.getValueByCode("label_json");
            if (StringUtils.isNotBlank(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
                List<LabelInfoResponse> list = jsonObject.getJSONArray("label").toJavaList(LabelInfoResponse.class);
                List<LabelInfoResponse> labelList = new ArrayList<>();
                for (String tag : tags) {
                    for (LabelInfoResponse labelInfoResponse : list) {
                        for (LabelInfoResponse child : labelInfoResponse.getChilds()) {
                            if (child.getName().equals(tag)) {
                                LabelInfoResponse labelInfoResponse1 = new LabelInfoResponse();
                                labelInfoResponse1.setName(child.getName());
                                labelInfoResponse1.setImgUrl(child.getImgUrl());
                                labelList.add(labelInfoResponse1);
                                break;
                            }
                        }
                    }
                }
                userDetailResponse.setFondTagsList(labelList);
            }
        }


        String ownerId = TokenUtils.getOwnerId();
        if (StringUtils.isNotBlank(ownerId)) {
            Wrapper<UserApplyEntity> ownerWrapper = new QueryWrapper<UserApplyEntity>().lambda().eq(UserApplyEntity::getUserId, ownerId).eq(UserApplyEntity::getApplyUserId, id).orderByDesc(UserApplyEntity::getCreateTime).last("limit 1");
            UserApplyEntity userApplyEntity = this.userApplyService.getOne(ownerWrapper);
            if (userApplyEntity != null) {
                userDetailResponse.setApplyStatus(userApplyEntity.getStatus());
                if ("3".equals(userApplyEntity.getStatus()) && userApplyEntity.getCreateTime().before(DateUtils.addDays(new Date(), 7))) {
                    userDetailResponse.setApplyStatus("0");
                }
                if ("2".equals(userApplyEntity.getStatus())) {
                    userDetailResponse.setWechat(userProfileEntity.getWechat());
                }
            } else {
                Wrapper<UserApplyEntity> ownerWrapper1 = new QueryWrapper<UserApplyEntity>().lambda().eq(UserApplyEntity::getUserId, id).eq(UserApplyEntity::getApplyUserId, ownerId).orderByDesc(UserApplyEntity::getCreateTime).last("limit 1");
                UserApplyEntity userApplyEntity1 = this.userApplyService.getOne(ownerWrapper1);
                if (userApplyEntity1 != null && "2".equals(userApplyEntity1.getStatus())) {
                    userDetailResponse.setApplyStatus(userApplyEntity1.getStatus());
                    userDetailResponse.setWechat(userProfileEntity.getWechat());
                }
            }

            Wrapper<UserStarEntity> userStarEntityWrapper = new QueryWrapper<UserStarEntity>().lambda().eq(UserStarEntity::getUserId, ownerId).eq(UserStarEntity::getStarUserId, id);
            userDetailResponse.setIsStar(this.userStarService.count(userStarEntityWrapper) > 0);

            Wrapper<UserChatEntity> userChatEntityWrapper = new QueryWrapper<UserChatEntity>().lambda()
                    .eq(UserChatEntity::getUserId, ownerId).eq(UserChatEntity::getApplyUserId, id);
            userDetailResponse.setIsChat(this.userChatService.count(userChatEntityWrapper) > 0);

            String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");
            if (id.equals(adminPhoneList) || ownerId.equals(adminPhoneList)) {
                userDetailResponse.setIsChat(true);
            }
        } else {
            userDetailResponse.setIsChat(false);
        }

        Wrapper<ArticleEntity> articleEntityWrapper = new QueryWrapper<ArticleEntity>().lambda()
                .eq(ArticleEntity::getUserId, id).eq(ArticleEntity::getOpen, "1")
                .ne(ArticleEntity::getImg, "[]")
                .orderByDesc(ArticleEntity::getCreateTime).last("limit 4");
        List<ArticleEntity> articleEntity = articleService.list(articleEntityWrapper);
        if (CollectionUtil.isNotEmpty(articleEntity)) {
            UserArticleViewResponse dynamicsBriefResponse = new UserArticleViewResponse();
            for (ArticleEntity entity : articleEntity) {
                ArticlePreVo articlePreVo = new ArticlePreVo();
                articlePreVo.setImg(entity.getImg().get(0));
                articlePreVo.setType(entity.getType());
                dynamicsBriefResponse.getArticleImg().add(articlePreVo);
            }
            userDetailResponse.setUserArticleViewResponse(dynamicsBriefResponse);
        } else {
            Wrapper<ArticleEntity> articleEntityOneWrapper = new QueryWrapper<ArticleEntity>().lambda()
                    .eq(ArticleEntity::getUserId, id).eq(ArticleEntity::getOpen, "1")
                    .orderByDesc(ArticleEntity::getCreateTime).last("limit 1");
            ArticleEntity articleEntityOne = articleService.getOne(articleEntityOneWrapper);
            if (articleEntityOne != null) {
                UserArticleViewResponse userArticleViewResponse = new UserArticleViewResponse();
                userArticleViewResponse.setArticleContent(articleEntityOne.getContent());
                userDetailResponse.setUserArticleViewResponse(userArticleViewResponse);
            }
        }

        Wrapper<UserStarEntity> userStarEntityWrapper1 = new QueryWrapper<UserStarEntity>().lambda().eq(UserStarEntity::getUserId, id);
        userDetailResponse.setStarNum(userStarService.count(userStarEntityWrapper1));
        Wrapper<UserStarEntity> userStarEntityWrapper2 = new QueryWrapper<UserStarEntity>().lambda().eq(UserStarEntity::getStarUserId, id);
        userDetailResponse.setStarByNum(userStarService.count(userStarEntityWrapper2));

        Wrapper<ArticleEntity> articleEntityWrapperNum = new QueryWrapper<ArticleEntity>().lambda().eq(ArticleEntity::getUserId, id);
        userDetailResponse.setArticleNum(articleService.count(articleEntityWrapperNum));
        if (StringUtils.isNotBlank(distanceRequest.getLonA()) && StringUtils.isNotBlank(distanceRequest.getLatA()) && StringUtils.isNotBlank(distanceRequest.getLonB()) && StringUtils.isNotBlank(distanceRequest.getLatB())) {
            userDetailResponse.setDistance(this.getBaseMapper().getDistance(distanceRequest));
            if (userDetailResponse.getDistance() != null) {
                if (userDetailResponse.getDistance().compareTo(new BigDecimal("1000")) < 0) {
                    userDetailResponse.setDistanceFormat(userDetailResponse.getDistance().setScale(0, RoundingMode.DOWN) + "m");
                } else {
                    userDetailResponse.setDistanceFormat(userDetailResponse.getDistance().divide(new BigDecimal("1000"), 1, RoundingMode.DOWN) + "km");
                }
            }
        }

        if (StringUtils.isNotBlank(userDetailResponse.getIdCard())) {
            //脱敏 只保留前四位
            userDetailResponse.setIdCard(NumberUtils.maskAfterFour(userDetailResponse.getIdCard(), 4));
        }

        if (Boolean.FALSE.equals(userDetailResponse.getMainCity()) && StringUtils.isNotBlank(userDetailResponse.getCity()) && StringUtils.isNotBlank(userDetailResponse.getCounty())) {
            userDetailResponse.setCity(userDetailResponse.getCity() + userDetailResponse.getCounty());
        }

        if (StringUtils.isNotBlank(ownerId) && !ownerId.equals(id)) {
            userVisitService.addVisit(ownerId, id);
            AddNatureVisitVo addNatureVisitVo = new AddNatureVisitVo();
            addNatureVisitVo.setUserId(ownerId);
            addNatureVisitVo.setVisitUserId(id);
            addNatureVisitVo.setType("1");
            userVisitService.addNatureVisit(addNatureVisitVo);
        }
        return userDetailResponse;
    }

    @Transactional
    @RedissonLock(lockName = "starLock:", isLockNameAppendOwner = true)
    @Override
    public Boolean star(String id) {

        Wrapper<UserShieldEntity> shieldWrapper = new QueryWrapper<UserShieldEntity>().lambda()
                .eq(UserShieldEntity::getUserId, id).eq(UserShieldEntity::getShieldId, TokenUtils.getOwnerId());
        if (userShieldService.count(shieldWrapper) > 0) {
            throw new JrsfException(UserExceptionEnum.SHIELD_USER_EXCEPTION);
        }

        if (id.equals(TokenUtils.getOwnerId())) {
            throw new JrsfException(UserExceptionEnum.STAR_SELF_EXCEPTION);
        }


        Wrapper<UserStarEntity> userStarEntityWrapper = new QueryWrapper<UserStarEntity>().lambda()
                .eq(UserStarEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserStarEntity::getStarUserId, id);
        if (this.userStarService.count(userStarEntityWrapper) == 0) {
            UserStarEntity userStarEntity = new UserStarEntity();
            userStarEntity.setUserId(TokenUtils.getOwnerId());
            userStarEntity.setStarUserId(id);
            userStarEntity.setReadStatus("0");
            this.userStarService.save(userStarEntity);

            AddNatureVisitVo addNatureVisitVo = new AddNatureVisitVo();
            addNatureVisitVo.setUserId(TokenUtils.getOwnerId());
            addNatureVisitVo.setVisitUserId(id);
            addNatureVisitVo.setType("2");
            userVisitService.addNatureVisit(addNatureVisitVo);
        }

        Wrapper<UserStarEntity> userStarEntity = new QueryWrapper<UserStarEntity>().lambda()
                .eq(UserStarEntity::getUserId, id)
                .eq(UserStarEntity::getStarUserId, TokenUtils.getOwnerId());
        return userStarService.count(userStarEntity) > 0;
    }

    @Transactional
    @Override
    public void cancelStar(String id) {

        Wrapper<UserStarEntity> userStarEntityWrapper = new QueryWrapper<UserStarEntity>().lambda().eq(UserStarEntity::getUserId, TokenUtils.getOwnerId()).eq(UserStarEntity::getStarUserId, id);
        this.userStarService.remove(userStarEntityWrapper);
    }

    @Override
    public void updateLatelyTime(String userId) {

        this.update(null, new LambdaUpdateWrapper<TUserEntity>().set(TUserEntity::getLatelyTime, new Date()).eq(TUserEntity::getId, userId));
    }

    @Transactional
    @Override
    public void invisible(InvisibleRequest request) {

        if (!request.getStatus().equals("1") && !request.getStatus().equals("2")) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("状态错误");
        }
        TUserEntity frUserEntity = this.getById(TokenUtils.getOwnerId());
        if (frUserEntity == null) throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        if ("1".equals(request.getStatus())) {

            UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
            if (userProfileEntity.getInvisibleDate() != null) {
                //如果隐身时间在两周内 则无能再隐身
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -7);
                if (userProfileEntity.getInvisibleDate().after(calendar.getTime())) {
                    throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("每周只能隐藏一次");
                }
            }
        }

        this.update(new LambdaUpdateWrapper<TUserEntity>().set(TUserEntity::getInvisible, request.getStatus()).eq(TUserEntity::getId, TokenUtils.getOwnerId()));
        if ("1".equals(request.getStatus())) {
            userProfileService.update(new LambdaUpdateWrapper<UserProfileEntity>().set(UserProfileEntity::getInvisibleDate, new Date()).eq(UserProfileEntity::getId, frUserEntity.getProfileId()));
        }
    }

    @Override
    @Transactional
    public void photoEdit(PhotoEditRequest request) {


        TUserEntity frUserEntity = this.getById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        if (PhotoEditTypeEnum.DELETE.equals(request.getPhotoEditType())) {

            if (StringUtils.isBlank(request.getOldUrl())) {
                throw new JrsfException(UserExceptionEnum.OLD_URL_NOT_EMPTY_EXCEPTION);
            }

            if (CollectionUtil.isNotEmpty(frUserEntity.getPersonalPhoto())) {

                frUserEntity.getPersonalPhoto().removeIf(p -> p.equals(request.getOldUrl()));
                if (CollectionUtil.isEmpty(frUserEntity.getPersonalPhoto())) {
                    throw new JrsfException(UserExceptionEnum.FIRST_PHOTO_NOT_DELETE_EXCEPTION);
                }
                if (CollectionUtil.isEmpty(frUserEntity.getPersonalPhoto()) || !frUserEntity.getPersonalPhoto().contains(request.getOldUrl())) {
                    msfFileService.deleteFileByUrl(request.getOldUrl());
                }

            }
        } else if (PhotoEditTypeEnum.REPLACE.equals(request.getPhotoEditType())) {
            if (StringUtils.isBlank(request.getNewUrl())) {
                throw new JrsfException(UserExceptionEnum.NEW_URL_NOT_EMPTY_EXCEPTION);
            }

            if (StringUtils.isBlank(request.getOldUrl())) {
                throw new JrsfException(UserExceptionEnum.OLD_URL_NOT_EMPTY_EXCEPTION);
            }

            if (CollectionUtil.isNotEmpty(frUserEntity.getPersonalPhoto())) {
                //  Collections.replaceAll(frUserEntity.getPersonalPhoto(), request.getOldUrl(), request.getNewUrl());
                if (CollectionUtil.isEmpty(frUserEntity.getPersonalPhoto()) || !frUserEntity.getPersonalPhoto().contains(request.getOldUrl())) {
                    msfFileService.deleteFileByUrl(request.getOldUrl());
                }
            }
        } else if (PhotoEditTypeEnum.ADD.equals(request.getPhotoEditType())) {

            if (StringUtils.isBlank(request.getNewUrl())) {
                throw new JrsfException(UserExceptionEnum.NEW_URL_NOT_EMPTY_EXCEPTION);
            }

            if (CollectionUtil.isNotEmpty(frUserEntity.getPersonalPhoto())) {
                // frUserEntity.getPersonalPhoto().add(request.getNewUrl());
            } else {
                List<String> urlList = new ArrayList<>();
                urlList.add(request.getNewUrl());
                //  frUserEntity.setPersonalPhoto(urlList);
            }
        }
        this.updateById(frUserEntity);
    }

    @RedissonLock(lockName = "applyWx:", isLockNameAppendOwner = true)
    @Transactional
    @Override
    public ServiceR<Void> applyWx(ApplyWxRequest request) {
        Wrapper<UserShieldEntity> shieldWrapper = new QueryWrapper<UserShieldEntity>().lambda()
                .eq(UserShieldEntity::getUserId, request.getApplyUserId())
                .eq(UserShieldEntity::getShieldId, TokenUtils.getOwnerId());
        if (userShieldService.count(shieldWrapper) > 0) {
            throw new JrsfException(UserExceptionEnum.SHIELD_USER_EXCEPTION);
        }

        if (request.getApplyUserId().equals(TokenUtils.getOwnerId())) {
            throw new JrsfException(UserExceptionEnum.APPLY_SELF_EXCEPTION);
        }

        TUserEntity ownerUser = this.getById(TokenUtils.getOwnerId());
        if (ownerUser == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }

        if ("1".equals(ownerUser.getInvisible())) {
            throw new JrsfException(UserExceptionEnum.USER_IS_INVISIBLE);
        }
        UserApplyEntity userApplyEntity = new UserApplyEntity();
        BigDecimal price = new BigDecimal("0");
        if ("2".equals(request.getApplyWay())) {

            if (!Boolean.TRUE.equals(userVipService.isVip(TokenUtils.getOwnerId()))) {
                return ServiceR.fail(UserExceptionEnum.NOT_VIP_USER_EXCEPTION);
            }

            String vipApplyLimit = msfConfigService.getValueByCode("vipApplyLimit");
            if (StringUtils.isNotBlank(vipApplyLimit)) {
                Wrapper<UserApplyEntity> userApplyEntityWrapperCount = new QueryWrapper<UserApplyEntity>().lambda()
                        .eq(UserApplyEntity::getUserId, TokenUtils.getOwnerId())
                        .eq(UserApplyEntity::getApplyWay, "2")
                        .ge(UserApplyEntity::getCreateTime, DateUtil.beginOfDay(new Date()));
                long count = userApplyService.count(userApplyEntityWrapperCount);
                if (count >= Long.parseLong(vipApplyLimit)) {
                    return ServiceR.fail(UserExceptionEnum.APPLY_COUNT_ERROR_EXCEPTION);
                }
            }
            //vip解锁
            userApplyEntity.setApplyWay("2");
        } else if ("1".equals(request.getApplyWay())) {
            BigDecimal blance = userAccountService.getBalance(TokenUtils.getOwnerId());
            String applyAmount = msfConfigService.getValueByCode("applyAmount");
            price = new BigDecimal(applyAmount);
            if (blance.compareTo(price) < 0) {
                return ServiceR.fail(UserExceptionEnum.BALANCE_LESS);
            }
            userApplyEntity.setApplyWay("1");
        } else {
            return ServiceR.fail("解锁方式错误");
        }

        TUserEntity applyUser = this.getById(request.getApplyUserId());
        if (applyUser == null) {
            return ServiceR.fail(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity applyUserProfile = userProfileService.getById(applyUser.getProfileId());
        if (Boolean.TRUE.equals(applyUserProfile.getWechatOpen())) {
            userApplyEntity.setStatus("2");
            userApplyEntity.setApplyDesc("直接解锁微信");
            //添加聊天
            UserChatEntity userChatEntity = new UserChatEntity();
            userChatEntity.setUserId(TokenUtils.getOwnerId());
            userChatEntity.setApplyUserId(request.getApplyUserId());
            userChatEntity.setApplyWay("3");
            userChatService.save(userChatEntity);

            UserChatEntity userApplyChatEntity = new UserChatEntity();
            userApplyChatEntity.setUserId(request.getApplyUserId());
            userApplyChatEntity.setApplyUserId(TokenUtils.getOwnerId());
            userApplyChatEntity.setApplyWay("3");
            userChatService.save(userApplyChatEntity);

        } else {
            if (StringUtils.isBlank(request.getApplyDesc())) {
                return ServiceR.fail("请输入申请描述");
            }
            userApplyEntity.setStatus("1");
            userApplyEntity.setApplyDesc(request.getApplyDesc());
        }

        userApplyEntity.setUserId(TokenUtils.getOwnerId());
        userApplyEntity.setApplyUserId(request.getApplyUserId());
        userApplyEntity.setApplyReadStatus("0");
        userApplyEntity.setApplyWaitStatus("1");
        userApplyService.save(userApplyEntity);

        if ("1".equals(request.getApplyWay())) {
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setUserId(TokenUtils.getOwnerId());
            //price 相反数
            userAccountEntity.setAmount(price.negate());
            userAccountEntity.setSource("UserApplyEntity");
            userAccountEntity.setSourceId(userApplyEntity.getId());
            userAccountEntity.setRemarks("解锁微信");
            userAccountService.save(userAccountEntity);
        }
        List<SmsData> smsDataList = new ArrayList<>();
        smsDataList.add(new SmsData("name", ownerUser.getNickName()));
        MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
        messageSendServcie.sendMessage(new SendUserMessageVo(userApplyEntity.getApplyUserId(),
                SendUserMessageTypeEnum.FRIEND,
                SendUserMessageEnum.APPLY_WECHAT,
                userApplyEntity.getApplyDesc(),
                smsDataList, false
        ));
        //查询用户是否是充值vip或是充值颜币后第一次解锁用户微信
        RewardSendVo rewardSendVo = new RewardSendVo();
        rewardSendVo.setApplyId(userApplyEntity.getId());
        rewardSendVo.setPayUserId(userApplyEntity.getUserId());
        rewardSendVo.setApplyUserId(userApplyEntity.getApplyUserId());
        rewardSendVo.setType("1");
        asyncService.reward(rewardSendVo);

        return ServiceR.ok();
    }

    @Transactional
    @Override
    public void removeUser() {
        TUserEntity frUserEntity = this.getById(TokenUtils.getOwnerId());
        if (frUserEntity != null) {
            frUserEntity.setStatus(FrUserStatusEnum.LOGOFF);
            this.updateById(frUserEntity);
            userProfileService.removeById(frUserEntity.getProfileId());

            String onlineToken = redisService.getCacheObject(SecurityConstants.MANY_ONLINE_USER_KEY + frUserEntity.getId());
            if (StringUtils.isNotBlank(onlineToken)) {
                redisService.deleteObject(onlineToken);
                redisService.deleteObject(SecurityConstants.MANY_ONLINE_USER_KEY + frUserEntity.getId());
            }
        }
    }


    @Override
    public MessageReadResponse getReadStatus() {
        MessageReadResponse messageReadResponse = new MessageReadResponse();
        messageReadResponse.setStarMe(userStarService.getCount(TokenUtils.getOwnerId()));

        messageReadResponse.setApplyMe(userApplyService.getApplyMeCount(TokenUtils.getOwnerId()));

        messageReadResponse.setMyApply(userApplyService.getApplyWaitCount(TokenUtils.getOwnerId()));

        return messageReadResponse;
    }

    @Override
    public String idAuth(IdAuthRequest request) {

        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("faceid.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            FaceidClient client = new FaceidClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            IdCardVerificationRequest req = new IdCardVerificationRequest();

            // 返回的resp是一个IdCardVerificationResponse的实例，与请求对象对应
            req.setIdCard(request.getIdCard());
            req.setName(request.getName());
            IdCardVerificationResponse resp = client.IdCardVerification(req);
            if (!"0".equals(resp.getResult())) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(resp.getDescription());
            }

        } catch (TencentCloudSDKException e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
        String uuid = MsfCommonTool.UUID();
        redisService.setCacheObject(uuid, request.getIdCard(), 10l, TimeUnit.MINUTES);
        return uuid;
    }

    @Override
    public PageResult<UserPageResponse> examinePage(UserPageRequest request, Integer pageIndex, Integer pageSize) {

        Page<UserPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.baseMapper.getExaminePage(request);
        PageResult<UserPageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    @Transactional
    public void examine(UserExamineRequest request) {

        RLock lock = redissonClient.getLock(Constants.PHOTO_EDIT + request.getUserId());
        try {
            lock.lock();
            TUserEntity frUserEntity = this.getById(request.getUserId());
            if (frUserEntity == null) {
                throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            }

            //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
            frUserEntity.setIdAuth(request.getResult());
            this.updateById(frUserEntity);
            Wrapper<UserIdcardAuditEntity> wrapper = new QueryWrapper<UserIdcardAuditEntity>().lambda().eq(UserIdcardAuditEntity::getUserId, request.getUserId()).orderByDesc(UserIdcardAuditEntity::getCreateTime).last("limit 1");
            UserIdcardAuditEntity userEduAuditEntity = userIdcardAuditService.getBaseMapper().selectOne(wrapper);
            if (userEduAuditEntity != null) {
                userEduAuditEntity.setStatus(request.getResult());
                userIdcardAuditService.updateById(userEduAuditEntity);
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public UserExamineInfoResponse getExamineInfo(String id) {

        UserExamineInfoResponse response = new UserExamineInfoResponse();
        TUserEntity frUserEntity = this.baseMapper.selectById(id);
        if (frUserEntity != null) {
            BeanUtils.copyProperties(frUserEntity, response);
            //response.setImgList(frUserEntity.getPersonalPhoto());
        }

        return response;
    }


    @Override
    public PageResult<UserIdentityPageResponse> identityPage(UserPageRequest request, Integer pageIndex, Integer pageSize) {
        Page<UserIdentityPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.baseMapper.getIdentityPage(request);
        PageResult<UserIdentityPageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    public IdentityExamineInfoResponse identityExamine(Integer id) {
        IdentityExamineInfoResponse response = new IdentityExamineInfoResponse();
        TUserEntity frUserEntity = this.baseMapper.selectById(id);
        if (frUserEntity != null) {
            BeanUtils.copyProperties(frUserEntity, response);
          /*  if (frUserEntity.getAdditional() != null) {
                if (AuthStatusEnum.REFUSE.equals(frUserEntity.getAdditional().getIdentityAuth())) {
                    FrUserExamineEntity frUserExamineEntity = this.frUserExamineService.getExamine(id, AuthTypeEnum.IdentityAuth, AuthStatusEnum.REFUSE);
                    if (frUserExamineEntity != null) {
                        response.setRemarks(frUserExamineEntity.getRemarks());
                    }
                }
                response.setAuthStatus(frUserEntity.getAdditional().getIdentityAuth());
            }*/

           /* response.setConstellation(DateUtils.getConstellation(frUserEntity.getBirthday()));
            FrUserAuthService frUserAuthService = SpringUtils.getBean(FrUserAuthService.class);
            FrUserAuthEntity frUserAuthEntity = frUserAuthService.getUserAuth(id, AuthTypeEnum.IdentityAuth, AuthStatusEnum.EXAMINE);
            if (frUserAuthEntity != null) {
                response.setImgList(frUserAuthEntity.getImgList());
                response.setVersion(frUserAuthEntity.getId());
            } else {
                response.setImgList(null);
                response.setVersion(0);
            }*/
        }

        return response;
    }

    @Transactional
    @Override
    public void identityExamine(UserExamineRequest request) {

        RLock lock = redissonClient.getLock(Constants.ADD_AUTH + request.getUserId());
        try {
            lock.lock();
            TUserEntity frUserEntity = this.getById(request.getUserId());
            if (frUserEntity == null) {
                throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            }

            //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
            frUserEntity.setIdAuth(request.getResult());
            this.updateById(frUserEntity);
            Wrapper<UserIdcardAuditEntity> wrapper = new QueryWrapper<UserIdcardAuditEntity>().lambda().eq(UserIdcardAuditEntity::getUserId, request.getUserId()).orderByDesc(UserIdcardAuditEntity::getCreateTime).last("limit 1");
            UserIdcardAuditEntity userEduAuditEntity = userIdcardAuditService.getBaseMapper().selectOne(wrapper);
            if (userEduAuditEntity != null) {
                userEduAuditEntity.setStatus(request.getResult());
                userIdcardAuditService.updateById(userEduAuditEntity);
            }


        } finally {
            lock.unlock();
        }
    }

    @Override
    public PageResult<UserEducationPageResponse> userEducationPage(UserPageRequest request, Integer pageIndex, Integer pageSize) {
        Page<UserEducationPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.baseMapper.getUserEducationPage(request);
        PageResult<UserEducationPageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    public EducationExamineInfoResponse educationExamine(String id) {
        EducationExamineInfoResponse response = new EducationExamineInfoResponse();
        TUserEntity frUserEntity = this.baseMapper.selectById(id);
        if (frUserEntity != null) {
            BeanUtils.copyProperties(frUserEntity, response);

            Wrapper<UserEduAuditEntity> wrapper = new QueryWrapper<UserEduAuditEntity>().lambda().eq(UserEduAuditEntity::getUserId, id).orderByDesc(UserEduAuditEntity::getCreateTime).last("limit 1");
            UserEduAuditEntity userEduAuditEntity = userEduAuditService.getBaseMapper().selectOne(wrapper);
            if (userEduAuditEntity != null) {
                response.setAuthStatus(userEduAuditEntity.getStatus());
                response.setImgList(userEduAuditEntity.getEvidence());
                response.setVersion(userEduAuditEntity.getVersion());
                response.setSchool(userEduAuditEntity.getSchool());
                response.setEducation(userEduAuditEntity.getEducation());
            }
        }

        return response;
    }

    @Override
    public void educationExamine(UserExamineRequest request) {

        RLock lock = redissonClient.getLock(Constants.ADD_AUTH + request.getUserId());
        try {
            lock.lock();
            TUserEntity frUserEntity = this.getById(request.getUserId());
            if (frUserEntity == null) {
                throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            }
            //1-未认证 2-认证中 3-通过 4-拒绝 5-失败
            frUserEntity.setEduAuth(request.getResult());
            this.updateById(frUserEntity);
            Wrapper<UserEduAuditEntity> wrapper = new QueryWrapper<UserEduAuditEntity>().lambda().eq(UserEduAuditEntity::getUserId, request.getUserId()).orderByDesc(UserEduAuditEntity::getCreateTime).last("limit 1");
            UserEduAuditEntity userEduAuditEntity = userEduAuditService.getBaseMapper().selectOne(wrapper);
            if (userEduAuditEntity != null) {
                userEduAuditEntity.setStatus(request.getResult());
                userEduAuditService.updateById(userEduAuditEntity);
            }

        } finally {
            lock.unlock();
        }
    }

    @Override
    public PageResult<UserInfoAdminPageResponse> userPage(UserInfoRequest request, Integer pageIndex, Integer pageSize) {
        if (StringUtils.isNotBlank(request.getNickName())) {
            request.setNickName("%" + request.getNickName() + "%");
        }

        if (StringUtils.isNotBlank(request.getCity())) {
            request.setCity("%" + request.getCity() + "%");
        }
        Page<UserInfoAdminPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.baseMapper.getUserInfoPage(request);
        PageResult<UserInfoAdminPageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    public UserAdminInfoResponse userAdminInfo(String id) {

        UserAdminInfoResponse response = new UserAdminInfoResponse();
        TUserEntity frUserEntity = this.baseMapper.selectById(id);
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
        BeanUtils.copyProperties(userProfileEntity, response);
        BeanUtils.copyProperties(frUserEntity, response);
        if (frUserEntity.getFondTags() != null) {
            response.setFondTags(frUserEntity.getFondTags().getMyLabel());
        }
        return response;
    }

    @Override
    @Transactional
    public void updateUser(UserAdminInfoAddRequest request) {

        if (request.getId() != null) {
            TUserEntity frUserEntity = this.baseMapper.selectById(request.getId());
            if (frUserEntity == null) {
                throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
            }

            if (!"2".equals(frUserEntity.getUserType())) {
                throw new JrsfException(UserExceptionEnum.USER_NOT_DUMMY_EXCEPTION);
            }
            UserProfileEntity userProfileEntity = userProfileService.getById(frUserEntity.getProfileId());
            //msfFileService.deleteImg(frUserEntity.getPersonalPhoto(), request.getPersonalPhoto());
        }
        TUserEntity frUserEntity = new TUserEntity();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        BeanUtils.copyProperties(request, userProfileEntity);
        BeanUtils.copyProperties(request, frUserEntity);
        frUserEntity.setOpenId("");
        // frUserEntity.setSessionKey("");
        if (CollectionUtil.isNotEmpty(request.getPersonalPhoto())) {
            frUserEntity.setAvatar(request.getPersonalPhoto().get(request.getPersonalPhoto().size() - 1));
        }
        frUserEntity.setUserType("2");
        frUserEntity.setIdAuth("3");
        frUserEntity.setEduAuth("3");

        if (StringUtils.isNotBlank(request.getFondTags())) {
            LabelVo labelVo = new LabelVo();
            labelVo.setMyLabel(request.getFondTags());
            frUserEntity.setFondTags(labelVo);
        }
        frUserEntity.setLatelyTime(new Date());
        frUserEntity.setInvisible("2");
        userProfileService.saveOrUpdate(userProfileEntity);
        saveOrUpdate(frUserEntity);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        TUserEntity frUserEntity = this.baseMapper.selectById(id);
        if (frUserEntity != null && !"2".equals(frUserEntity.getUserType())) {
            throw new JrsfException(UserExceptionEnum.USER_NOT_DUMMY_EXCEPTION);
        }
        this.removeById(id);
    }

    @Override
    public OutlineResponse outline() {

        OutlineResponse outlineResponse = new OutlineResponse();

        //用户总数
        LambdaQueryWrapper<TUserEntity> queryWrapper = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1");
        outlineResponse.setUserCount(count(queryWrapper));

        //今天注册
        Calendar calendar = Calendar.getInstance();
        LambdaQueryWrapper<TUserEntity> todayRegisterQuery = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1").ge(TUserEntity::getCreateTime, DateUtils.getStartTimeOfDay(calendar.getTime()));
        outlineResponse.setTodayRegister(count(todayRegisterQuery));

        //今日在线
        LambdaQueryWrapper<TUserEntity> todayOnlineQuery = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1").ge(TUserEntity::getLatelyTime, DateUtils.getStartTimeOfDay(calendar.getTime()));
        outlineResponse.setTodayOnline(count(todayOnlineQuery));

        //24小时注册
        calendar.add(Calendar.HOUR, -24);
        LambdaQueryWrapper<TUserEntity> registerQuery = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1").ge(TUserEntity::getCreateTime, calendar.getTime());
        outlineResponse.setRegister24(count(registerQuery));

        //24小时在线
        LambdaQueryWrapper<TUserEntity> onlineQuery = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1").ge(TUserEntity::getLatelyTime, calendar.getTime());
        outlineResponse.setOnline24(count(onlineQuery));

        //昨日注册
        LambdaQueryWrapper<TUserEntity> registerYeardayQuery = new QueryWrapper<TUserEntity>().lambda().eq(TUserEntity::getUserType, "1").ge(TUserEntity::getCreateTime, DateUtils.getStartTimeOfDay(calendar.getTime())).le(TUserEntity::getCreateTime, DateUtils.getEndTimeOfDay(calendar.getTime()));
        outlineResponse.setYesterdayRegister(count(registerYeardayQuery));

        outlineResponse.setGenderPie(this.baseMapper.getGenderPie());
        outlineResponse.setCityBar(this.baseMapper.getCityBar());
        return outlineResponse;
    }

    @Override
    public List<ProportionResponse> userRegister(UserRegisterStatisticRequest request) {
        return this.baseMapper.getUserRegisterStatistic(request);
    }

    @Transactional
    @Override
    public void executeGiftPoint() {
        Integer pageIndex = 1;
        Integer pageSize = 100;
        Boolean isTrue = true;
        String applyAmount = msfConfigService.getValueByCode("applyAmount");
        BigDecimal price = new BigDecimal(applyAmount);
        Integer giftUserCount = 0;
        Integer messageUserCount = 0;
        do {

            log.info("executeGiftPoint pageIndex:{}", pageIndex);
            Page<String> page = PageHelper.startPage(pageIndex, pageSize);
            this.getBaseMapper().getGiftUserList();
            PageResult<String> result = new PageResult<>(page);
            if (result.getRows().size() < pageSize) {
                isTrue = false;
            }
            pageIndex++;

            for (String id : result.getRows()) {
                //赠送次数
                TUserEntity tUserEntity = this.getById(id);
                if (tUserEntity != null) {
                    BigDecimal balance = userAccountService.getBalance(tUserEntity.getId());
                    if (balance.compareTo(price) < 0) {
                        //赠送余额
                        UserAccountEntity userAccountEntity = new UserAccountEntity();
                        userAccountEntity.setUserId(tUserEntity.getId());
                        userAccountEntity.setAmount(price);
                        userAccountEntity.setSource("TUserEntity");
                        userAccountEntity.setSourceId(tUserEntity.getId());
                        userAccountEntity.setRemarks("每日系统奖励");
                        userAccountService.save(userAccountEntity);

                        List<SmsData> smsDataList = new ArrayList<>();
                        smsDataList.add(new SmsData("amount", price.toString()));
                        //发送消息
                        MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
                        //发送通知
                        messageSendServcie.sendMessage(new SendUserMessageVo(id,
                                SendUserMessageTypeEnum.SYSTEM,
                                SendUserMessageEnum.SYSTEM_REWARD,
                                "赠送" + price + "颜币，可用于解锁用户微信或私信",
                                smsDataList,
                                false
                        ));
                        giftUserCount++;
                    } else {
                        //提醒余额
                        //发送消息

                        List<SmsData> smsDataList = new ArrayList<>();
                        smsDataList.add(new SmsData("amount", balance.toString()));
                        MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
                        //发送通知
                        messageSendServcie.sendMessage(new SendUserMessageVo(id,
                                SendUserMessageTypeEnum.SYSTEM,
                                SendUserMessageEnum.YANBI_NOT_USE,
                                "您有" + balance + "颜币即将过期，请尽快使用！",
                                smsDataList, false
                        ));
                        messageUserCount++;
                    }
                }

            }

        } while (isTrue);
        log.info("executeGiftPoint end,giftUserCount:{},messageUserCount:{}", giftUserCount, messageUserCount);
    }

    @Override
    public EidAuthResultResponse idAuthCheck(String eidToken) {
        //身份认证校验
        EidAuthResultResponse eidAuthResult = new EidAuthResultResponse();
        ServiceR<EidTokenResulltVo> ret = FaceUtils.getEidResult(eidToken);
        eidAuthResult.setResult(!ServiceR.isError(ret));
        eidAuthResult.setMessage(ret.getMsg());
        return eidAuthResult;
    }

    @Transactional
    @Override
    public void checkUser(UserAdminCheckRequest request) {

        Wrapper<TUserEntity> wrapper = new UpdateWrapper<TUserEntity>().lambda().eq(TUserEntity::getId, request.getId()).set(TUserEntity::getStatus, request.getStatus());
        this.update(wrapper);
        String onlineToken = redisService.getCacheObject(SecurityConstants.MANY_ONLINE_USER_KEY + request.getId());
        if (StringUtils.isNotBlank(onlineToken)) {
            redisService.deleteObject(onlineToken);
            redisService.deleteObject(SecurityConstants.MANY_ONLINE_USER_KEY + request.getId());
        }

    }

    @Override
    public PageResult<ComplaintPageResponse> complaintPage(UserPageRequest request, Integer pageIndex, Integer pageSize) {
        Page<ComplaintPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.baseMapper.complaintPage(request);
        PageResult<ComplaintPageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    public UserComplaintResponse complaintInfo(String id) {

        UserComplaintResponse userComplaintResponse = new UserComplaintResponse();
        ComplaintEntity complaintEntity = complaintService.getById(id);

        if (complaintEntity != null) {
            BeanUtils.copyProperties(complaintEntity, userComplaintResponse);
            if (StringUtils.isNotBlank(complaintEntity.getUserId())) {
                TUserEntity tUserEntity = this.getById(complaintEntity.getUserId());
                if (tUserEntity != null) {
                    UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                    userComplaintResponse.setUserName(tUserEntity.getNickName());
                    userComplaintResponse.setPhone(tUserEntity.getPhone());
                }
            }
            if (StringUtils.isNotBlank(complaintEntity.getComplaintId())) {
                if ("1".equals(complaintEntity.getType())) {
                    TUserEntity tUserEntity = this.getById(complaintEntity.getComplaintId());
                    if (tUserEntity != null) {
                        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                        userComplaintResponse.setComplaintName(tUserEntity.getNickName());
                        userComplaintResponse.setComplaintPhone(tUserEntity.getPhone());
                    }
                } else if ("2".equals(complaintEntity.getType())) {
                    ArticleEntity articleEntity = articleService.getById(complaintEntity.getComplaintId());
                    if (articleEntity != null) {
                        userComplaintResponse.setArticleContent(articleEntity.getContent());
                        userComplaintResponse.setArticleImg(articleEntity.getImg());
                        TUserEntity tUserEntity = this.getById(articleEntity.getUserId());
                        if (tUserEntity != null) {
                            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                            userComplaintResponse.setComplaintPhone(tUserEntity.getPhone());
                        }
                    }
                } else if ("3".equals(complaintEntity.getType())) {
                    ArticleCommentEntity articleCommentEntity = articleCommentService.getById(complaintEntity.getComplaintId());
                    if (articleCommentEntity != null) {
                        userComplaintResponse.setComment(articleCommentEntity.getContent());
                        TUserEntity tUserEntity = this.getById(articleCommentEntity.getUserId());
                        if (tUserEntity != null) {
                            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
                            userComplaintResponse.setComplaintName(tUserEntity.getNickName());
                            userComplaintResponse.setComplaintPhone(tUserEntity.getPhone());
                        }
                    }
                }

            }
        }
        return userComplaintResponse;
    }

    @Transactional
    @Override
    public void postComplaint(UserAdminCheckRequest request) {

        Wrapper<ComplaintEntity> wrapper = new UpdateWrapper<ComplaintEntity>().lambda().eq(ComplaintEntity::getId, request.getId()).set(ComplaintEntity::getStatus, "2");

        complaintService.update(wrapper);
    }

    @Override
    public String getWechatPhone(String code) {
        try {


            HashMap<String, String> param = new HashMap<>();
            param.put("code", code);
            String phoneJSON = restTemplate.postForObject("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + wxAppletService.getAccessToken(), param, String.class);
            JSONObject jsonObject = JSONObject.parseObject(phoneJSON);
            if (jsonObject.getInteger("errcode") == 0) {
                return jsonObject.getJSONObject("phone_info").getString("purePhoneNumber");
            } else {
                throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg(jsonObject.getString("errmsg"));
            }

        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
    }

    @Transactional
    @Override
    public String setWechat() {
        List<UserProfileEntity> list = userProfileService.list();
        for (UserProfileEntity userProfileEntity : list) {
            userProfileEntity.setFakeWechat(NumberUtils.replaceWithRandom(userProfileEntity.getWechat()));
            userProfileService.updateById(userProfileEntity);
        }
        userProfileService.updateBatchById(list);
        return "";
    }

    @Transactional
    @Override
    public void photoAuth() {

        String userId = TokenUtils.getOwnerId();
        TUserEntity tUserEntity = getById(userId);
        if (tUserEntity != null) {
            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
            if (CollectionUtil.isEmpty(tUserEntity.getPersonalPhoto()))
                throw new JrsfException(UserExceptionEnum.PHOTO_IS_EMPTY_EXCEPTION);

            if (!"3".equals(tUserEntity.getIdAuth())) {
                throw new JrsfException(UserExceptionEnum.NOT_ID_AUTH_EXCEPTION);
            }

            // peiwei: calculate new similarity if the first personal photo is changed
            float newSim;
            List<String> newPhotos = new ArrayList<>();//tUserEntity.getPersonalPhoto();
            if (newPhotos != null && !newPhotos.isEmpty()) {
                String newFirstPhoto = newPhotos.get(0);
                // try to get the existing first photo
                String imgBase64 = userProfileEntity.getImgBase64();
                if (imgBase64 != null) {
                    FaceCompareVo faceCompareVo = new FaceCompareVo();
                    faceCompareVo.setImageA(imgBase64);
                    faceCompareVo.setUrlB(newFirstPhoto);
                    ServiceR<Float> simServiceR = FaceUtils.detectFaceSimilarity(faceCompareVo, 1);
                    if (ServiceR.isError(simServiceR)) {
                        throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(simServiceR.getMsg());
                    }
                    newSim = simServiceR.getData();
                } else {
                    // -2 - 认证失败：未保存到人脸核身图片
                    newSim = -2;
                }

                // update sim when the first photo changes
                tUserEntity.setSimilarity(newSim);
                // photo_auth: 用户相册认证 1-未认证 2-认证中 3-通过 4-拒绝 5-失败
                if (newSim >= 70) {
                    tUserEntity.setPhotoAuth("3");
                } else if (newSim > 0) {
                    tUserEntity.setPhotoAuth("4");
                } else if (newSim == -1) {
                    tUserEntity.setPhotoAuth("1");
                } else if (newSim == -2) {
                    tUserEntity.setPhotoAuth("5");
                } else {
                    throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("人脸相似性比对出现异常值");
                }
                this.updateById(tUserEntity);
                userProfileService.updateById(userProfileEntity);
                if ("3".equals(tUserEntity.getPhotoAuth())) {
                    RegisterDistributionService registerDistributionService = SpringUtils.getBean(RegisterDistributionService.class);
                    registerDistributionService.sendAward(tUserEntity.getId());
                }

            }
        }
    }

    @Override
    public Boolean getUserInvalidInfoStatus() {
        String userId = TokenUtils.getOwnerId();
        if (StringUtils.isBlank(userId)) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        TUserEntity user = this.getById(userId);
        if (user == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        UserProfileEntity userProfileEntity = userProfileService.getById(user.getProfileId());
        return userProfileEntity.getHasInvalidInfo() != null ? userProfileEntity.getHasInvalidInfo() : false;
    }

    @Override
    public CenterMessageResponse getCenterMessageResponse() {

        CenterMessageResponse centerMessageResponse = new CenterMessageResponse();
        centerMessageResponse.setVisitCount(this.getBaseMapper().visitUnreadCount(TokenUtils.getOwnerId()));
        return centerMessageResponse;
    }

    @Transactional
    @Override
    public ShareResponse getShare() {

        TUserEntity tUserEntity = this.getById(TokenUtils.getOwnerId());
        if (tUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        ShareResponse shareResponse = this.getBaseMapper().getShareCount(TokenUtils.getOwnerId());
        shareResponse.setWaitAmount(shareResponse.getAmount().subtract(shareResponse.getWithdrawAmount()));
        shareResponse.setWithdrawAbleAmount(shareResponse.getSurplusTotalAmount().subtract(shareResponse.getWithdrawAmount()));
        UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
        if (StringUtils.isBlank(userProfileEntity.getDistributionCode())) {
            userProfileEntity.setDistributionCode(NumberUtils.getShortUUID());
            userProfileService.updateById(userProfileEntity);
            shareResponse.setDistributionCode(userProfileEntity.getDistributionCode());
        } else {
            shareResponse.setDistributionCode(userProfileEntity.getDistributionCode());
        }


        return shareResponse;
    }

    @Override
    public PageResult<SharePageResponse> sharePage(Integer pageIndex, Integer pageSize) {
        Page<SharePageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().sharePage(TokenUtils.getOwnerId());
        PageResult<SharePageResponse> result = new PageResult<>(page);
        return result;
    }

    @Override
    public PageResult<SharePageResponse> sharePersonPage(Integer pageIndex, Integer pageSize) {
        Page<SharePageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().sharePersonPage(TokenUtils.getOwnerId());
        PageResult<SharePageResponse> result = new PageResult<>(page);
        return result;
    }

    @Transactional
    @Override
    public void pay(UserPayRequest request) {

        Wrapper<RegisterDistributionEntity> wrapper = new QueryWrapper<RegisterDistributionEntity>().lambda().eq(RegisterDistributionEntity::getDistributionPersonId, request.getUserId()).eq(RegisterDistributionEntity::getType, "2").eq(RegisterDistributionEntity::getPayStatus, "0");
        long count = registerDistributionService.count(wrapper);

        List<String> registerDistributionList = registerDistributionDao.getRegisterDistributionPay(request.getUserId());
        long awardCount = registerDistributionList.size() / 3;
        long payCount = count + awardCount;
        if (payCount < 10) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("分销人数不足10人");
        }

        Wrapper<RegisterDistributionEntity> wrapperUpdate = new UpdateWrapper<RegisterDistributionEntity>().lambda().eq(RegisterDistributionEntity::getDistributionPersonId, request.getUserId()).eq(RegisterDistributionEntity::getType, "2").eq(RegisterDistributionEntity::getPayStatus, "0").set(RegisterDistributionEntity::getPayStatus, "1");
        registerDistributionService.update(wrapperUpdate);

        if (CollectionUtil.isNotEmpty(registerDistributionList)) {
            Wrapper<RegisterDistributionEntity> wrapperUpdateList = new UpdateWrapper<RegisterDistributionEntity>().lambda().in(RegisterDistributionEntity::getId, registerDistributionList).set(RegisterDistributionEntity::getHigherPayStatus, "1");
            registerDistributionService.update(wrapperUpdateList);
        }

        WithdrawalRecordEntity withdrawalRecordEntity = new WithdrawalRecordEntity();
        withdrawalRecordEntity.setUserId(request.getUserId());
        withdrawalRecordEntity.setAmount(request.getAmount());
        withdrawalRecordService.save(withdrawalRecordEntity);
    }

    @Transactional
    @Override
    public void updateLocation(LocationRequest request) {

        if (StringUtils.isBlank(request.getUserId())) {
            return;
        }
        TUserEntity tUserEntity = this.getById(request.getUserId());
        if (tUserEntity != null) {
            BeanUtils.copyProperties(request, tUserEntity);
            if (StringUtils.isNotBlank(tUserEntity.getCounty()) && StringUtils.isNotBlank(tUserEntity.getCity())) {
                tUserEntity.setMainCity(LocationAnalysisUtils.isMainCity(tUserEntity.getCity(), tUserEntity.getCounty()));
            }
            this.updateById(tUserEntity);
            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
            userProfileEntity.setAddress(request.getAddress());
            userProfileService.updateById(userProfileEntity);
        }
    }

    @Override
    public ServiceR<String> compareFace(String a, String b) {
        FaceCompareVo request = new FaceCompareVo();
        request.setUrlA(a);
        // request.setUrlB(b);
        // return ServiceR.ok("对比：" + FaceUtils.detectFaceSimilarity(request) + "相似度对比：" + FaceUtils.detectFaceSimilarity(request));
        return ServiceR.ok(JSON.toJSONString(FaceUtils.getEidResult(a)));
    }

    @Override
    public ServiceR<UserBaseIndoResponse> isRegister(String code) {
        WxAppletOpenResponse wxAppletOpenResponse = wxAppletService.getOpenIdInfoByCode(code);
        Wrapper<TUserEntity> wrapper = new QueryWrapper<TUserEntity>().lambda()
                .eq(TUserEntity::getOpenId, wxAppletOpenResponse.getOpenId())
                .eq(TUserEntity::getStatus, FrUserStatusEnum.ENABLE)
                .orderByDesc(TUserEntity::getCreateTime)
                .last("limit 1");
        UserBaseIndoResponse userBaseIndoResponse = new UserBaseIndoResponse();
        userBaseIndoResponse.setIsRegister(false);
        TUserEntity userEntity = baseMapper.selectOne(wrapper);
        if (userEntity != null) {
            userBaseIndoResponse.setIsRegister(true);
            userBaseIndoResponse.setSex(userEntity.getSex());
        }

        return ServiceR.ok(userBaseIndoResponse);
    }

    @Override
    public UseSubscribeResponse getuseSubscribe() {

        UseSubscribeResponse useSubscribeResponse = new UseSubscribeResponse();
        useSubscribeResponse.setUseSubscribe(false);
        if (StringUtils.isBlank(TokenUtils.getOwnerId())) {
            return useSubscribeResponse;
        }
        TUserEntity tUserEntity = this.getById(TokenUtils.getOwnerId());
        if (tUserEntity != null) {
            UserProfileEntity userProfileEntity = userProfileService.getById(tUserEntity.getProfileId());
            if (userProfileEntity != null && StringUtils.isNotBlank(userProfileEntity.getPublicOpenId())) {
                String openid = userProfileEntity.getPublicOpenId();
                ServiceR<WxH5UserInfoResponse> ret = wxh5Service.getUserInfoByOpenId(openid);
                if (ServiceR.isSuccess(ret)) {
                    WxH5UserInfoResponse wxH5UserInfoResponse = ret.getData();
                    useSubscribeResponse.setUseSubscribe(wxH5UserInfoResponse.getSubscribe() == 0);
                }

            }
        }
        return useSubscribeResponse;
    }

    @Override
    public void executeReminderApply() {
        Integer pageIndex = 1;
        Integer pageSize = 100;
        Boolean isTrue = true;
        do {
            log.info("executeReminderApply pageIndex:{}", pageIndex);
            Page<String> page = PageHelper.startPage(pageIndex, pageSize);
            this.getBaseMapper().getApplyReminderList();
            PageResult<String> result = new PageResult<>(page);
            if (result.getRows().size() < pageSize) {
                isTrue = false;
            }
            pageIndex++;

            for (String id : result.getRows()) {
                UserApplyEntity userApplyEntity = userApplyService.getById(id);
                if (userApplyEntity != null) {
                    TUserEntity tUserEntity = this.getById(userApplyEntity.getUserId());
                    List<SmsData> smsDataList = new ArrayList<>();
                    smsDataList.add(new SmsData("name", tUserEntity.getNickName()));
                    //发送消息
                    MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
                    //发送通知
                    messageSendServcie.sendMessage(new SendUserMessageVo(userApplyEntity.getApplyUserId(),
                            SendUserMessageTypeEnum.FRIEND,
                            SendUserMessageEnum.WECHAR_APPLY_NOT_PROCESS,
                            tUserEntity.getNickName() + "申请解锁你的微信未审核即将过期啦",
                            smsDataList, true
                    ));
                }
            }

        } while (isTrue);
        log.info("executeReminderApply end");
    }

    @Override
    public void executeReminderVisit() {

        Integer pageIndex = 1;
        Integer pageSize = 100;
        Boolean isTrue = true;
        do {
            log.info("executeReminderVisit pageIndex:{}", pageIndex);
            Page<String> page = PageHelper.startPage(pageIndex, pageSize);
            this.getBaseMapper().getVisitReminderList();
            PageResult<String> result = new PageResult<>(page);
            if (result.getRows().size() < pageSize) {
                isTrue = false;
            }
            pageIndex++;

            for (String id : result.getRows()) {
                //发送消息
                MessageSendServcie messageSendServcie = SpringUtils.getBean(MessageSendServcie.class);
                //发送通知
                messageSendServcie.sendMessage(new SendUserMessageVo(id,
                        SendUserMessageTypeEnum.FRIEND,
                        SendUserMessageEnum.REPEAT_VIEW_YOU,
                        "有人反复查看了你的主页，快去看看是谁吧！",
                        null, true
                ));
            }

        } while (isTrue);
        log.info("executeReminderVisit end");
    }

}
