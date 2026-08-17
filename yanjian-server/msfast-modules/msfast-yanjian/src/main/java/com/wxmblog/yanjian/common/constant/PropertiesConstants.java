package com.wxmblog.yanjian.common.constant;

import com.wxmblog.base.common.utils.SpringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
//@RefreshScope
public class PropertiesConstants {

    @Value("${wx.template}")
    private String Wx_Template;

    @Value("${tencent.secretId}")
    private String secretId;

    @Value("${tencent.secretKey}")
    private String secretKey;

    @Value("${locationAnalysis.SecretID}")
    private String locationAnalysisSecretID;

    @Value("${locationAnalysis.SecretKey}")
    private String locationAnalysisSecretKey;

    @Value("${wx.qrCodeEnvVersion:release}")
    private String qrCodeEnvVersion;


    public static String Wx_Template() {
        return SpringUtils.getBean(PropertiesConstants.class).getWx_Template();
    }

    public static String SecretId() {
        return SpringUtils.getBean(PropertiesConstants.class).getSecretId();
    }

    public static String SecretKey() {
        return SpringUtils.getBean(PropertiesConstants.class).getSecretKey();
    }

    public static String locationAnalysisSecretID() {
        return SpringUtils.getBean(PropertiesConstants.class).getLocationAnalysisSecretID();
    }

    public static String locationAnalysisSecretKey() {
        return SpringUtils.getBean(PropertiesConstants.class).getLocationAnalysisSecretKey();
    }

    public static String qrCodeEnvVersion() {
        return SpringUtils.getBean(PropertiesConstants.class).getQrCodeEnvVersion();
    }
}

