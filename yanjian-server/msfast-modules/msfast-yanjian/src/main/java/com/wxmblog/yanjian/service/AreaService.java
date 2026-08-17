package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.area.LocationResponse;
import com.wxmblog.yanjian.common.rest.response.front.area.AreaResponse;
import com.wxmblog.yanjian.entity.AreaEntity;

import java.util.List;


/**
 * 地区
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2022-12-26 13:40:17
 */
public interface AreaService extends IService<AreaEntity> {

    List<AreaResponse> province();

    List<AreaResponse> sonArea(String parentCode);

    List<AreaResponse> allCity();

    ServiceR<LocationResponse> getLocationAnalysis(String longitude, String latitude);

    ServiceR<List<String>> getPoster();
}

