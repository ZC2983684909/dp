package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxmblog.base.auth.authority.service.WxAppletService;
import com.wxmblog.base.auth.authority.service.Wxh5Service;
import com.wxmblog.base.auth.common.rest.response.WxH5UserInfoResponse;
import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.area.LocationResponse;
import com.wxmblog.yanjian.common.rest.response.front.area.AreaResponse;
import com.wxmblog.yanjian.common.utils.XmlToMapUtils;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.entity.WechatPublicBindEntity;
import com.wxmblog.yanjian.service.AreaService;
import com.wxmblog.yanjian.service.UserProfileService;
import com.wxmblog.yanjian.service.WechatPublicBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 地区
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2022-12-26 13:40:17
 */
@RestController
@RequestMapping("yanjian/area")
@Api(tags = "地区")
@Slf4j
public class AreaController {

    @Autowired
    private AreaService areaService;


    @Value("${wx.messageToken}")
    private String messageToken;

    @Value("${wx.EncodingAESKey}")
    private String EncodingAESKey;

    @Value("${wxpublic.messageToken}")
    private String wxpublicMessageToken;

    @Value("${wxpublic.EncodingAESKey}")
    private String wxpublicEncodingAESKey;

    @Autowired
    RestTemplate restTemplate;

    @Resource
    private RedisService redisService;

    @Autowired
    private WxAppletService wxAppletService;

    @Autowired
    private Wxh5Service wxh5Service;

    @Value("${weburl}")
    private String weburl;

    @Autowired
    private WechatPublicBindService wechatPublicBindService;

    @Autowired
    private UserProfileService userProfileService;


    @ApiOperation("查询省级地区")
    @ApiOperationSort(value = 1)
    @GetMapping("/province")
    @AuthIgnore
    public R<List<AreaResponse>> province() {
        return R.ok(areaService.province());
    }

    @ApiOperation("查询下级地区")
    @ApiOperationSort(value = 2)
    @GetMapping("/son")
    @AuthIgnore
    public R<List<AreaResponse>> sonArea(@RequestParam String parentCode) {
        return R.ok(areaService.sonArea(parentCode));
    }

    @ApiOperation("查询所有城市")
    @ApiOperationSort(value = 3)
    @GetMapping("/allCity")
    @AuthIgnore
    public R<List<AreaResponse>> allCity() {
        return R.ok(areaService.allCity());
    }


    @ApiOperation("查询微信消息")
    @ApiOperationSort(value = 4)
    @GetMapping("/getWxMessage")
    @AuthIgnore
    public String getWxMessage(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr) {
        //token、timestamp、nonce三个参数进行字典序排序
        List<String> words = new ArrayList<>();
        words.add(messageToken);
        words.add(timestamp);
        words.add(nonce);
        Collections.sort(words);

        String s = String.join("", words);
        StringBuilder hexString = new StringBuilder();
        //sha1计算签名
        try {
            // 获取 SHA-1 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 计算输入字符串的哈希值
            byte[] hashBytes = md.digest(s.getBytes());

            // 将字节数组转换为十六进制字符串

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if (signature.contentEquals(hexString)) {
            //发送微信消息
            return echostr;
        }

        return "";

    }


    @ApiOperation("接收微信推送的消息")
    @ApiOperationSort(value = 5)
    @PostMapping("/getWxMessage")
    @AuthIgnore
    public String postMessage(HttpServletRequest request, HttpServletResponse response) {

        // 获取所有查询参数的名称
        Map<String, String> notifyMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            notifyMap.put(paramName, paramValue);
        }
        StringBuilder requestBody = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 假设请求体是 JSON 格式
        String jsonBody = requestBody.toString();
        log.info("接收微信推送的消息数据:{},body:{}", JSONObject.toJSONString(notifyMap), jsonBody);

        List<String> words = new ArrayList<>();
        words.add(messageToken);
        words.add(notifyMap.get("timestamp"));
        words.add(notifyMap.get("nonce"));
        Collections.sort(words);

        String s = String.join("", words);
        StringBuilder hexString = new StringBuilder();
        //sha1计算签名
        try {
            // 获取 SHA-1 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 计算输入字符串的哈希值
            byte[] hashBytes = md.digest(s.getBytes());

            // 将字节数组转换为十六进制字符串

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            log.info("微信消息sha1计算签名失败{}", e.getMessage());
        }

        if (!notifyMap.get("signature").contentEquals(hexString)) {
            //发送微信消息
            log.info("微信消息验证失败");
            return "fail";
        }


        //发送消息
        Map<String, Object> param = new HashMap<>();
        JSONObject jsonObjectBody = JSONObject.parseObject(jsonBody);

        if ("user_enter_tempsession".equals(jsonObjectBody.getString("Event"))) {
            String touser = jsonObjectBody.getString("FromUserName");
            Long expire = redisService.getExpire("sendmessage:" + touser);
            if (expire.compareTo(0L) <= 0) {
            param.put("touser", touser);
            param.put("msgtype", "text");
            Map<String, Object> content = new HashMap<>();
            content.put("content", "1. <a href=\"" + weburl + "/pages/sponsorshipPlatform/sponsorshipPlatform\">赞助平台</a>" + "\n\n2. <a href=\"" + weburl + "/pages/applayNum/applayNum\">充值颜币</a>" + "\n\n2. <a href=\"" + weburl + "/pages/openVip/openVip\">开通续费VIP</a>" + "\n\n请点击对应的蓝色项目链接");
            param.put("text", content);
            String accessTokenBucket = wxAppletService.getAccessToken();
            String result = restTemplate.postForObject("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessTokenBucket, param, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer errcode = jsonObject.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                log.error(jsonObject.getString("errmsg"));
            } else {
                redisService.setCacheObject("sendmessage:" + touser, touser, 10L, TimeUnit.MINUTES);
            }

            }
        } else {
            log.info("小程序微信消息:{}", jsonBody);
        }

        return "success";
    }


