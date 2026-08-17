package com.wxmblog.yanjian.common.utils;

import com.alibaba.fastjson.JSON;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.GetEidResultRequest;
import com.tencentcloudapi.faceid.v20180301.models.GetEidResultResponse;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.constant.PropertiesConstants;
import com.wxmblog.yanjian.common.rest.vo.DetectFaceResultVo;
import com.wxmblog.yanjian.common.rest.vo.FaceCompareVo;
import com.wxmblog.yanjian.common.rest.vo.EidTokenResulltVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.tencentcloudapi.iai.v20200303.models.CompareFaceResponse;
import com.tencentcloudapi.iai.v20200303.IaiClient;
import com.tencentcloudapi.iai.v20200303.models.CompareFaceRequest;
import com.tencentcloudapi.common.AbstractModel;

import com.tencentcloudapi.iai.v20200303.models.*;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
public class FaceUtils {

    /**
     * 人脸比对 一般超过50分则可认定为同一人
     *
     * @param request
     * @return
     */
    public static ServiceR<Float> compareFace(FaceCompareVo request) {

        // 进行相似度比较 (bestFrame 和 personal_photo_first)
        Credential credSim = new Credential(PropertiesConstants.SecretId(), PropertiesConstants.SecretKey());
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfileSim = new HttpProfile();
        httpProfileSim.setEndpoint("iai.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfileSim = new ClientProfile();
        clientProfileSim.setHttpProfile(httpProfileSim);
        // 实例化要请求产品的client对象,clientProfile是可选的
        IaiClient clientSim = new IaiClient(credSim, "ap-chongqing", clientProfileSim);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        CompareFaceRequest reqSim = new CompareFaceRequest();
        if (StringUtils.isNotBlank(request.getImageA())) {
            reqSim.setImageA(request.getImageA());
        }
        if (StringUtils.isNotBlank(request.getImageB())) {
            reqSim.setImageB(request.getImageB());
        }
        if (StringUtils.isNotBlank(request.getUrlA())) {
            reqSim.setUrlA(request.getUrlA());
        }
        if (StringUtils.isNotBlank(request.getUrlB())) {
            reqSim.setUrlB(request.getUrlB());
        }
        CompareFaceResponse respSim = null;
        try {
            respSim = clientSim.CompareFace(reqSim);
        } catch (TencentCloudSDKException e) {
            if (e.getErrorCode().equals("InvalidParameterValue.NoFaceInPhoto")) {
                return ServiceR.ok(0f);
            } else {
                return ServiceR.fail(e.getMessage());
            }
        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
        // 输出json格式的字符串回包
        Float score = respSim.getScore();
        return ServiceR.ok(score);
    }

    /**
     * 人脸相似度 推荐相似度大于等于70时可判断为同一人
     *
     * @param request
     * @return
     */
    public static ServiceR<Float> detectFaceSimilarity(FaceCompareVo request, int number) {
        if (number > 3) {
            return ServiceR.fail("图片错误，请更换图片后重试");
        }
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性
        // 以下代码示例仅供参考，建议采用更安全的方式来使用密钥
        // 请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(PropertiesConstants.SecretId(), PropertiesConstants.SecretKey());
        // 使用临时密钥示例
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("iai.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        IaiClient client = new IaiClient(cred, "ap-chongqing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DetectFaceSimilarityRequest req = new DetectFaceSimilarityRequest();

        if (StringUtils.isNotBlank(request.getImageA())) {
            req.setImageA(request.getImageA());
        }
        if (StringUtils.isNotBlank(request.getImageB())) {
            req.setImageB(request.getImageB());
        }
        if (StringUtils.isNotBlank(request.getUrlA())) {
            req.setUrlA(request.getUrlA());
        }
        if (StringUtils.isNotBlank(request.getUrlB())) {
            req.setUrlB(request.getUrlB());
        }
        // 返回的resp是一个DetectFaceSimilarityResponse的实例，与请求对象对应
        DetectFaceSimilarityResponse resp = null;
        try {
            resp = client.DetectFaceSimilarity(req);
        } catch (TencentCloudSDKException e) {
            if (e.getErrorCode().equals("FailedOperation.CompareFail")) {
                return ServiceR.ok(0f);
            } else if (e.getErrorCode().equals("FailedOperation.ImageSizeExceed")) {
                //压缩图片至5M以下
                FaceCompareVo requestNew = compressImage(request);
                return detectFaceSimilarity(requestNew, number + 1);
            } else if (e.getErrorCode().equals("FailedOperation.ImageResolutionExceed")) {
                FaceCompareVo requestNew = resolutionImage(request);
                return detectFaceSimilarity(requestNew, number + 1);
            } else if (e.getErrorCode().equals("FailedOperation.CompareFail")) {
                return ServiceR.ok(0f);
            } else {
                return ServiceR.fail(e.getMessage());
            }
        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
        // 输出json格式的字符串回包
        Float score = resp.getScore();
        return ServiceR.ok(score);
    }

    /*

   人脸检测与分析
     */
    public static ServiceR<DetectFaceResultVo> detectFace(FaceCompareVo request, int number) {
        if (number > 3) {
            return ServiceR.fail("图片错误，请更换图片后重试");
        }

        try {
            DetectFaceResultVo detectFaceResultVo = new DetectFaceResultVo();
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性
            // 以下代码示例仅供参考，建议采用更安全的方式来使用密钥
            // 请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(PropertiesConstants.SecretId(), PropertiesConstants.SecretKey());
            // 使用临时密钥示例
            // Credential cred = new Credential("SecretId", "SecretKey", "Token");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, "ap-chongqing", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DetectFaceRequest req = new DetectFaceRequest();
            if (StringUtils.isNotBlank(request.getImageA())) {
                req.setImage(request.getImageA());
            }
            if (StringUtils.isNotBlank(request.getUrlA())) {
                req.setUrl(request.getUrlA());
            }
            //返回的resp是一个DetectFaceResponse的实例，与请求对象对应
            req.setNeedFaceAttributes(1L);
            req.setNeedQualityDetection(1L);
            DetectFaceResponse resp = client.DetectFace(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
            detectFaceResultVo.setIsExistFace(resp.getFaceInfos() != null && resp.getFaceInfos().length > 0);
            detectFaceResultVo.setFaceCount(resp.getFaceInfos() != null ? resp.getFaceInfos().length : 0);
            detectFaceResultVo.setQuality(resp.getFaceInfos() != null && resp.getFaceInfos().length > 0 && resp.getFaceInfos()[0].getFaceQualityInfo() != null ? resp.getFaceInfos()[0].getFaceQualityInfo().getScore() : null);
            if (resp.getFaceInfos() != null && resp.getFaceInfos().length > 0 && resp.getFaceInfos()[0].getFaceAttributesInfo() != null && resp.getFaceInfos()[0].getFaceAttributesInfo().getGender() != null) {
                Long gender = resp.getFaceInfos()[0].getFaceAttributesInfo().getGender();
                //性别[0~49]为女性，[50，100]为男性，越接近0和100表示置信度越高
                Long maxGirl = 5L;
                Long maxBoy = 95L;
                if (gender.compareTo(maxGirl) <= 0) {
                    detectFaceResultVo.setGender("女");
                } else if (gender.compareTo(maxBoy) >= 0) {
                    detectFaceResultVo.setGender("男");
                }
            }

            return ServiceR.ok(detectFaceResultVo);
        } catch (TencentCloudSDKException e) {
            if ("InvalidParameterValue.NoFaceInPhoto".equals(e.getErrorCode())) {
                DetectFaceResultVo detectFaceResultVo = new DetectFaceResultVo();
                detectFaceResultVo.setFaceCount(0);
                detectFaceResultVo.setIsExistFace(false);
                return ServiceR.ok(detectFaceResultVo);
            } else if (e.getErrorCode().equals("FailedOperation.ImageSizeExceed")) {
                //压缩图片至5M以下
                FaceCompareVo requestNew = compressImage(request);
                return detectFace(requestNew, number + 1);
            } else if (e.getErrorCode().equals("FailedOperation.ImageResolutionExceed")) {
                FaceCompareVo requestNew = resolutionImage(request);
                return detectFace(requestNew, number + 1);
            } else {
                return ServiceR.fail(e.getMessage());
            }
        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }

    }

    public static ServiceR<EidTokenResulltVo> getEidResult(String eidToken) {

        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(PropertiesConstants.SecretId(), PropertiesConstants.SecretKey());
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("faceid.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        FaceidClient client = new FaceidClient(cred, "", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        GetEidResultRequest req = new GetEidResultRequest();
        req.setEidToken(eidToken);
        // 返回的resp是一个GetEidResultResponse的实例，与请求对象对应
        GetEidResultResponse resp = null;
        try {
            resp = client.GetEidResult(req);
        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
        log.info("GetEidResult返回结果:{}", JSON.toJSONString(resp));
        if (resp.getText() == null || resp.getText().getErrCode() == null) {
            return ServiceR.fail("认证失败");
        }

        if (resp.getText().getErrCode() != 0) {
            return ServiceR.fail(getSimple(resp.getText().getErrMsg()));
        }

        if (resp.getText().getComparestatus() == null || resp.getText().getComparestatus() != 0) {
            return ServiceR.fail(getSimple(resp.getText().getComparemsg()));
        }
        EidTokenResulltVo eidTokenResulltVo = new EidTokenResulltVo();
        eidTokenResulltVo.setCompareSim(resp.getText().getSim());

        if (StringUtils.isNotBlank(resp.getText().getSim())) {
            BigDecimal sim = new BigDecimal(resp.getText().getSim());
            if (sim.compareTo(new BigDecimal("70")) < 0) {
                return ServiceR.fail("人脸对比相似度过低");
            }
        }
        if (StringUtils.isNotBlank(resp.getText().getExtra())) {
            log.info("extra信息：{}", resp.getText().getExtra());
            String[] extra = resp.getText().getExtra().split(",");
            if (extra.length >= 2) {
                eidTokenResulltVo.setName(extra[0]);
                eidTokenResulltVo.setIdCard(extra[1]);
            }
        }
        if (StringUtils.isNotBlank(resp.getText().getOcrBirth())) {
            log.info("生日:{}", resp.getText().getOcrBirth());
            eidTokenResulltVo.setBirthDate(DateUtils.dateTime("yyyy/MM/dd", resp.getText().getOcrBirth()));
        } else if (StringUtils.isNotBlank(eidTokenResulltVo.getIdCard())) {
            log.info("没取到生日用身份证号");
            String idCard = eidTokenResulltVo.getIdCard();
            eidTokenResulltVo.setBirthDate(DateUtils.dateTime("yyyyMMdd", idCard.substring(6, 14)));
        }

        if (StringUtils.isNotBlank(resp.getText().getOcrGender())) {
            eidTokenResulltVo.setSex(resp.getText().getOcrGender());
        }

        // 根据eidtoken获取验证时的最佳帧照片并存储
        // E证通服务器集成流程：https://cloud.tencent.com/document/product/1007/108010
        // 获取E证通结果信息：https://cloud.tencent.com/document/product/1007/54090

        if (resp.getBestFrame() == null || StringUtils.isBlank(resp.getBestFrame().getBestFrame())) {
            return ServiceR.fail("未获得人脸识别最佳帧");
        }
        String bestFrame = resp.getBestFrame().getBestFrame();
        // 存储最佳帧
        eidTokenResulltVo.setBestFrame(bestFrame);

        return ServiceR.ok(eidTokenResulltVo);
    }

    private static String getSimple(String message) {
        if (StringUtils.isNotBlank(message)) {
            return message.substring(0, message.indexOf(":") > 0 ? message.indexOf("：") : message.indexOf("("));
        }
        return "";
    }

    private static FaceCompareVo compressImage(FaceCompareVo request) {

        FaceCompareVo requestNew = new FaceCompareVo();
        if (StringUtils.isNotBlank(request.getUrlA())) {
            try {
                String base64A = ImageCompressor.compressImageToBase64(request.getUrlA());
                requestNew.setImageA(base64A);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getUrlB())) {
            try {
                String base64B = ImageCompressor.compressImageToBase64(request.getUrlB());
                requestNew.setImageB(base64B);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getImageA())) {
            try {
                String base64A = ImageCompressor.compressBase64Image(request.getImageA());
                requestNew.setImageA(base64A);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getImageB())) {
            try {
                String base64B = ImageCompressor.compressBase64Image(request.getImageB());
                requestNew.setImageB(base64B);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return requestNew;
    }

    private static FaceCompareVo resolutionImage(FaceCompareVo request) {

        FaceCompareVo requestNew = new FaceCompareVo();
        if (StringUtils.isNotBlank(request.getUrlA())) {
            try {
                String base64A = ImageResolution.compressImageToBase64ByResolution(request.getUrlA());
                requestNew.setImageA(base64A);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getUrlB())) {
            try {
                String base64B = ImageResolution.compressImageToBase64ByResolution(request.getUrlB());
                requestNew.setImageB(base64B);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getImageA())) {
            try {
                String base64A = ImageResolution.compressBase64ImageByResolution(request.getImageA());
                requestNew.setImageA(base64A);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (StringUtils.isNotBlank(request.getImageB())) {
            try {
                String base64B = ImageResolution.compressBase64ImageByResolution(request.getImageB());
                requestNew.setImageB(base64B);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return requestNew;
    }

}

