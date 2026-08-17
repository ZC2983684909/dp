package com.wxmblog.yanjian.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.utils.SecurityUtils;
import com.wxmblog.yanjian.common.constant.PropertiesConstants;
import com.wxmblog.yanjian.common.rest.request.front.area.LocationResponse;
import com.wxmblog.yanjian.common.rest.vo.UserLocationVo;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

@Slf4j
public class LocationAnalysisUtils {

    public static String calcAuthorization(String secretId, String secretKey, String datetime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
        String sig = new BASE64Encoder().encode(hash);

        String auth = "{\"id\":\"" + secretId + "\", \"x-date\":\"" + datetime + "\", \"signature\":\"" + sig + "\"}";
        return auth;
    }

    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }

    public static LocationResponse getLocation(String longitude, String latitude) {

        LocationResponse locationResponse = new LocationResponse();
        StringBuilder result = new StringBuilder();
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = null;
        try {
            auth = calcAuthorization(PropertiesConstants.locationAnalysisSecretID(), PropertiesConstants.locationAnalysisSecretKey(), datetime);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }
        // 请求方法
        String method = "POST";
        // 请求头
        String uuid = UUID.randomUUID().toString();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("request-id", uuid);
        headers.put("Authorization", auth);

        // 查询参数
        Map<String, String> queryParams = new HashMap<>();

        // body参数
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("batch", "");
        bodyParams.put("callback", "");
        bodyParams.put("extensions", "");
        bodyParams.put("homeorcorp", "");
        bodyParams.put("location", longitude + "," + latitude);
        bodyParams.put("output", "");
        bodyParams.put("poitype", "");
        bodyParams.put("radius", "");
        bodyParams.put("roadlevel", "");
        String bodyParamStr = null;
        try {
            bodyParamStr = urlencode(bodyParams);
        } catch (UnsupportedEncodingException e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }

        // url参数拼接
        String url = null;
        try {
            url = "https://ap-guangzhou.cloudmarket-apigw.com/service-jfc4ssy4/geocode/regeo/query" + "?" + urlencode(queryParams);
        } catch (UnsupportedEncodingException e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        }

        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // request body
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(bodyParamStr);
                out.flush();
                out.close();
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(result.toString());
            if (jsonObject.getInteger("code") == null || jsonObject.getInteger("code") != 200 || !Boolean.TRUE.equals(jsonObject.getBoolean("success"))) {
                throw new JrsfException(BaseExceptionEnum.API_ERROR).setMsg(jsonObject.getString("msg"));
            }
            JSONObject data = jsonObject.getJSONObject("data").getJSONArray("regeocodes").getJSONObject(0);
            String province = data.getJSONObject("addressComponent").getString("province");
            locationResponse.setProvince("[]".equals(province) ? null : province);
            Object city = data.getJSONObject("addressComponent").get("city");
            if (city instanceof String && StringUtils.isNotBlank(city.toString())) {
                String cityStr = data.getJSONObject("addressComponent").getString("city");
                locationResponse.setCity("[]".equals(cityStr) ? null : cityStr);
            }
            String county = data.getJSONObject("addressComponent").getString("district");
            locationResponse.setCounty("[]".equals(county) ? null : county);
            String township = data.getJSONObject("addressComponent").getString("township");
            locationResponse.setTownship("[]".equals(township) ? null : township);
            String address = data.getString("formatted_address");
            locationResponse.setAddress("[]".equals(address) ? null : address);
            locationResponse.setLon(longitude);
            locationResponse.setLat(latitude);
            if (StringUtils.isBlank(locationResponse.getCity())) {
                locationResponse.setCity(locationResponse.getProvince());
            }

        } catch (Exception e) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return locationResponse;
    }

    //UserLocationVo
    public static UserLocationVo getUserHeadLocation() {
        String location = SecurityUtils.getHeader("coordinates");
        if (StringUtils.isNotBlank(location)) {
            String[] split = location.split(",");
            String lon = split[0];
            String lat = split[1];
            UserLocationVo userLocationVo = new UserLocationVo();
            userLocationVo.setLon(lon);
            userLocationVo.setLat(lat);
            return userLocationVo;
        }
        return null;
    }

    private static final double EARTH_RADIUS_KM = 6371.0; // 地球半径，单位：公里
    private static final double METER_PER_KILOMETER = 1000.0; // 每公里的米数

    /**
     * 计算两个经纬度之间的距离，返回单位为米
     *
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离，单位：米
     */
    public static double calculateDistanceInMeters(String lon1, String lat1, String lon2, String lat2) {
        // 将角度转换为弧度
        double lat1Rad = Math.toRadians(Double.parseDouble(lat1));
        double lon1Rad = Math.toRadians(Double.parseDouble(lon1));
        double lat2Rad = Math.toRadians(Double.parseDouble(lat2));
        double lon2Rad = Math.toRadians(Double.parseDouble(lon2));

        // 计算差值
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine公式
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离（公里），然后转换为米
        double distance = EARTH_RADIUS_KM * c * METER_PER_KILOMETER;
        BigDecimal distanceBD = new BigDecimal(distance);
        return distanceBD.setScale(2, RoundingMode.DOWN).doubleValue();
    }


    /*
    //‌广州‌
       //‌深圳
   2	杭州
   4	武汉	、
   5	苏州
               6	西安	"一带一路"文旅中心
   7	南京	高校科研资源密集
   8	长沙	工程机械之都
   9	郑州	航空港实验区核心
   10	天津	港口经济转型
   11	合肥	量子科技/新能源车
   12	青岛	海洋经济+外资总部
   13	东莞	电子信息代工转型
   14	宁波	港口吞吐量全球第一
   15	佛山

       无锡‌（制造业单项冠军多）
               ‌大连‌（东北开放门户）
               ‌厦门‌（计划单列市，但产业单一）
               ‌昆明‌（东南亚桥头堡）
               ‌沈阳‌（东北老工业基地转型）*/
    private static final List<String> MAIN_CITY_COUNTY = new ArrayList<String>() {{
        add("重庆市渝中区");
        add("重庆市大渡口区");
        add("重庆市江北区");
        add("重庆市沙坪坝区");
        add("重庆市九龙坡区");
        add("重庆市南岸区");
        add("重庆市北碚区");
        add("重庆市渝北区");
        add("重庆市巴南区");
        add("重庆市两江新区");
        add("成都市锦江区");
        add("成都市青羊区");
        add("成都市金牛区");
        add("成都市武侯区");
        add("成都市成华区");
        add("成都市龙泉驿区");
        add("成都市青白江区");
        add("成都市新都区");
        add("成都市温江区");
        add("成都市双流区");
        add("成都市郫都区");
        add("成都市新津区");
        add("北京市东城区");
        add("北京市西城区");
        add("北京市海淀区");
        add("北京市朝阳区");
        add("北京市丰台区");
        add("北京市石景山区");
        add("北京市通州区");
        add("上海市黄浦区");
        add("上海市徐汇区");
        add("上海市长宁区");
        add("上海市静安区");
        add("上海市普陀区");
        add("上海市虹口区");
        add("上海市杨浦区");
        add("上海市浦东新区");
    }};

    public static Boolean isMainCity(String city, String county) {

        long count = MAIN_CITY_COUNTY.stream().filter(p -> p.contains(city)).count();
        if (count == 0) {
            //该城市还未收录 暂时默认为主城区
            return true;
        }
        return MAIN_CITY_COUNTY.contains(city + county);
    }
}
