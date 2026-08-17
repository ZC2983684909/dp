package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.utils.SecurityUtils;
import com.wxmblog.base.common.utils.ServletUtils;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.area.LocationResponse;
import com.wxmblog.yanjian.common.rest.request.front.user.LocationRequest;
import com.wxmblog.yanjian.common.rest.response.front.area.AreaResponse;
import com.wxmblog.yanjian.common.rest.vo.UserLocationVo;
import com.wxmblog.yanjian.common.utils.LocationAnalysisUtils;
import com.wxmblog.yanjian.dao.AreaDao;
import com.wxmblog.yanjian.entity.AreaEntity;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.service.AreaService;
import com.wxmblog.yanjian.service.TUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service("areaService")
public class AreaServiceImpl extends ServiceImpl<AreaDao, AreaEntity> implements AreaService {

    @Autowired
    TUserService tUserService;

    @Autowired
    MsfConfigService msfConfigService;

    @Value("${wxmfast.config.locationAnalysis.enabled:true}")
    private boolean locationAnalysisEnabled;

    @Override
    public List<AreaResponse> province() {
        Map<String, Object> param = new HashMap<>();
        param.put("province", true);
        return this.baseMapper.province(param);
    }

    @Override
    public List<AreaResponse> sonArea(String parentCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("parentCode", parentCode);
        return this.baseMapper.province(param);
    }

    @Override
    public List<AreaResponse> allCity() {

        List<AreaResponse> allCity = new ArrayList<>();
        Wrapper<AreaEntity> wrapper = new QueryWrapper<AreaEntity>().lambda().eq(AreaEntity::getParentCode, "0").orderByAsc(AreaEntity::getSort);
        List<AreaEntity> list = this.list(wrapper);
        List<String> ids = list.stream().map(AreaEntity::getRegionCode).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(ids)) {
            Wrapper<AreaEntity> wrapper1 = new QueryWrapper<AreaEntity>().lambda().in(AreaEntity::getParentCode, ids);
            List<AreaEntity> list1 = this.list(wrapper1);
            for (AreaEntity entity : list) {
                AreaResponse areaResponse = new AreaResponse();
                BeanUtils.copyProperties(entity, areaResponse);

                List<AreaResponse> children = new ArrayList<>();
                List<AreaEntity> childrenList = list1.stream().filter(item -> item.getParentCode().equals(entity.getRegionCode())).collect(Collectors.toList());
                for (AreaEntity childrenEntity : childrenList) {
                    AreaResponse childrenResponse = new AreaResponse();
                    BeanUtils.copyProperties(childrenEntity, childrenResponse);
                    children.add(childrenResponse);
                }
                areaResponse.setChildren(children);
                allCity.add(areaResponse);
            }
        }
        return allCity;
    }

    @Override
    public ServiceR<LocationResponse> getLocationAnalysis(String longitude, String latitude) {

        LocationResponse locationResponse;
        if (locationAnalysisEnabled) {
            locationResponse = LocationAnalysisUtils.getLocation(longitude, latitude);
        } else {
            // 本地开发跳过第三方地图服务，只保留坐标，避免依赖付费密钥。
            locationResponse = new LocationResponse();
            locationResponse.setLon(longitude);
            locationResponse.setLat(latitude);
            locationResponse.setIsMove(true);
        }
        LocationRequest locationRequest = new LocationRequest();
        BeanUtils.copyProperties(locationResponse, locationRequest);
        locationRequest.setUserId(TokenUtils.getOwnerId());
        if (locationAnalysisEnabled && StringUtils.isNotBlank(locationRequest.getUserId())) {
            tUserService.updateLocation(locationRequest);
        }
        if (locationAnalysisEnabled) {
            locationResponse.setIsMove(true);
        }
        UserLocationVo userLocationVo = LocationAnalysisUtils.getUserHeadLocation();
        if (userLocationVo != null) {
            double distance = LocationAnalysisUtils.calculateDistanceInMeters(userLocationVo.getLon(), userLocationVo.getLat(), longitude, latitude);
            if (distance < 500) {
                locationResponse.setIsMove(false);
            }
        }
        return ServiceR.ok(locationResponse);
    }

    @Override
    public ServiceR<List<String>> getPoster() {

        String poster = msfConfigService.getValueByCode("poster");
        if (StringUtils.isNotBlank(poster)) {

            if (StringUtils.isBlank(TokenUtils.getOwnerId())) {
                JSONObject jsonObject = JSONObject.parseObject(poster);
                JSONArray list = jsonObject.getJSONArray("boy");
                return ServiceR.ok(list.toJavaList(String.class));
            }

            TUserEntity user = this.tUserService.getById(TokenUtils.getOwnerId());
            if (user != null) {
                JSONObject jsonObject = JSONObject.parseObject(poster);
                JSONArray list;
                if ("男".equals(user.getSex())) {
                    list = jsonObject.getJSONArray("boy");
                } else {
                    list = jsonObject.getJSONArray("girl");
                }
                return ServiceR.ok(list.toJavaList(String.class));
            }
        }
        return ServiceR.ok();
    }
}