    @ApiOperation("查询服务号微信消息")
    @ApiOperationSort(value = 4)
    @GetMapping("/getPublicWxMessage")
    @AuthIgnore
    public String getPublicWxMessage(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr) {
        //token、timestamp、nonce三个参数进行字典序排序
        List<String> words = new ArrayList<>();
        words.add(wxpublicMessageToken);
        words.add(timestamp);
        words.add(nonce);
        Collections.sort(words);

        String s = String.join("", words);
        StringBuilder hexString = new StringBuilder();
        //sha1计算签名
        try {
            // 获取 SHA-1 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 计算输入字符串的哈希值
            byte[] hashBytes = md.digest(s.getBytes());

            // 将字节数组转换为十六进制字符串

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if (signature.contentEquals(hexString)) {
            //发送微信消息
            return echostr;
        }

        return "";

    }


    @ApiOperation("接收服务号推送的消息")
    @ApiOperationSort(value = 5)
    @PostMapping("/getPublicWxMessage")
    @Transactional
    @AuthIgnore
    public String postPublicWxMessage(HttpServletRequest request, HttpServletResponse response) {

        // 获取所有查询参数的名称
        Map<String, String> notifyMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            notifyMap.put(paramName, paramValue);
        }
        StringBuilder requestBody = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 假设请求体是 JSON 格式
        String jsonBody = requestBody.toString();
        log.info("接收微信推送的消息数据:{},body:{}", JSONObject.toJSONString(notifyMap), jsonBody);

        List<String> words = new ArrayList<>();
        words.add(wxpublicMessageToken);
        words.add(notifyMap.get("timestamp"));
        words.add(notifyMap.get("nonce"));
        Collections.sort(words);

        String s = String.join("", words);
        StringBuilder hexString = new StringBuilder();
        //sha1计算签名
        try {
            // 获取 SHA-1 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 计算输入字符串的哈希值
            byte[] hashBytes = md.digest(s.getBytes());

            // 将字节数组转换为十六进制字符串

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            log.info("微信消息sha1计算签名失败{}", e.getMessage());
        }

        if (!notifyMap.get("signature").contentEquals(hexString)) {
            //发送微信消息
            log.info("微信消息验证失败");
            return "fail";
        }

        Map<String, String> xmlMap = XmlToMapUtils.xmlToMap(requestBody.toString());
        // 处理微信事件
        String event = xmlMap.get("Event");
        if ("subscribe".equals(event)) {
            // 处理关注事件
            String openid = notifyMap.get("openid");
            ServiceR<WxH5UserInfoResponse> ret = wxh5Service.getUserInfoByOpenId(openid);
            if (ServiceR.isError(ret)) {
                log.info("获取微信用户信息失败:{}", ret.getMsg());
                return "fail";
            }
            WxH5UserInfoResponse wxH5UserInfoResponse = ret.getData();
            if (StringUtils.isNotBlank(wxH5UserInfoResponse.getUnionId())) {

                Wrapper<WechatPublicBindEntity> queryBindWrapper = new QueryWrapper<WechatPublicBindEntity>().lambda()
                        .eq(WechatPublicBindEntity::getOpenId, openid)
                        .orderByDesc(WechatPublicBindEntity::getCreateTime)
                        .last("limit 1");
                WechatPublicBindEntity wechatPublicBindEntity = wechatPublicBindService.getOne(queryBindWrapper);
                if (wechatPublicBindEntity == null) {
                    wechatPublicBindEntity = new WechatPublicBindEntity();
                    wechatPublicBindEntity.setOpenId(openid);
                    wechatPublicBindEntity.setUnionId(wxH5UserInfoResponse.getUnionId());
                    wechatPublicBindService.save(wechatPublicBindEntity);
                }

                Wrapper<UserProfileEntity> updateWrapper = new UpdateWrapper<UserProfileEntity>().lambda()
                        .eq(UserProfileEntity::getUnionId, wxH5UserInfoResponse.getUnionId())
                        .set(UserProfileEntity::getPublicOpenId, wxH5UserInfoResponse.getOpenId());
                userProfileService.update(updateWrapper);
            } else {
                log.info("微信用户信息无unionId");
            }
        } else {
            log.info("微信事件:{},内容:{}", event, JSON.toJSONString(xmlMap));
        }

        return "success";
    }

    @ApiOperation("经纬度解析")
    @ApiOperationSort(value = 6)
    @GetMapping("/locationAnalysis")
    @AuthIgnore
    public R<LocationResponse> locationAnalysis(@RequestParam String longitude, @RequestParam String latitude) {

        ServiceR<LocationResponse> ret = areaService.getLocationAnalysis(longitude, latitude);
        if (ServiceR.isError(ret)) {
            return R.fail(ret.getMsg());
        }
        return R.ok(ret.getData());
    }

    @ApiOperation("查询海报")
    @ApiOperationSort(value = 7)
    @GetMapping("/getPoster")
    @AuthIgnore
    public R<List<String>> getposter() {
        ServiceR<List<String>> ret = areaService.getPoster();
        if (ServiceR.isError(ret)) {
            return R.fail(ret.getMsg());
        }
        return R.ok(ret.getData());
    }
}
