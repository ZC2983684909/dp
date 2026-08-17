package com.wxmblog.yanjian.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.auth.authority.service.Wxh5Service;
import com.wxmblog.base.auth.common.constant.EnvConstants;
import com.wxmblog.base.auth.common.rest.request.wx.h5.BatchGetMaterialRequest;
import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.constant.ConfigConstants;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.enums.FrUserStatusEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.rest.request.sms.SmsData;
import com.wxmblog.base.common.utils.NumberUtils;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.file.service.MsfFileService;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageEnum;
import com.wxmblog.yanjian.common.enums.message.SendUserMessageTypeEnum;
import com.wxmblog.yanjian.common.rest.request.front.TestRequest;
import com.wxmblog.yanjian.common.rest.vo.DetectFaceResultVo;
import com.wxmblog.yanjian.common.rest.vo.FaceCompareVo;
import com.wxmblog.yanjian.common.rest.vo.SendUserMessageVo;
import com.wxmblog.yanjian.common.utils.FaceUtils;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserAccountEntity;
import com.wxmblog.yanjian.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("yanjian/test")
@Api(tags = "测试")
@Slf4j
public class TestController {


    @Autowired
    private TUserService tUserService;

    @Autowired
    UserApplyService userApplyService;

    @Autowired
    UserNatureVisitService userNatureVisitService;

    @Autowired
    Wxh5Service wxh5Service;

    @Autowired
    private MessageSendServcie messageSendServcie;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private WxAppletService wxAppletService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MsfFileService msfFileService;

    @PostMapping("/compare")
    @AuthIgnore
    @ApiOperation("图片人脸分析")
    public R fileSave(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        FaceCompareVo faceCompareVo = new FaceCompareVo();
        faceCompareVo.setUrlA(request.getUrlA());
        ServiceR<DetectFaceResultVo> detectFaceResultVoServiceR = FaceUtils.detectFace(faceCompareVo, 1);
        if (ServiceR.isError(detectFaceResultVoServiceR)) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(detectFaceResultVoServiceR.getMsg());
        }
        DetectFaceResultVo detectFaceResultVo = detectFaceResultVoServiceR.getData();
        return R.ok(detectFaceResultVo);
    }

    @PostMapping("/compare/batch")
    @AuthIgnore
    @ApiOperation("批量图片人脸分析")
    public R compareBatch(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }

        Wrapper<TUserEntity> wrapper = new QueryWrapper<TUserEntity>().lambda()
                .in(TUserEntity::getPhotoAuth, 2, 3);
        List<TUserEntity> tUserEntities = tUserService.list(wrapper);
        for (TUserEntity tUserEntity : tUserEntities) {
            FaceCompareVo faceCompareVo = new FaceCompareVo();
            faceCompareVo.setUrlA(tUserEntity.getAvatar());
            ServiceR<DetectFaceResultVo> detectFaceResultVoServiceR = FaceUtils.detectFace(faceCompareVo, 1);
            if (ServiceR.isSuccess(detectFaceResultVoServiceR)) {
                DetectFaceResultVo detectFaceResultVo = detectFaceResultVoServiceR.getData();

                log.info("用户{}，质量{}，图片：{}", tUserEntity.getNickName(), detectFaceResultVo.getQuality(), tUserEntity.getAvatar());
                //换行
                System.out.println("\n");
            }

        }

        return R.ok();
    }

    @PostMapping("/detectFaceSimilarity")
    @AuthIgnore
    @ApiOperation("图片相似度检测")
    public R detectFaceSimilarity(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        FaceCompareVo faceCompareVo = new FaceCompareVo();
        faceCompareVo.setImageA(request.getImageA());
        faceCompareVo.setUrlB(request.getUrlB());
        ServiceR<Float> detectFaceResultVoServiceR = FaceUtils.detectFaceSimilarity(faceCompareVo, 1);
        if (ServiceR.isError(detectFaceResultVoServiceR)) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(detectFaceResultVoServiceR.getMsg());
        }
        Float detectFaceResultVo = detectFaceResultVoServiceR.getData();
        return R.ok(detectFaceResultVo);
    }

    @PostMapping("/reSort")
    @AuthIgnore
    @ApiOperation("重置排序")
    public R reSort() {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        userNatureVisitService.executeDeleteNatureVisit();
        return R.ok();
    }

    @PostMapping("/createMenu")
    @AuthIgnore
    @ApiOperation("创建菜单")
    public R createMenu(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        ServiceR<String> result = wxh5Service.createMenu(request.getMenuButtonVo());
        if (!ServiceR.isSuccess(result)) {
            return R.fail(result.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/sendWxTemplateMessage")
    @AuthIgnore
    @ApiOperation("发送微信模板消息")
    public R sendWxTemplateMessage(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        List<SmsData> smsDataList = new ArrayList<>();
        smsDataList.add(new SmsData("name", "王小木"));
        request.getSendUserMessageVo().setSmsDataList(smsDataList);
        messageSendServcie.sendMessage(request.getSendUserMessageVo());
        return R.ok();
    }

    @PostMapping("/batchGetMaterial")
    @AuthIgnore
    @ApiOperation("获取素材列表")
    public R batchGetMaterial(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        BatchGetMaterialRequest param = new BatchGetMaterialRequest();
        param.setType(request.getType());
        param.setOffset(request.getOffset());
        param.setCount(request.getCount());
        return R.ok(wxh5Service.batchGetMaterial(param));
    }

    @PostMapping("/valueOpsByCode")
    @AuthIgnore
    @ApiOperation("测试序列号")
    public R valueOpsByCode(@RequestBody TestRequest request) {
        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("testvalueOpsByCode" + request.getImageA(), endOfDay);
        return R.ok(executeNum);
    }

    //生成微信链接
    @PostMapping("/generateWxh5Link")
    @AuthIgnore
    @ApiOperation("生成微信链接")
    public R generateWxh5Link(@RequestBody TestRequest request) {

        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }

        String url = "https://api.weixin.qq.com/wxa/generatescheme?access_token=" + wxAppletService.getAccessToken();
        Map<String, Object> param = new HashMap<>();
        param.put("is_expire", false);
        JSONObject jsonObject = restTemplate.postForObject(url, param, JSONObject.class);
        return R.ok(jsonObject);

    }

    @PostMapping("/executeReminderVisit")
    @AuthIgnore
    @ApiOperation("访客提醒")
    public R executeReminderVisit(@RequestBody TestRequest request) {

        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        tUserService.executeReminderVisit();
        return R.ok();
    }

    @PostMapping("/executeDeleteTempFileTask")
    @AuthIgnore
    @ApiOperation("删除临时文件")
    public R executeDeleteTempFileTask(@RequestBody TestRequest request) {

        if (!EnvConstants.ENV_TEST.equals(ConfigConstants.ENV())) {
            return R.fail("非测试环境不允许执行此操作");
        }
        msfFileService.executeDeleteTempFileTask();
        return R.ok();
    }


}
